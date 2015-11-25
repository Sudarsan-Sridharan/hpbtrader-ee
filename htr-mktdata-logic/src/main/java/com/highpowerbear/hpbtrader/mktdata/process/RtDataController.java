package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataMaps;
import com.highpowerbear.hpbtrader.mktdata.common.MktDataDefinitions;
import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.mktdata.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.entity.Series;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Created by robertk on 20.11.2015.
 */
@Named
@ApplicationScoped
public class RtDataController {
    private static final Logger l = Logger.getLogger(MktDataDefinitions.LOGGER);

    @Inject private MktDataMaps mktDataMaps;
    @Inject private IbController ibController;
    @Inject private WebsocketController websocketController;

    public void toggleRealtimeData(Series series) {
        RealtimeData rtd = null;
        for (RealtimeData realtimeData : mktDataMaps.getRealtimeDataMap().values()) {
            if (realtimeData.getSeries().equals(series)) {
                rtd = realtimeData;
            }
        }
        if (rtd == null) {
            rtd = new RealtimeData(series);
            l.fine("Requesting realtime data for " + rtd.getSeries().getSymbol());
            mktDataMaps.getRealtimeDataMap().put(rtd.getIbRequestId(), rtd);
            boolean requested = ibController.requestRealtimeData(rtd.getIbRequestId(), rtd.getSeries().createIbContract());
            if (!requested) {
                mktDataMaps.getRealtimeDataMap().remove(rtd.getIbRequestId());
            }
        } else {
            l.fine("Canceling realtime data for " + rtd.getSeries().getSymbol());
            boolean requested = ibController.cancelRealtimeData(rtd.getIbRequestId());
            if (requested) {
                mktDataMaps.getRealtimeDataMap().remove(rtd.getIbRequestId());
            }
        }
    }
}
