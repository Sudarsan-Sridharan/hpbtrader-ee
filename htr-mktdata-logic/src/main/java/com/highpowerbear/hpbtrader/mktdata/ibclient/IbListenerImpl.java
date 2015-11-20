package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataMaps;
import com.highpowerbear.hpbtrader.mktdata.common.SingletonRepo;
import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.mktdata.process.RtDataController;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.ibclient.AbstractIbListener;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;

import java.util.Calendar;
import java.util.List;

/**
 *
 * @author rkolar
 */
public class IbListenerImpl extends AbstractIbListener {
    private MktDataMaps mktDataMaps = SingletonRepo.getInstance().getMktDataMaps();
    private IbController ibController = SingletonRepo.getInstance().getIbController();
    private HistDataController histDataController = SingletonRepo.getInstance().getHistDataController();
    private RtDataController rtDataController = SingletonRepo.getInstance().getRtDataController();
    private SeriesDao seriesDao = SingletonRepo.getInstance().getSeriesDao();

    private IbAccount ibAccount;

    public IbListenerImpl(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        //super.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
        
        Series s = seriesDao.findSeries(reqId / HtrSettings.IB_REQUEST_MULT);
        if (date.startsWith("finish")) {
            // remove last bar if it is not finished yet
            List<Bar> barsReceived = mktDataMaps.getBarsReceivedMap().get(s.getId());
            int numBars = barsReceived.size();
            Bar lastBar = barsReceived.get(numBars - 1);
            if (lastBar.getTimeInMillisBarClose() > HtrUtil.getCalendar().getTimeInMillis()) {
                barsReceived.remove(numBars - 1);
            }
            histDataController.processBars(s);
            return;
        }
        Bar q = new Bar();
        Calendar c = HtrUtil.getCalendar();
        c.setTimeInMillis(Long.valueOf(date) * 1000 + s.getInterval().getMillis()); // date-time stamp of the end of the bar
        if (HtrEnums.Interval.INT_60_MIN.equals(s.getInterval())) {
            c.set(Calendar.MINUTE, 0); // needed in case of bars started at 9:30 (END 10:00 not 10:30) or 17:15 (END 18:00 not 18:15)
        }
        q.setqDateBarClose(c);
        q.setqOpen(open);
        q.setHigh(high);
        q.setLow(low);
        q.setqClose(close);
        q.setVolume(volume == -1 ? 0 : volume);
        q.setCount(count);
        q.setWap(WAP);
        q.setHasGaps(hasGaps);
        q.setSeries(s);
        mktDataMaps.getBarsReceivedMap().get(s.getId()).add(q);
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        rtDataController.updateRealtimeData(tickerId, field, price);
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        rtDataController.updateRealtimeData(tickerId, field, size);
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        rtDataController.updateRealtimeData(tickerId, tickType, value);
    }
}
