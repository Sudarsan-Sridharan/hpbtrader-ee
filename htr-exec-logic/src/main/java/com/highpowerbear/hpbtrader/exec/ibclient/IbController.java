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

    @PostConstruct
    public void init() {
        ibAccountDao.getIbAccounts().stream().forEach(ibAccount -> ibConnectionMap.put(ibAccount, new IbConnection()));
    }

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    public void setNextValidOrderId(IbAccount ibAccount, int nextValidOrderId) {
        this.nextValidOrderId = nextValidOrderId;
    }

    private Integer getNextValidOrderId() {
        return this.nextValidOrderId++;
    }

    public void connectExec(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);

        if (c.getClientSocket() == null)  {
            c.setClientSocket(new EClientSocket(new IbListenerImpl(ibAccount)));
        }
        if (c.getClientSocket() != null && !c.getClientSocket().isConnected()) {
            c.setAccounts(null);
            c.setIsConnected(false);
            l.info("Connecting ibAccount " + ibAccount.print());
            c.getClientSocket().eConnect(ibAccount.getHost(), ibAccount.getPort(), ibAccount.getExecClientId());
            HtrUtil.waitMilliseconds(HtrDefinitions.ONE_SECOND_MILLIS);
            if (isConnectedExec(ibAccount)) {
                c.setIsConnected(true);
                l.info("Sucessfully connected ibAccount " + ibAccount.print());
                requestOpenOrders(ibAccount);
                requestAccounts(ibAccount);
            }
        }
    }

    public void disconnectExec(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        if (c.getClientSocket() != null && c.getClientSocket().isConnected()) {
            l.info("Disconnecting ibAccount " + ibAccount.print());
            c.getClientSocket().eDisconnect();
            HtrUtil.waitMilliseconds(HtrDefinitions.ONE_SECOND_MILLIS);
            if (!isConnectedExec(ibAccount)) {
                l.info("Successfully disconnected ibAccount " + ibAccount.print());
                c.clear();
            }
        }
    }

    public boolean isConnectedExec(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        return (c.getClientSocket() != null && c.getClientSocket().isConnected());
    }

    public void requestOpenOrders(IbAccount ibAccount) {
        l.info("Requesting open orders for ibAccount " + ibAccount.print());
        IbConnection c = ibConnectionMap.get(ibAccount);
        c.getClientSocket().reqOpenOrders();
        c.getClientSocket().reqAllOpenOrders();
        c.getClientSocket().reqAutoOpenOrders(true);
    }

    private void requestAccounts(IbAccount ibAccount) {
        l.info("Requesting account for ibAccount " + ibAccount.print());
        ibConnectionMap.get(ibAccount).getClientSocket().reqManagedAccts();
    }

    private void retrySubmit(IbAccount ibAccount) {
        ibOrderDao.getNewRetryIbOrders(ibAccount).forEach(this::submitIbOrder);
    }

    public void submitIbOrder(IbOrder ibOrder) {
        l.info("START submit order " + ibOrder.getDescription());
        IbConnection c = ibConnectionMap.get(ibOrder.getStrategy().getIbAccount());
        heartbeatControl.addHeartbeat(ibOrder);
        if (!isConnectedExec(ibOrder.getStrategy().getIbAccount())) {
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
