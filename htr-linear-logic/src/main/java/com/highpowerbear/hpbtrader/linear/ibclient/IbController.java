package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrConstants;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private LinData linData;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private EventBroker eventBroker;

    private int nextValidOrderId = 1;

    public void setNextValidOrderId(IbAccount ibAccount, int nextValidOrderId) {
        this.nextValidOrderId = nextValidOrderId;
    }

    private Integer getNextValidOrderId() {
        return this.nextValidOrderId++;
    }

    public void connect(IbAccount ibAccount) {
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);

        if (c.getClientSocket() == null)  {
            c.setClientSocket(new EClientSocket(new IbListenerImpl(ibAccount)));
        }
        if (c.getClientSocket() != null && !c.getClientSocket().isConnected()) {
            c.setAccounts(null);
            c.setIsConnected(false);
            l.info("Connecting ibAccount " + ibAccount.print());
            c.getClientSocket().eConnect(ibAccount.getHost(), ibAccount.getPort(), HtrSettings.IB_CONNECT_CLIENT_ID);
            HtrUtil.waitMilliseconds(HtrConstants.ONE_SECOND);
            if (isConnected(ibAccount)) {
                c.setIsConnected(true);
                l.info("Sucessfully connected ibAccount " + ibAccount.print());
                requestOpenOrders(ibAccount);
                requestAccounts(ibAccount);
            }
        }
    }

    public void disconnect(IbAccount ibAccount) {
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);
        if (c.getClientSocket() != null && c.getClientSocket().isConnected()) {
            l.info("Disconnecting ibAccount " + ibAccount.print());
            c.getClientSocket().eDisconnect();
            HtrUtil.waitMilliseconds(HtrConstants.ONE_SECOND);
            if (!isConnected(ibAccount)) {
                l.info("Successfully disconnected ibAccount " + ibAccount.print());
                c.clear();
            }
        }
    }

    public boolean isConnected(IbAccount ibAccount) {
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);
        return (c.getClientSocket() != null && c.getClientSocket().isConnected());
    }

    public void requestOpenOrders(IbAccount ibAccount) {
        l.info("Requesting open orders for ibAccount " + ibAccount.print());
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);
        c.getClientSocket().reqOpenOrders();
        c.getClientSocket().reqAllOpenOrders();
        c.getClientSocket().reqAutoOpenOrders(true);
    }

    private void requestAccounts(IbAccount ibAccount) {
        l.info("Requesting account for ibAccount " + ibAccount.print());
        linData.getIbConnectionMap().get(ibAccount).getClientSocket().reqManagedAccts();
    }

    private void retrySubmit(IbAccount ibAccount) {
        ibOrderDao.getNewRetryIbOrders(ibAccount).forEach(this::submitIbOrder);
    }

    public void submitIbOrder(IbOrder ibOrder) {
        l.info("START submit order " + ibOrder.getDescription());
        IbConnection c = linData.getIbConnectionMap().get(ibOrder.getIbAccount());
        heartbeatControl.addHeartbeat(ibOrder);
        if (!isConnected(ibOrder.getIbAccount())) {
            if (!HtrEnums.IbOrderStatus.NEW_RETRY.equals(ibOrder.getStatus())) {
                ibOrder.addEvent(HtrEnums.IbOrderStatus.NEW_RETRY, HtrUtil.getCalendar(), null);
                ibOrderDao.updateIbOrder(ibOrder);
                eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
            }
            l.info("Not connected to IB, cannot submit order " + ibOrder.getDescription());
            return;
        }
        Integer ibOrderId = getNextValidOrderId();
        c.getClientSocket().placeOrder(ibOrderId, ibOrder.getStrategy().getSeries().createIbContract(), ibOrder.createIbOrder());
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, HtrUtil.getCalendar(), null);
        ibOrder.setIbOrderId(ibOrderId);
        ibOrderDao.updateIbOrder(ibOrder);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
        l.info("END submit order " + ibOrder.getDescription());
    }

    public void reqHistoricalData(IbAccount ibAccount, int tickerId, Contract contract, String endDateTime, String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate) {
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);
        c.getClientSocket().reqHistoricalData(tickerId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH, formatDate, null);
    }

    public boolean requestRealtimeData(IbAccount ibAccount, int reqId, com.ib.client.Contract contract) {
        if (!isConnected(ibAccount)) {
            return false;
        }
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);
        l.fine("Requested realtime data, reqId=" + reqId + ", contract=" + HtrUtil.printIbContract(contract));
        c.getClientSocket().reqMktData(reqId, contract, "", false, null);
        return true;
    }

    public boolean cancelRealtimeData(IbAccount ibAccount, int reqId) {
        if (!isConnected(ibAccount)) {
            return false;
        }
        IbConnection c = linData.getIbConnectionMap().get(ibAccount);
        l.fine("Canceling realtime data for reqId=" + reqId);
        c.getClientSocket().cancelMktData(reqId);
        return true;
    }
}
