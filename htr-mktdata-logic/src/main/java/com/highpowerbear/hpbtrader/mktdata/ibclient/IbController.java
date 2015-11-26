package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataDefinitions;
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
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class IbController {
    private static final Logger l = Logger.getLogger(MktDataDefinitions.LOGGER);

    @Inject IbAccountDao ibAccountDao;
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection

    @PostConstruct
    public void init() {
        for (IbAccount ibAccount : ibAccountDao.getIbAccounts()) {
            ibConnectionMap.put(ibAccount, new IbConnection());
        }
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
            l.info("Not connected");
        }
        return connected;
    }

    public void reqHistoricalData(int tickerId, Contract contract, String endDateTime, String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate) {
        // use first connected ib connection
        IbConnection c = null;
        for (IbConnection ibc : ibConnectionMap.values()) {
            if (ibc.isConnected()) {
                c = ibc;
            }
        }
        if (c != null) {
            c.getClientSocket().reqHistoricalData(tickerId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH, formatDate, null);
        }
    }

    public boolean requestRealtimeData(int reqId, Contract contract) {
        boolean success = false;
        // use first connected ib connection
        IbConnection c = null;
        for (IbConnection ibc : ibConnectionMap.values()) {
            if (ibc.isConnected()) {
                c = ibc;
            }
        }
        if (c != null) {
            l.info("Requested realtime data, reqId=" + reqId + ", contract=" + HtrUtil.printIbContract(contract));
            c.getClientSocket().reqMktData(reqId, contract, "", false, null);
            success = true;
        }
        return success;
    }

    public boolean cancelRealtimeData(int reqId) {
        boolean success = false;
        for (IbConnection ibConnection : ibConnectionMap.values()) {
            if (ibConnection.isConnected()) {
                l.info("Canceling realtime data for reqId=" + reqId);
                ibConnection.getClientSocket().cancelMktData(reqId);
                success = true;
            }
        }
        return success;
    }
}
