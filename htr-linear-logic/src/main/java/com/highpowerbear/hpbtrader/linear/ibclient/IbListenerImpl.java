package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.SingletonRepo;
import com.highpowerbear.hpbtrader.linear.strategy.OrderStateHandler;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.AbstractIbListener;
import com.highpowerbear.hpbtrader.shared.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.ib.client.Contract;
import com.ib.client.OrderState;

/**
 *
 * @author rkolar
 */
public class IbListenerImpl extends AbstractIbListener {
    private IbOrderDao ibOrderDao = SingletonRepo.getInstance().getIbOrderDao();
    private IbController ibController = SingletonRepo.getInstance().getIbController();
    private OrderStateHandler orderStateHandler = SingletonRepo.getInstance().getOrderStateHandler();
    private HeartbeatControl heartbeatControl = SingletonRepo.getInstance().getHeartbeatControl();
    private EventBroker eventBroker = SingletonRepo.getInstance().getEventBroker();

    private IbAccount ibAccount;

    public IbListenerImpl(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        
        if (!(  IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status)))
        {
            return;
        }
        IbOrder ibOrder = ibOrderDao.getIbOrderByIbPermId(ibAccount, permId);
        if (ibOrder == null) {
            return;
        }

        if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && HtrEnums.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.heartbeatReceived(ibOrder);
            eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
        } else if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && !HtrEnums.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.heartbeatReceived(ibOrder);
            orderStateHandler.orderSubmitted(ibOrder, HtrUtil.getCalendar());
        } else if (IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) && !HtrEnums.IbOrderStatus.CANCELED.equals(ibOrder.getStatus())) {
            heartbeatControl.removeHeartbeat(ibOrder);
            orderStateHandler.orderCanceled(ibOrder, HtrUtil.getCalendar());
        } else if (IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status) && remaining == 0 && !HtrEnums.IbOrderStatus.FILLED.equals(ibOrder.getStatus())) {
            heartbeatControl.removeHeartbeat(ibOrder);
            orderStateHandler.orderFilled(ibOrder, HtrUtil.getCalendar(), avgFillPrice);
        }
    }

    @Override
    public void openOrder(int orderId, Contract contract, com.ib.client.Order order, OrderState orderState) {
        super.openOrder(orderId, contract, order, orderState);
        
        IbOrder dbIbOrder = ibOrderDao.getIbOrderByIbOrderId(ibAccount, orderId);
        if (dbIbOrder != null && dbIbOrder.getIbPermId() == null) {
            dbIbOrder.setIbPermId(order.m_permId);
            ibOrderDao.updateIbOrder(dbIbOrder);
            eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
        }
    }

    @Override
    public void nextValidId(int orderId) {
        super.nextValidId(orderId);
        ibController.setNextValidOrderId(ibAccount, orderId);
    }
}
