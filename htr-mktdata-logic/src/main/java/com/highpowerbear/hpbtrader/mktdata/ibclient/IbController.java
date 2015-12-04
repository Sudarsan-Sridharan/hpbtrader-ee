package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrConstants;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);

    @Inject IbAccountDao ibAccountDao;
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    @PostConstruct
    public void init() {
        ibAccountDao.getIbAccounts().stream().forEach(ibAccount -> ibConnectionMap.put(ibAccount, new IbConnection()));
    }

    public void connect(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        if (c.getClientSocket() == null)  {
            c.setClientSocket(new EClientSocket(new IbListenerImpl()));
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
            }
        }
    }

    public void disconnect(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
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
        IbConnection c = ibConnectionMap.get(ibAccount);
        boolean connected = c.getClientSocket() != null && c.getClientSocket().isConnected();
        if (!connected) {
            l.info("Not connected " + ibAccount.print());
        }
        return connected;
    }

    public IbConnection getActiveConnection() {
        return ibConnectionMap.values().stream().filter(IbConnection::isConnected).findFirst().orElse(null);
    }

    public List<IbConnection> getActiveConnections() {
        return ibConnectionMap.values().stream().filter(IbConnection::isConnected).collect(Collectors.toList());
    }

    public boolean isAnyActiveConnection() {
        return getActiveConnection() != null;
    }

    public void reqHistoricalData(int tickerId, Contract contract, String endDateTime, String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate) {
        IbConnection c = getActiveConnection();
        if (c != null) {
            c.getClientSocket().reqHistoricalData(tickerId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH, formatDate, null);
        }
    }

    public void requestRealtimeData(int reqId, Contract contract) {
        IbConnection c = getActiveConnection();
        if (c != null) {
            l.info("Requested realtime data, reqId=" + reqId + ", contract=" + HtrUtil.printIbContract(contract));
            c.getClientSocket().reqMktData(reqId, contract, "", false, null);
        }
    }

    public void cancelRealtimeData(int reqId) {
        getActiveConnections().forEach(c -> {
            l.info("Canceling realtime data for reqId=" + reqId);
            c.getClientSocket().cancelMktData(reqId);
        });
    }
}
