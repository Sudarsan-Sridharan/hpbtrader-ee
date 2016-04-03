package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
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
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject IbAccountDao ibAccountDao;
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    @PostConstruct
    public void init() {
        ibAccountDao.getIbAccounts().stream().forEach(ibAccount -> ibConnectionMap.put(ibAccount, new IbConnection()));
    }

    public void connectMktData(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        if (c.getClientSocket() == null)  {
            c.setClientSocket(new EClientSocket(new IbListenerImpl()));
        }
        if (c.getClientSocket() != null && !c.getClientSocket().isConnected()) {
            c.setAccounts(null);
            c.setIsConnected(false);
            l.info("Connecting mkt data " + ibAccount.print());
            c.getClientSocket().eConnect(ibAccount.getHost(), ibAccount.getPort(), ibAccount.getMktDataClientId());
            HtrUtil.waitMilliseconds(HtrDefinitions.ONE_SECOND_MILLIS);
            if (isConnectedMktData(ibAccount)) {
                c.setIsConnected(true);
                l.info("Sucessfully connected mkt data " + ibAccount.print());
            }
        }
    }

    public void disconnectMktData(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        if (c.getClientSocket() != null && c.getClientSocket().isConnected()) {
            l.info("Disconnecting mkt data " + ibAccount.print());
            c.getClientSocket().eDisconnect();
            HtrUtil.waitMilliseconds(HtrDefinitions.ONE_SECOND_MILLIS);
            if (!isConnectedMktData(ibAccount)) {
                l.info("Successfully disconnected mkt data " + ibAccount.print());
                c.clear();
            }
        }
    }

    public boolean isConnectedMktData(IbAccount ibAccount) {
        IbConnection c = ibConnectionMap.get(ibAccount);
        boolean connected = c.getClientSocket() != null && c.getClientSocket().isConnected();
        if (!connected) {
            l.info("Not connected mkt data " + ibAccount.print());
        }
        return connected;
    }

    private IbConnection getActiveMktDataConnection() {
        return ibConnectionMap.values().stream().filter(IbConnection::isConnected).findFirst().orElse(null);
    }

    private List<IbConnection> getActiveMktDataConnections() {
        return ibConnectionMap.values().stream().filter(IbConnection::isConnected).collect(Collectors.toList());
    }

    public boolean isAnyActiveMktDataConnection() {
        return getActiveMktDataConnection() != null;
    }

    public void reqHistoricalData(int tickerId, Contract contract, String endDateTime, String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate) {
        IbConnection c = getActiveMktDataConnection();
        if (c != null) {
            c.getClientSocket().reqHistoricalData(tickerId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH, formatDate, null);
        }
    }

    public void requestRealtimeData(int reqId, Contract contract) {
        IbConnection c = getActiveMktDataConnection();
        if (c != null) {
            l.info("Requested realtime data, reqId=" + reqId + ", contract=" + HtrUtil.printIbContract(contract));
            c.getClientSocket().reqMktData(reqId, contract, "", false, null);
        }
    }

    public void cancelRealtimeData(int reqId) {
        getActiveMktDataConnections().forEach(c -> {
            l.info("Canceling realtime data for reqId=" + reqId);
            c.getClientSocket().cancelMktData(reqId);
        });
    }
}
