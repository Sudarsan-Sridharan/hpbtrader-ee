package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.message.MqSender;
import com.highpowerbear.hpbtrader.shared.defintions.HtrConstants;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;
import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.ib.client.Contract;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@ApplicationScoped
public class HistDataController {
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;
    @Inject private IbController ibController;
    @Inject private MqSender mqSender;
    private DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private Map<Series, Map<Long, Bar>> barsReceivedMap = new HashMap<>(); // series --> (timeInMillisBarClose --> bar)

    public void barReceived(int reqId, String date, Bar bar) {
        int seriesId = reqId / HtrSettings.IB_REQUEST_MULT;
        Series series = seriesDao.findSeries(seriesId);
        if (barsReceivedMap.get(series) == null) {
            barsReceivedMap.put(series, new LinkedHashMap<>());
        }
        Calendar c = HtrUtil.getCalendar();
        c.setTimeInMillis(Long.valueOf(date) * 1000 + series.getInterval().getMillis()); // date-time stamp of the end of the bar
        if (HtrEnums.Interval.INT_60_MIN.equals(series.getInterval())) {
            c.set(Calendar.MINUTE, 0); // needed in case of bars started at 9:30 (END 10:00 not 10:30) or 17:15 (END 18:00 not 18:15)
        }
        bar.setqDateBarClose(c);
        bar.setSeries(series);
        barsReceivedMap.get(series).put(bar.getTimeInMillisBarClose(), bar);
    }

    public void reqFinished(int reqId) {
        int seriesId = reqId / HtrSettings.IB_REQUEST_MULT;
        Series series = seriesDao.findSeries(seriesId);
        // remove last bar if it is not finished yet
        new LinkedHashSet<>(barsReceivedMap.get(series).keySet())
                .stream()
                .filter(timeInMillisBarClose -> timeInMillisBarClose > System.currentTimeMillis())
                .forEach(barsReceivedMap.get(series)::remove);
        List<Bar> barsToCreate = new ArrayList<>(barsReceivedMap.get(series).values());
        barDao.createBars(series, barsToCreate);
        Bar lastBar = barsToCreate.get(barsToCreate.size() - 1);
        boolean isCurrentLastBar = ((lastBar.getTimeInMillisBarClose() + series.getInterval().getMillis()) > System.currentTimeMillis());
        if (isCurrentLastBar) {
            mqSender.notifyBarsAdded(series);
        }
        barsReceivedMap.remove(series);
    }

    public void requestFiveMinBars(IbAccount ibAccount) {
        l.info("START requestFiveMinBars");
        if (!ibController.isConnected(ibAccount)) {
            return;
        }
        seriesDao.getSeriesByInterval(HtrEnums.Interval.INT_5_MIN).stream().filter(Series::getEnabled).forEach(s -> {
            Contract contract = s.createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? MktDefinitions.IB_ETH_TOO : MktDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    (HtrEnums.SecType.CASH.equals(s.getSecType()) ? MktDefinitions.IB_DURATION_1_DAY : MktDefinitions.IB_DURATION_2_DAY),
                    MktDefinitions.IB_BAR_5_MIN,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        });
        l.info("END requestFiveMinBars");
    }

    public void requestSixtyMinBars(IbAccount ibAccount) {
        l.info("START requestSixtyMinBars");
        if (!ibController.isConnected(ibAccount)) {
            return;
        }
        seriesDao.getSeriesByInterval(HtrEnums.Interval.INT_60_MIN).stream().filter(Series::getEnabled).forEach(s -> {
            Contract contract = s.createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? MktDefinitions.IB_ETH_TOO : MktDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    MktDefinitions.IB_DURATION_1_WEEK,
                    MktDefinitions.IB_BAR_1_HOUR,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        });
        l.info("END requestSixtyMinBars");
    }

    public void backfill(Series series) {
        if (!series.getEnabled()) {
            l.info("Series not enabled, backfill won't be performed, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
            return;
        }
        l.info("START backfillManual, series=" + series.getId() + ", symbol=" + series.getSymbol());
        if (!ibController.isAnyActiveConnection()) {
            return;
        }
        Contract contract = series.createIbContract();
        Calendar now = HtrUtil.getCalendar();
        int isUseRTH = (HtrEnums.SecType.FUT.equals(series.getSecType()) ? MktDefinitions.IB_ETH_TOO : MktDefinitions.IB_RTH_ONLY);
        if (HtrEnums.Interval.INT_5_MIN.equals(series.getInterval())) {
            ibController.reqHistoricalData(
                    series.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    MktDefinitions.IB_DURATION_10_DAY,
                    MktDefinitions.IB_BAR_5_MIN,
                    series.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        } else if (HtrEnums.Interval.INT_60_MIN.equals(series.getInterval())) {
            int reqId = (series.getId() * HtrSettings.IB_REQUEST_MULT) + 4;
            Calendar his = HtrUtil.getCalendar();
            his.add(Calendar.MONTH, -3);
            for (int i = 0; i < 4; i++) {
                ibController.reqHistoricalData(
                        reqId--,
                        contract,
                        df.format(his.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                        MktDefinitions.IB_DURATION_1_MONTH,
                        MktDefinitions.IB_BAR_1_HOUR,
                        series.getSecType().getIbBarType(),
                        isUseRTH,
                        MktDefinitions.IB_FORMAT_DATE_MILLIS);
                his.add(Calendar.MONTH, 1);
                his.add(Calendar.DAY_OF_MONTH, -3); // some overlap
            }
            ibController.reqHistoricalData(
                    reqId,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    MktDefinitions.IB_DURATION_1_MONTH,
                    MktDefinitions.IB_BAR_1_HOUR,
                    series.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END backfillManual, series=" + series.getId() + ", symbol=" + series.getSymbol());
    }
}
