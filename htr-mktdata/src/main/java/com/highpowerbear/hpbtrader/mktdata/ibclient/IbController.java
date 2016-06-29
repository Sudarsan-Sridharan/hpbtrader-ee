package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by rkolar on 4/10/14.
 */
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject IbAccountDao ibAccountDao;
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection

    public IbConnection getIbConnection(IbAccount ibAccount) {
        return ibConnectionMap.get(ibAccount);
    }

    @PostConstruct
    private void init() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> ibConnectionMap.put(ibAccount, createIbConnection(ibAccount)));
    }

    private IbConnection createIbConnection(IbAccount ibAccount) {
        EClientSocket eClientSocket = new EClientSocket(new IbListenerImpl(ibAccount));
        return new IbConnection(HtrEnums.IbConnectionType.MKTDATA, ibAccount.getHost(), ibAccount.getPort(), ibAccount.getMktDataClientId(), eClientSocket);
    }

    private IbConnection getActiveMktDataConnection() {
        return ibConnectionMap.values().stream().filter(IbConnection::isConnected).findFirst().orElse(null);
    }

    private List<IbConnection> getActiveMktDataConnections() {
        return ibConnectionMap.values().stream().filter(IbConnection::isConnected).collect(Collectors.toList());
    }

    public void connectMktData(IbAccount ibAccount) {
        ibConnectionMap.get(ibAccount).connect();
    }

    public void disconnectMktData(IbAccount ibAccount) {
        ibConnectionMap.get(ibAccount).disconnect();
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

    public boolean requestRealtimeData(int reqId, Contract contract) {
        IbConnection c = getActiveMktDataConnection();
        boolean requested = false;
        if (c != null) {
            l.info("Requested realtime data, reqId=" + reqId + ", contract=" + HtrUtil.printIbContract(contract));
            c.getClientSocket().reqMktData(reqId, contract, "", false, null);
            requested = true;
        }
        return requested;
    }

    public boolean cancelRealtimeData(int reqId) {
        boolean canceled = false;
        for (IbConnection c : getActiveMktDataConnections()) {
            l.info("Canceling realtime data for reqId=" + reqId);
            c.getClientSocket().cancelMktData(reqId);
            canceled = true;
        }
       return canceled;
    }
}
