package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.mktdata.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.ib.client.TickType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by robertk on 20.11.2015.
 */
@Named
@ApplicationScoped
public class RtDataController {
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);

    @Inject private IbController ibController;
    @Inject private WebsocketController websocketController;
    private Map<Integer, RealtimeData> realtimeDataMap = new LinkedHashMap<>(); // ib request id --> realtimeData

    public void tickPriceReceived(int tickerId, int field, double price) {
        RealtimeData rtd = realtimeDataMap.get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(field, price);
        if (updateMessage != null) {
            websocketController.broadcastMessage(updateMessage);
            if (field == TickType.LAST || (field == TickType.ASK && HtrEnums.SecType.CASH.equals(rtd.getSeries().getSecType()))) {
                String updateMessageChangePct = rtd.createChangePctUpdateMsg();
                websocketController.broadcastMessage(updateMessageChangePct);
            }
        }
    }

    public void tickSizeReceived(int tickerId, int field, int size) {
        RealtimeData rtd = realtimeDataMap.get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(field, size);
        if (updateMessage != null) {
            websocketController.broadcastMessage(updateMessage);
        }
    }

    public void tickGenericReceived(int tickerId, int tickType, double value) {
        RealtimeData rtd = realtimeDataMap.get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(tickType, value);
        if (updateMessage != null) {
            websocketController.broadcastMessage(updateMessage);
        }
    }

    public void toggleRealtimeData(Series series) {
        if (!ibController.isAnyActiveConnection()) {
            return;
        }
        RealtimeData rtd = realtimeDataMap.values().stream().filter(r -> r.getSeries().equals(series)).findAny().get();
        if (rtd == null) {
            rtd = new RealtimeData(series);
            l.info("Requesting realtime data for " + rtd.getSeries().getSymbol());
            realtimeDataMap.put(rtd.getIbRequestId(), rtd);
            ibController.requestRealtimeData(rtd.getIbRequestId(), rtd.getSeries().createIbContract());
            realtimeDataMap.remove(rtd.getIbRequestId());
        } else {
            l.info("Canceling realtime data for " + rtd.getSeries().getSymbol());
            ibController.cancelRealtimeData(rtd.getIbRequestId());
            realtimeDataMap.remove(rtd.getIbRequestId());
        }
    }
}
