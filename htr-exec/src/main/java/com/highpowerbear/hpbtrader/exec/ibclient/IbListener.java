package com.highpowerbear.hpbtrader.exec.ibclient;

import com.highpowerbear.hpbtrader.exec.message.MqSender;
import com.highpowerbear.hpbtrader.exec.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.GenerictIbListener;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.ib.client.Contract;
import com.ib.client.OrderState;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author rkolar
 */
@Dependent
public class IbListener extends GenerictIbListener {

    @Inject private IbOrderDao ibOrderDao;
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private MqSender mqSender;
    @Inject private WebsocketController websocketController;

    private IbAccount ibAccount;

    public IbListener configure(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
        return this;
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        
        if (!(  HtrEnums.IbOrderStatus.SUBMITTED.name().equalsIgnoreCase(status) ||
                HtrEnums.IbOrderStatus.PRESUBMITTED.name().equalsIgnoreCase(status) ||
                HtrEnums.IbOrderStatus.CANCELLED.name().equalsIgnoreCase(status) ||
                HtrEnums.IbOrderStatus.FILLED.name().equalsIgnoreCase(status)))
        {
            return;
        }
        IbOrder ibOrder = ibOrderDao.getIbOrderByIbPermId(ibAccount, permId);
        if (ibOrder == null) {
            return;
        }

        if ((HtrEnums.IbOrderStatus.SUBMITTED.name().equalsIgnoreCase(status) || HtrEnums.IbOrderStatus.PRESUBMITTED.name().equalsIgnoreCase(status)) && HtrEnums.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            heartbeatControl.initHeartbeat(ibOrder);

        } else if (HtrEnums.IbOrderStatus.SUBMITTED.name().equalsIgnoreCase(status) && !HtrEnums.IbOrderStatus.SUBMITTED.equals(ibOrder.getStatus())) {
            ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMITTED, HtrUtil.getCalendar());
            heartbeatControl.initHeartbeat(ibOrder);
            orderStateChanged(ibOrder);

        } else if (HtrEnums.IbOrderStatus.CANCELLED.name().equalsIgnoreCase(status) && !HtrEnums.IbOrderStatus.CANCELLED.equals(ibOrder.getStatus())) {
            ibOrder.addEvent(HtrEnums.IbOrderStatus.CANCELLED, HtrUtil.getCalendar());
            heartbeatControl.removeHeartbeat(ibOrder);
            orderStateChanged(ibOrder);

        } else if (HtrEnums.IbOrderStatus.FILLED.name().equalsIgnoreCase(status) && remaining == 0 && !HtrEnums.IbOrderStatus.FILLED.equals(ibOrder.getStatus())) {
            ibOrder.setFillPrice(avgFillPrice);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.FILLED, HtrUtil.getCalendar());
            heartbeatControl.removeHeartbeat(ibOrder);
            orderStateChanged(ibOrder);
        }
    }

    private void orderStateChanged(IbOrder ibOrder) {
        ibOrderDao.updateIbOrder(ibOrder);
        mqSender.notifyOrderStateChanged(ibOrder);
        websocketController.notifyOrderStateChanged(ibOrder);
    }

    @Override
    public void openOrder(int orderId, Contract contract, com.ib.client.Order order, OrderState orderState) {
        super.openOrder(orderId, contract, order, orderState);
        
        IbOrder ibOrder = ibOrderDao.getIbOrderByIbOrderId(ibAccount, orderId);
        if (ibOrder != null && ibOrder.getIbPermId() == null) {
            ibOrder.setIbPermId(order.m_permId);
            ibOrderDao.updateIbOrder(ibOrder);
        }
    }

    @Override
    public void nextValidId(int orderId) {
        super.nextValidId(orderId);
        ibController.setNextValidOrderId(ibAccount, orderId);
    }

    @Override
    public void managedAccounts(String accountsList) {
        super.managedAccounts(accountsList);
        ibController.getIbConnection(ibAccount).setAccounts(accountsList);
    }
}
