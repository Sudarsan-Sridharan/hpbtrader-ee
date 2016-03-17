package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.message.MqSender;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
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
    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private IbController ibController;
    @Inject private MqSender mqSender;
    private DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private Map<DataSeries, Map<Long, DataBar>> barsReceivedMap = new HashMap<>(); // series --> (timeInMillisBarClose --> bar)

    public void barReceived(int reqId, String date, DataBar dataBar) {
        int seriesId = reqId / HtrDefinitions.IB_REQUEST_MULT;
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (barsReceivedMap.get(dataSeries) == null) {
            barsReceivedMap.put(dataSeries, new LinkedHashMap<>());
        }
        Calendar c = HtrUtil.getCalendar();
        c.setTimeInMillis(Long.valueOf(date) * 1000 + dataSeries.getInterval().getMillis()); // date-time stamp of the end of the bar
        if (HtrEnums.Interval.MIN60.equals(dataSeries.getInterval())) {
            c.set(Calendar.MINUTE, 0); // needed in case of bars started at 9:30 (END 10:00 not 10:30) or 17:15 (END 18:00 not 18:15)
        }
        dataBar.setqDateBarClose(c);
        dataBar.setDataSeries(dataSeries);
        barsReceivedMap.get(dataSeries).put(dataBar.getTimeInMillisBarClose(), dataBar);
    }

    public void reqFinished(int reqId) {
        int seriesId = reqId / HtrDefinitions.IB_REQUEST_MULT;
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        // remove last bar if it is not finished yet
        new LinkedHashSet<>(barsReceivedMap.get(dataSeries).keySet())
                .stream()
                .filter(timeInMillisBarClose -> timeInMillisBarClose > System.currentTimeMillis())
                .forEach(barsReceivedMap.get(dataSeries)::remove);
        List<DataBar> barsToCreate = new ArrayList<>(barsReceivedMap.get(dataSeries).values());
        dataSeriesDao.createBars(dataSeries, barsToCreate);
        DataBar lastDataBar = barsToCreate.get(barsToCreate.size() - 1);
        boolean isCurrentLastBar = ((lastDataBar.getTimeInMillisBarClose() + dataSeries.getInterval().getMillis()) > System.currentTimeMillis());
        if (isCurrentLastBar) {
            mqSender.notifyBarsAdded(dataSeries);
        }
        barsReceivedMap.remove(dataSeries);
    }

    public void requestFiveMinBars(IbAccount ibAccount) {
        l.info("START requestFiveMinBars");
        if (!ibController.isConnected(ibAccount)) {
            return;
        }
        dataSeriesDao.getSeriesByInterval(HtrEnums.Interval.MIN5).stream().filter(DataSeries::getActive).forEach(s -> {
            Contract contract = s.getInstrument().createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getInstrument().getSecType()) ? MktDefinitions.IB_ETH_TOO : MktDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrDefinitions.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    (HtrEnums.SecType.CASH.equals(s.getInstrument().getSecType()) ? MktDefinitions.IB_DURATION_1_DAY : MktDefinitions.IB_DURATION_2_DAY),
                    MktDefinitions.IB_BAR_5_MIN,
                    s.getInstrument().getSecType().getIbBarType(),
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
        dataSeriesDao.getSeriesByInterval(HtrEnums.Interval.MIN60).stream().filter(DataSeries::getActive).forEach(s -> {
            Contract contract = s.getInstrument().createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getInstrument().getSecType()) ? MktDefinitions.IB_ETH_TOO : MktDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrDefinitions.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    MktDefinitions.IB_DURATION_1_WEEK,
                    MktDefinitions.IB_BAR_1_HOUR,
                    s.getInstrument().getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        });
        l.info("END requestSixtyMinBars");
    }

    public void backfill(DataSeries dataSeries) {
        if (!dataSeries.getActive()) {
            l.info("Series not enabled, backfill won't be performed, seriesId=" + dataSeries.getId() + ", symbol=" + dataSeries.getInstrument().getSymbol());
            return;
        }
        l.info("START backfillManual, series=" + dataSeries.getId() + ", symbol=" + dataSeries.getInstrument().getSymbol());
        if (!ibController.isAnyActiveConnection()) {
            return;
        }
        Contract contract = dataSeries.getInstrument().createIbContract();
        Calendar now = HtrUtil.getCalendar();
        int isUseRTH = (HtrEnums.SecType.FUT.equals(dataSeries.getInstrument().getSecType()) ? MktDefinitions.IB_ETH_TOO : MktDefinitions.IB_RTH_ONLY);
        if (HtrEnums.Interval.MIN5.equals(dataSeries.getInterval())) {
            ibController.reqHistoricalData(
                    dataSeries.getId() * HtrDefinitions.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    MktDefinitions.IB_DURATION_10_DAY,
                    MktDefinitions.IB_BAR_5_MIN,
                    dataSeries.getInstrument().getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        } else if (HtrEnums.Interval.MIN60.equals(dataSeries.getInterval())) {
            int reqId = (dataSeries.getId() * HtrDefinitions.IB_REQUEST_MULT) + 4;
            Calendar his = HtrUtil.getCalendar();
            his.add(Calendar.MONTH, -3);
            for (int i = 0; i < 4; i++) {
                ibController.reqHistoricalData(
                        reqId--,
                        contract,
                        df.format(his.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                        MktDefinitions.IB_DURATION_1_MONTH,
                        MktDefinitions.IB_BAR_1_HOUR,
                        dataSeries.getInstrument().getSecType().getIbBarType(),
                        isUseRTH,
                        MktDefinitions.IB_FORMAT_DATE_MILLIS);
                his.add(Calendar.MONTH, 1);
                his.add(Calendar.DAY_OF_MONTH, -3); // some overlap
            }
            ibController.reqHistoricalData(
                    reqId,
                    contract,
                    df.format(now.getTime()) + " " + HtrDefinitions.IB_TIMEZONE,
                    MktDefinitions.IB_DURATION_1_MONTH,
                    MktDefinitions.IB_BAR_1_HOUR,
                    dataSeries.getInstrument().getSecType().getIbBarType(),
                    isUseRTH,
                    MktDefinitions.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END backfillManual, series=" + dataSeries.getId() + ", symbol=" + dataSeries.getInstrument().getSymbol());
    }
}
