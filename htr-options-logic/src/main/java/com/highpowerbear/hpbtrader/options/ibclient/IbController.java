package com.highpowerbear.hpbtrader.options.ibclient;

import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.OptionOrder;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject private OptDao optDao;
    @Inject private OptData optData;
    
    private EWrapper ibListener;
    private EClientSocket eclientSocket;
    private int nextValidOrderId = 1;

    public void start() throws Exception {
        setupHeartbeat();
        connect();
    }

    public void stop() throws Exception {
        disconnect();
        ibListener = null;
    }
    
    private void setupHeartbeat() {
        List<OptionOrder> openOptionOrders = optDao.getOpenOrders();
        for (OptionOrder o : openOptionOrders) {
            optData.getOpenOrderHeartbeatMap().put(o.getId(), OptDefinitions.MAX_ORDER_HEARTBEAT_FAILS);
        }
    }
    
    public void connect() {
        if (eclientSocket == null)  {
            eclientSocket = new EClientSocket(ibListener);
        }
        if (!eclientSocket.isConnected()) {
            l.info("Connecting IB, " + OptDefinitions.IB_HOST + ", " + OptDefinitions.IB_PORT + ", " + OptDefinitions.IB_CLIENT_ID);
            eclientSocket.eConnect(OptDefinitions.IB_HOST, OptDefinitions.IB_PORT, OptDefinitions.IB_CLIENT_ID);
            if (isConnected()) {
                requestOpenOrders();
            }
        }
    }
    
    public boolean disconnect() {
        if (eclientSocket != null && eclientSocket.isConnected()) {
            l.info("Disconnecting IB");
            eclientSocket.eDisconnect();
        }
        eclientSocket = null;
        return isConnected();
    }
    
    public boolean isConnected() {
        return (eclientSocket != null && eclientSocket.isConnected());
    }
    
    // needed in order to break circular dependency during bean creation
    public void setIbListener(EWrapper ibListener) {
        this.ibListener = ibListener;
    }
    
    private synchronized int consumeNextValidOrderId() {
        return nextValidOrderId++;
    }
    
    public synchronized void setNextValidOrderId(int orderId) {
        nextValidOrderId = orderId;
    }
    
    public synchronized void requestOpenOrders() {
        eclientSocket.reqOpenOrders();
    }
    
    public synchronized void retrySubmit() {
        optDao.getNewRetryOrders().forEach(this::submitIbOrder);
    }
    
    public synchronized void submitIbOrder(OptionOrder o) {
        l.info("START submit order " + o.print());
        optData.getOpenOrderHeartbeatMap().put(o.getId(), OptDefinitions.MAX_ORDER_HEARTBEAT_FAILS);
        if (!isConnected()) {
            if (!OptEnums.OrderStatus.NEW_RETRY.equals(o.getOrderStatus())) {
                o.addEvent(OptEnums.OrderStatus.NEW_RETRY);
                optDao.updateOrder(o);
            }
            l.info("Not connected to IB, cannot submit order " + o.print());
            return;
        }
        int orderId = consumeNextValidOrderId();
        eclientSocket.placeOrder(orderId, o.createApiOptionContract(), o.createApiOrder());
        o.addEvent(OptEnums.OrderStatus.SUBMIT_REQ);
        o.setIbOrderId(orderId);
        optDao.updateOrder(o);
        
        l.info("END submit order " + o.print());
    }
    
    public synchronized void requestOptionChain(int reqId, com.ib.client.Contract contract) {
        l.fine("Requested contract, requId=" + reqId + ", contract=" + OptUtil.printIbContract(contract));
        eclientSocket.reqContractDetails(reqId, contract);
    }
    
    public synchronized void requestRealtimeData(int reqId, com.ib.client.Contract contract) {
        l.fine("Requested realtime data, reqId=" + reqId + ", contract=" + OptUtil.printIbContract(contract));
        eclientSocket.reqMktData(reqId, contract, "101", false, null);
    }
    
    public synchronized void cancelRealtimeData(int reqId) {
        l.fine("Canceling realtime data for reqId=" + reqId);
        eclientSocket.cancelMktData(reqId);
    }
}
