package com.highpowerbear.hpbtrader.exec.ibclient;

import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.ib.client.EClientSocket;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private IbOrderDao ibOrderDao;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private IbAccountDao ibAccountDao;
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection
    private int nextValidOrderId = 1;

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    @PostConstruct
    private void init() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> ibConnectionMap.put(ibAccount, createIbConnection(ibAccount)));
    }

    private IbConnection createIbConnection(IbAccount ibAccount) {
        EClientSocket eClientSocket = new EClientSocket(new IbListenerImpl(ibAccount));
        return new IbConnection(HtrEnums.IbConnectionType.EXEC, ibAccount.getHost(), ibAccount.getPort(), ibAccount.getMktDataClientId(), eClientSocket);
    }

    public void setNextValidOrderId(IbAccount ibAccount, int nextValidOrderId) {
        this.nextValidOrderId = nextValidOrderId;
    }

    private Integer getNextValidOrderId() {
        return this.nextValidOrderId++;
    }

    public void connectExec(IbAccount ibAccount) {
        ibConnectionMap.get(ibAccount).connect();
    }

    public void disconnectExec(IbAccount ibAccount) {
        ibConnectionMap.get(ibAccount).disconnect();
    }

    public void requestOpenOrders(IbAccount ibAccount) {
        l.info("Requesting open orders for ibAccount " + ibAccount.print());
        IbConnection c = ibConnectionMap.get(ibAccount);
        c.getClientSocket().reqOpenOrders();
        c.getClientSocket().reqAllOpenOrders();
        c.getClientSocket().reqAutoOpenOrders(true);
    }

    private void retrySubmit(IbAccount ibAccount) {
        ibOrderDao.getNewRetryIbOrders(ibAccount).forEach(this::submitIbOrder);
    }

    public void submitIbOrder(IbOrder ibOrder) {
        l.info("START submit order " + ibOrder.getDescription());
        IbConnection c = ibConnectionMap.get(ibOrder.getStrategy().getIbAccount());
        heartbeatControl.addHeartbeat(ibOrder);
        if (!c.isConnected()) {
            if (!HtrEnums.IbOrderStatus.NEW_RETRY.equals(ibOrder.getStatus())) {
                ibOrder.addEvent(HtrEnums.IbOrderStatus.NEW_RETRY, HtrUtil.getCalendar(), null);
                ibOrderDao.updateIbOrder(ibOrder);
            }
            l.info("Not connected to IB, cannot submit order " + ibOrder.getDescription());
            return;
        }
        Integer ibOrderId = getNextValidOrderId();
        c.getClientSocket().placeOrder(ibOrderId, ibOrder.getStrategy().getTradeInstrument().createIbContract(), ibOrder.createIbOrder());
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, HtrUtil.getCalendar(), null);
        ibOrder.setIbOrderId(ibOrderId);
        ibOrderDao.updateIbOrder(ibOrder);
        l.info("END submit order " + ibOrder.getDescription());
    }
}
