package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataMaps;
import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrConstants;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
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
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);
    @Inject private MktDataMaps mktDataMaps;

    private int nextValidOrderId = 1;

    public void setNextValidOrderId(IbAccount ibAccount, int nextValidOrderId) {
        this.nextValidOrderId = nextValidOrderId;
    }

    private Integer getNextValidOrderId() {
        return this.nextValidOrderId++;
    }

    public void connect(IbAccount ibAccount) {
        IbConnection c = mktDataMaps.getIbConnectionMap().get(ibAccount);

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
            }
        }
    }

    public void disconnect(IbAccount ibAccount) {
        IbConnection c = mktDataMaps.getIbConnectionMap().get(ibAccount);
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
        IbConnection c = mktDataMaps.getIbConnectionMap().get(ibAccount);
        return (c.getClientSocket() != null && c.getClientSocket().isConnected());
    }

    public void reqHistoricalData(IbAccount ibAccount, int tickerId, Contract contract, String endDateTime, String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate) {
        IbConnection c = mktDataMaps.getIbConnectionMap().get(ibAccount);
        c.getClientSocket().reqHistoricalData(tickerId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH, formatDate, null);
    }

    public boolean requestRealtimeData(IbAccount ibAccount, int reqId, Contract contract) {
        if (!isConnected(ibAccount)) {
            return false;
        }
        IbConnection c = mktDataMaps.getIbConnectionMap().get(ibAccount);
        l.fine("Requested realtime data, reqId=" + reqId + ", contract=" + HtrUtil.printIbContract(contract));
        c.getClientSocket().reqMktData(reqId, contract, "", false, null);
        return true;
    }

    public boolean cancelRealtimeData(IbAccount ibAccount, int reqId) {
        if (!isConnected(ibAccount)) {
            return false;
        }
        IbConnection c = mktDataMaps.getIbConnectionMap().get(ibAccount);
        l.fine("Canceling realtime data for reqId=" + reqId);
        c.getClientSocket().cancelMktData(reqId);
        return true;
    }
}
