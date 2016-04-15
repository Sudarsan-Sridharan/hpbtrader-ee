package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.model.RealtimeData;
import com.highpowerbear.hpbtrader.mktdata.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.ib.client.TickType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by robertk on 20.11.2015.
 */
@Named
@ApplicationScoped
public class RtDataController {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private IbController ibController;
    @Inject private WebsocketController websocketController;
    private Map<Integer, RealtimeData> realtimeDataMap = new LinkedHashMap<>(); // ib request id --> realtimeData

    public List<RealtimeData> getRealtimeDataList() {
        return new ArrayList<>(realtimeDataMap.values());
    }

    public void tickPriceReceived(int tickerId, int field, double price) {
        RealtimeData rtd = realtimeDataMap.get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(field, price);
        if (updateMessage != null) {
            websocketController.broadcastMessage(updateMessage);
            if (field == TickType.LAST || (field == TickType.ASK && HtrEnums.SecType.CASH.equals(rtd.getDataSeries().getInstrument().getSecType()))) {
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

    public void toggleRealtimeData(DataSeries dataSeries) {
        if (!ibController.isAnyActiveMktDataConnection()) {
            return;
        }
        RealtimeData rtd = realtimeDataMap.values().stream().filter(r -> r.getDataSeries().equals(dataSeries)).findAny().orElse(null);
        if (rtd == null) {
            rtd = new RealtimeData(dataSeries);
            l.info("Requesting realtime data for " + rtd.getDataSeries().getInstrument().getSymbol());
            realtimeDataMap.put(rtd.getIbRequestId(), rtd);
            boolean requested = ibController.requestRealtimeData(rtd.getIbRequestId(), rtd.getDataSeries().getInstrument().createIbContract());
            if (!requested) {
                realtimeDataMap.remove(rtd.getIbRequestId());
            }
        } else {
            l.info("Canceling realtime data for " + rtd.getDataSeries().getInstrument().getSymbol());
            boolean canceled = ibController.cancelRealtimeData(rtd.getIbRequestId());
            if (canceled) {
                realtimeDataMap.remove(rtd.getIbRequestId());
            }
        }
    }

    public void cancelAllMktData() {
        for (Integer requestId : new ArrayList<>(realtimeDataMap.keySet())) {
            RealtimeData rtd = realtimeDataMap.get(requestId);
            l.info("Canceling realtime data for " + rtd.getDataSeries().getInstrument().getSymbol());
            boolean canceled = ibController.cancelRealtimeData(rtd.getIbRequestId());
            if (canceled) {
                realtimeDataMap.remove(rtd.getIbRequestId());
            }
        }
    }
}
