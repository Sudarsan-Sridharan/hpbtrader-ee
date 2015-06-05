package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;

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
    @Inject private DatabaseDao databaseDao;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private EventBroker eventBroker;

    private EClientSocket ibClient;
    private EWrapper ibListener;
    private int nextValidOrderId = 1;

    // use set method instead of injection to prevent circular dependency
    public void setIbListener(EWrapper ibListener) {
        this.ibListener = ibListener;
    }

    public EClientSocket getIbClient() {
        return ibClient;
    }

    public void setNextValidOrderId(int nextValidOrderId) {
        this.nextValidOrderId = nextValidOrderId;
    }

    private Integer getNextValidOrderId() {
        return this.nextValidOrderId++;
    }

    public void connect() {
        if (ibClient == null)  {
            ibClient = new EClientSocket(ibListener);
        }
        if (!ibClient.isConnected()) {
            l.info("Connecting IB");
            ibClient.eConnect(LinSettings.HOST, LinSettings.PORT, LinSettings.CLIENT_ID);
            if (isConnected()) {
                requestOpenOrders();
                retrySubmit();
            }
        }
    }

    public void requestOpenOrders() {
        ibClient.reqOpenOrders();
    }

    public boolean disconnect() {
        if (ibClient != null && ibClient.isConnected()) {
            l.info("Disconnecting IB");
            ibClient.eDisconnect();
            linData.getRealtimeDataRequestMap().clear();
            linData.getRealtimeDataMap().clear();
        }
        ibClient = null;
        return isConnected();
    }

    public boolean isConnected() {
        return (ibClient != null && ibClient.isConnected());
    }

    private void retrySubmit() {
        databaseDao.getNewRetryOrders().forEach(this::submitIbOrder);
    }

    public void submitIbOrder(Order order) {
        l.info("START submit order " + order.getDescription());
        heartbeatControl.addHeartbeat(order);
        if (!isConnected()) {
            if (!LinEnums.OrderStatus.NEW_RETRY.equals(order.getOrderStatus())) {
                order.addEvent(LinEnums.OrderStatus.NEW_RETRY, LinUtil.getCalendar());
                databaseDao.updateOrder(order);
                eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
            }
            l.info("Not connected to IB, cannot submit order " + order.getDescription());
            return;
        }
        Integer ibOrderId = getNextValidOrderId();
        ibClient.placeOrder(ibOrderId, order.getStrategy().getSeries().createIbContract(), order.createIbOrder());
        order.addEvent(LinEnums.OrderStatus.SUBMIT_REQ, LinUtil.getCalendar());
        order.setIbOrderId(ibOrderId);
        databaseDao.updateOrder(order);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
        l.info("END submit order " + order.getDescription());
    }

    public void reqHistoricalData(int tickerId, Contract contract, String endDateTime, String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate) {
        ibClient.reqHistoricalData(tickerId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH, formatDate, null);
    }

    public boolean requestRealtimeData(int reqId, com.ib.client.Contract contract) {
        if (ibClient == null || !ibClient.isConnected()) {
            return false;
        }
        l.fine("Requested realtime data, reqId=" + reqId + ", contract=" + LinUtil.printIbContract(contract));
        ibClient.reqMktData(reqId, contract, "", false, null);
        return true;
    }

    public boolean cancelRealtimeData(int reqId) {
        if (ibClient == null || !ibClient.isConnected()) {
            return false;
        }
        l.fine("Canceling realtime data for reqId=" + reqId);
        ibClient.cancelMktData(reqId);
        return true;
    }
}
