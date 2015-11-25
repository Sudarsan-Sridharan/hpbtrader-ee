package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataMaps;
import com.highpowerbear.hpbtrader.mktdata.common.MktDataDefinitions;
import com.highpowerbear.hpbtrader.mktdata.common.SingletonRepo;
import com.highpowerbear.hpbtrader.mktdata.message.MqSender;
import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.mktdata.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.ibclient.AbstractIbListener;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.ib.client.TickType;

import java.util.*;

/**
 *
 * @author rkolar
 */
public class IbListenerImpl extends AbstractIbListener {
    private SeriesDao seriesDao = SingletonRepo.getInstance().getSeriesDao();
    private BarDao barDao = SingletonRepo.getInstance().getBarDao();
    private MktDataMaps mktDataMaps = SingletonRepo.getInstance().getMktDataMaps();
    private MqSender mqSender = SingletonRepo.getInstance().getMqSender();
    private WebsocketController websocketController = SingletonRepo.getInstance().getWebsocketController();

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        //super.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
        
        Series series = seriesDao.findSeries(reqId / HtrSettings.IB_REQUEST_MULT);
        Map<Long, Bar> barsReceived = mktDataMaps.getBarsReceivedMap().get(series);
        if (barsReceived == null) {
            barsReceived = new LinkedHashMap<>();
            mktDataMaps.getBarsReceivedMap().put(series, barsReceived);
        }

        if (date.startsWith(MktDataDefinitions.FINISH)) {
            // remove last bar if it is not finished yet
            new LinkedHashSet<>(barsReceived.keySet()).stream().filter(timeInMillisBarClose -> timeInMillisBarClose > System.currentTimeMillis()).forEach(barsReceived::remove);
            barDao.createBars(new ArrayList<>(barsReceived.values()));
            mqSender.notifyBarsAdded(series);
            mktDataMaps.getBarsReceivedMap().remove(series);
            return;
        }
        Bar bar = new Bar();
        Calendar c = HtrUtil.getCalendar();
        c.setTimeInMillis(Long.valueOf(date) * 1000 + series.getInterval().getMillis()); // date-time stamp of the end of the bar
        if (HtrEnums.Interval.INT_60_MIN.equals(series.getInterval())) {
            c.set(Calendar.MINUTE, 0); // needed in case of bars started at 9:30 (END 10:00 not 10:30) or 17:15 (END 18:00 not 18:15)
        }
        bar.setqDateBarClose(c);
        bar.setqOpen(open);
        bar.setHigh(high);
        bar.setLow(low);
        bar.setqClose(close);
        bar.setVolume(volume == -1 ? 0 : volume);
        bar.setCount(count);
        bar.setWap(WAP);
        bar.setHasGaps(hasGaps);
        bar.setSeries(series);
        barsReceived.put(bar.getTimeInMillisBarClose(), bar);
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        RealtimeData rtd = mktDataMaps.getRealtimeDataMap().get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(field, price);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
            if (field == TickType.LAST || (field == TickType.ASK && HtrEnums.SecType.CASH.equals(rtd.getSeries().getSecType()))) {
                String updateMessageChangePct = rtd.createChangePctUpdateMsg();
                websocketController.broadcastSeriesMessage(updateMessageChangePct);
            }
        }
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        RealtimeData rtd = mktDataMaps.getRealtimeDataMap().get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(field, size);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
        }
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        RealtimeData rtd = mktDataMaps.getRealtimeDataMap().get(tickerId);
        if (rtd == null) {
            return;
        }
        String updateMessage = rtd.createUpdateMessage(tickType, value);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
        }
    }


}
