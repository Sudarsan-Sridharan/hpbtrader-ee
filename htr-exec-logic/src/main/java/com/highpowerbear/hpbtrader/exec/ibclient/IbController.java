package com.highpowerbear.hpbtrader.exec.ibclient;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.ib.client.EClientSocket;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by rkolar on 4/10/14.
 */
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private IbOrderDao ibOrderDao;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private IbAccountDao ibAccountDao;
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection
    private Map<IbAccount, Integer> validOrderIdMap = new HashMap<>();

    public IbConnection getIbConnection(IbAccount ibAccount) {
        return ibConnectionMap.get(ibAccount);
    }

    @PostConstruct
    private void init() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> {
            ibConnectionMap.put(ibAccount, createIbConnection(ibAccount));
            validOrderIdMap.put(ibAccount, 1);
        });
    }

    private IbConnection createIbConnection(IbAccount ibAccount) {
        EClientSocket eClientSocket = new EClientSocket(new IbListenerImpl(ibAccount));
        return new IbConnection(HtrEnums.IbConnectionType.EXEC, ibAccount.getHost(), ibAccount.getPort(), ibAccount.getMktDataClientId(), eClientSocket);
    }

    public synchronized void setNextValidOrderId(IbAccount ibAccount, int nextValidOrderId) {
        validOrderIdMap.put(ibAccount, nextValidOrderId);
    }

    private synchronized Integer nextValidOrderId(IbAccount ibAccount) {
        Integer nextValidOrderId = validOrderIdMap.get(ibAccount);
        validOrderIdMap.put(ibAccount, nextValidOrderId + 1);
        return nextValidOrderId;
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

    public void retrySubmit(IbAccount ibAccount) {
        ibOrderDao.getNewRetryIbOrders(ibAccount).forEach(this::submitIbOrder);
    }

    public void submitIbOrder(IbOrder ibOrder) {
        l.info("START submit order " + ibOrder.getDescription());
        IbConnection c = ibConnectionMap.get(ibOrder.getStrategy().getIbAccount());
        heartbeatControl.initHeartbeat(ibOrder);
        if (!c.isConnected()) {
            if (!HtrEnums.IbOrderStatus.NEW_RETRY.equals(ibOrder.getStatus())) {
                ibOrder.addEvent(HtrEnums.IbOrderStatus.NEW_RETRY, HtrUtil.getCalendar(), null);
                ibOrderDao.updateIbOrder(ibOrder);
            }
            l.info("Not connected to IB, cannot submit order " + ibOrder.getDescription());
            return;
        }
        Integer ibOrderId = nextValidOrderId(ibOrder.getStrategy().getIbAccount());
        c.getClientSocket().placeOrder(ibOrderId, ibOrder.getStrategy().getTradeInstrument().createIbContract(), ibOrder.createIbOrder());
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, HtrUtil.getCalendar(), null);
        ibOrder.setIbOrderId(ibOrderId);
        ibOrderDao.updateIbOrder(ibOrder);
        l.info("END submit order " + ibOrder.getDescription());
    }
}
