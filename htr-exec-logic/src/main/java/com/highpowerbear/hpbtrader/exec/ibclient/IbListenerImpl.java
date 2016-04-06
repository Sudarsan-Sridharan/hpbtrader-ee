package com.highpowerbear.hpbtrader.exec.ibclient;

import com.highpowerbear.hpbtrader.exec.common.SingletonRepo;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.AbstractIbListener;
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
    private HeartbeatControl heartbeatControl = SingletonRepo.getInstance().getHeartbeatControl();

    private IbAccount ibAccount;

    public IbListenerImpl(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        
        if (!(  HtrEnums.IbOrderStatus.SUBMITTED.name().equalsIgnoreCase(status) ||
                HtrEnums.IbOrderStatus.CANCELLED.name().equalsIgnoreCase(status) ||
                HtrEnums.IbOrderStatus.FILLED.name().equalsIgnoreCase(status)))
        {
            return;
        }
        IbOrder ibOrder = ibOrderDao.getIbOrderByIbPermId(ibAccount, permId);
        if (ibOrder == null) {
            return;
        }

        if (HtrEnums.IbOrderStatus.SUBMITTED.name().equalsIgnoreCase(status) && HtrEnums.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.heartbeatReceived(ibOrder);
        } else if (HtrEnums.IbOrderStatus.SUBMITTED.name().equalsIgnoreCase(status) && !HtrEnums.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.heartbeatReceived(ibOrder);
            //orderStateHandler.orderSubmitted(ibOrder, HtrUtil.getCalendar());
        } else if (HtrEnums.IbOrderStatus.CANCELLED.name().equalsIgnoreCase(status) && !HtrEnums.IbOrderStatus.CANCELLED.equals(ibOrder.getStatus())) {
            heartbeatControl.removeHeartbeat(ibOrder);
            //orderStateHandler.orderCanceled(ibOrder, HtrUtil.getCalendar());
        } else if (HtrEnums.IbOrderStatus.FILLED.name().equalsIgnoreCase(status) && remaining == 0 && !HtrEnums.IbOrderStatus.FILLED.equals(ibOrder.getStatus())) {
            heartbeatControl.removeHeartbeat(ibOrder);
            //orderStateHandler.orderFilled(ibOrder, HtrUtil.getCalendar(), avgFillPrice);
        }
    }

    @Override
    public void openOrder(int orderId, Contract contract, com.ib.client.Order order, OrderState orderState) {
        super.openOrder(orderId, contract, order, orderState);
        
        IbOrder dbIbOrder = ibOrderDao.getIbOrderByIbOrderId(ibAccount, orderId);
        if (dbIbOrder != null && dbIbOrder.getIbPermId() == null) {
            dbIbOrder.setIbPermId(order.m_permId);
            ibOrderDao.updateIbOrder(dbIbOrder);
        }
    }

    @Override
    public void nextValidId(int orderId) {
        super.nextValidId(orderId);
        ibController.setNextValidOrderId(ibAccount, orderId);
    }
}
