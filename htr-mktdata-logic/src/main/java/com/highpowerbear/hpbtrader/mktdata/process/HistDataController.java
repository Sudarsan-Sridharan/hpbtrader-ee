package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataMaps;
import com.highpowerbear.hpbtrader.mktdata.common.MktDataDefinitions;
import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.common.HtrConstants;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
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
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@ApplicationScoped
public class HistDataController {
    private static final Logger l = Logger.getLogger(MktDataDefinitions.LOGGER);
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;
    @Inject private MktDataMaps mktDataMaps;
    @Inject private IbController ibController;

    private DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public void requestFiveMinBars(IbAccount ibAccount) {
        l.info("START requestFiveMinBars");
        if (!ibController.isConnected(ibAccount)) {
            l.info("Not connected");
            l.info("END requestFiveMinBars");
            return;
        }
        for (Series s : seriesDao.getSeriesByInterval(HtrEnums.Interval.INT_5_MIN)) {
            if (!s.getEnabled()) {
                continue;
            }
            mktDataMaps.getBarsReceivedMap().put(s, new LinkedHashMap<>());
            mktDataMaps.getBackfillStatusMap().put(s, null);
            Contract contract = s.createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? MktDataDefinitions.IB_ETH_TOO : MktDataDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    (HtrEnums.SecType.CASH.equals(s.getSecType()) ? MktDataDefinitions.IB_DURATION_1_DAY : MktDataDefinitions.IB_DURATION_2_DAY),
                    MktDataDefinitions.IB_BAR_5_MIN,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDataDefinitions.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END requestFiveMinBars");
    }

    public void requestSixtyMinBars(IbAccount ibAccount) {
        l.info("START requestSixtyMinBars");
        if (!ibController.isConnected(ibAccount)) {
            l.info("Not connected");
            l.info("END requestSixtyMinBars");
            return;
        }
        for (Series s : seriesDao.getSeriesByInterval(HtrEnums.Interval.INT_60_MIN)) {
            if (!s.getEnabled()) {
                continue;
            }
            mktDataMaps.getBarsReceivedMap().put(s, new LinkedHashMap<>());
            mktDataMaps.getBackfillStatusMap().put(s, null);
            Contract contract = s.createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? MktDataDefinitions.IB_ETH_TOO : MktDataDefinitions.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    MktDataDefinitions.IB_DURATION_1_WEEK,
                    MktDataDefinitions.IB_BAR_1_HOUR,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDataDefinitions.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END requestSixtyMinBars");
    }

    public void backfillManual(Series s) {
        if (!s.getEnabled()) {
            l.info("Series not enabled, backfill won't be performed, seriesId=" + s.getId() + ", symbol=" + s.getSymbol());
            return;
        }
        l.info("START backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
        if (!ibController.isConnected(s.getIbAccount())) {
            l.info("Not connected");
            l.info("END backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
            return;
        }
        Contract contract = s.createIbContract();
        Calendar now = HtrUtil.getCalendar();
        int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? MktDataDefinitions.IB_ETH_TOO : MktDataDefinitions.IB_RTH_ONLY);
        if (HtrEnums.Interval.INT_5_MIN.equals(s.getInterval())) {
            mktDataMaps.getBackfillStatusMap().put(s, 0);
            ibController.reqHistoricalData(
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    MktDataDefinitions.IB_DURATION_10_DAY,
                    MktDataDefinitions.IB_BAR_5_MIN,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDataDefinitions.IB_FORMAT_DATE_MILLIS);
        } else if (HtrEnums.Interval.INT_60_MIN.equals(s.getInterval())) {
            int backfillStatus = 4;
            mktDataMaps.getBackfillStatusMap().put(s, backfillStatus);
            int reqId = (s.getId() * HtrSettings.IB_REQUEST_MULT) + backfillStatus;
            Calendar his = HtrUtil.getCalendar();
            his.add(Calendar.MONTH, -3);
            for (int i = 0; i < 4; i++) {
                ibController.reqHistoricalData(
                        reqId--,
                        contract,
                        df.format(his.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                        MktDataDefinitions.IB_DURATION_1_MONTH,
                        MktDataDefinitions.IB_BAR_1_HOUR,
                        s.getSecType().getIbBarType(),
                        isUseRTH,
                        MktDataDefinitions.IB_FORMAT_DATE_MILLIS);
                his.add(Calendar.MONTH, 1);
                his.add(Calendar.DAY_OF_MONTH, -3); // some overlap
            }
            ibController.reqHistoricalData(
                    reqId,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    MktDataDefinitions.IB_DURATION_1_MONTH,
                    MktDataDefinitions.IB_BAR_1_HOUR,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    MktDataDefinitions.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
    }
}
