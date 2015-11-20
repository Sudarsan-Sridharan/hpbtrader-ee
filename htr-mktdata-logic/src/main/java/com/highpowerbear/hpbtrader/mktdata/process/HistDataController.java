package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataMaps;
import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.shared.common.HtrConstants;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.ib.client.Contract;
import com.ib.client.TickType;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@Singleton
public class HistDataController {
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;
    @Inject private MktDataMaps mktDataMaps;
    @Inject private IbController ibController;
    @Inject private WebsocketController websocketController;

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
            mktDataMaps.getBarsReceivedMap().put(s.getId(), new ArrayList<>());
            mktDataMaps.getBackfillStatusMap().put(s.getId(), null);
            Contract contract = s.createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? HtrConstants.IB_ETH_TOO : HtrConstants.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getIbAccount(),
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    (HtrEnums.SecType.CASH.equals(s.getSecType()) ? HtrConstants.IB_DURATION_1_DAY : HtrConstants.IB_DURATION_2_DAY),
                    HtrConstants.IB_BAR_5_MIN,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    HtrConstants.IB_FORMAT_DATE_MILLIS);
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
            mktDataMaps.getBarsReceivedMap().put(s.getId(), new ArrayList<>());
            mktDataMaps.getBackfillStatusMap().put(s.getId(), null);
            Contract contract = s.createIbContract();
            Calendar now = HtrUtil.getCalendar();
            int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? HtrConstants.IB_ETH_TOO : HtrConstants.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getIbAccount(),
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    HtrConstants.IB_DURATION_1_WEEK,
                    HtrConstants.IB_BAR_1_HOUR,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    HtrConstants.IB_FORMAT_DATE_MILLIS);
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
        mktDataMaps.getBarsReceivedMap().put(s.getId(), new ArrayList<>());
        Contract contract = s.createIbContract();
        Calendar now = HtrUtil.getCalendar();
        int isUseRTH = (HtrEnums.SecType.FUT.equals(s.getSecType()) ? HtrConstants.IB_ETH_TOO : HtrConstants.IB_RTH_ONLY);
        if (HtrEnums.Interval.INT_5_MIN.equals(s.getInterval())) {
            mktDataMaps.getBackfillStatusMap().put(s.getId(), 0);
            ibController.reqHistoricalData(
                    s.getIbAccount(),
                    s.getId() * HtrSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    HtrConstants.IB_DURATION_10_DAY,
                    HtrConstants.IB_BAR_5_MIN,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    HtrConstants.IB_FORMAT_DATE_MILLIS);
        } else if (HtrEnums.Interval.INT_60_MIN.equals(s.getInterval())) {
            int backfillStatus = 4;
            mktDataMaps.getBackfillStatusMap().put(s.getId(), backfillStatus);
            int reqId = (s.getId() * HtrSettings.IB_REQUEST_MULT) + backfillStatus;
            Calendar his = HtrUtil.getCalendar();
            his.add(Calendar.MONTH, -3);
            for (int i = 0; i < 4; i++) {
                ibController.reqHistoricalData(
                        s.getIbAccount(),
                        reqId--,
                        contract,
                        df.format(his.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                        HtrConstants.IB_DURATION_1_MONTH,
                        HtrConstants.IB_BAR_1_HOUR,
                        s.getSecType().getIbBarType(),
                        isUseRTH,
                        HtrConstants.IB_FORMAT_DATE_MILLIS);
                his.add(Calendar.MONTH, 1);
                his.add(Calendar.DAY_OF_MONTH, -3); // some overlap
            }
            ibController.reqHistoricalData(
                    s.getIbAccount(),
                    reqId,
                    contract,
                    df.format(now.getTime()) + " " + HtrConstants.IB_TIMEZONE,
                    HtrConstants.IB_DURATION_1_MONTH,
                    HtrConstants.IB_BAR_1_HOUR,
                    s.getSecType().getIbBarType(),
                    isUseRTH,
                    HtrConstants.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void processBars(Series series) {
    }

    public void toggleRealtimeData(Series series) {
        RealtimeData rtd = mktDataMaps.getRealtimeDataMap().get(series.getSymbol());
        if (rtd == null) {
            rtd = new RealtimeData(series);
            l.fine("Requesting realtime data for " + rtd.getSeries().getSymbol());
            mktDataMaps.getRealtimeDataRequestMap().put(rtd.getIbRequestId(), rtd.getSeries().getSymbol());
            mktDataMaps.getRealtimeDataMap().put(rtd.getSeries().getSymbol(), rtd);
            boolean requested = ibController.requestRealtimeData(series.getIbAccount(), rtd.getIbRequestId(), rtd.getSeries().createIbContract());
            if (!requested) {
                mktDataMaps.getRealtimeDataRequestMap().remove(rtd.getIbRequestId());
                mktDataMaps.getRealtimeDataMap().remove(rtd.getSeries().getSymbol());
            }
        } else {
            l.fine("Canceling realtime data for " + rtd.getSeries().getSymbol());
            boolean canceled = ibController.cancelRealtimeData(series.getIbAccount(), rtd.getIbRequestId());
            if (canceled) {
                mktDataMaps.getRealtimeDataRequestMap().remove(rtd.getIbRequestId());
                mktDataMaps.getRealtimeDataMap().remove(rtd.getSeries().getSymbol());
            }
        }
    }

    @Asynchronous
    public void updateRealtimeData(int reqId, int field, double price) {
        String contractSymbol = mktDataMaps.getRealtimeDataRequestMap().get(reqId);
        if (contractSymbol == null) {
            return;
        }
        RealtimeData rtd = mktDataMaps.getRealtimeDataMap().get(contractSymbol);
        String updateMessage = rtd.createUpdateMessage(field, price);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
            if (field == TickType.LAST || (field == TickType.ASK && HtrEnums.SecType.CASH.equals(rtd.getSeries().getSecType()))) {
                String updateMessageChangePct = rtd.createChangePctUpdateMsg();
                websocketController.broadcastSeriesMessage(updateMessageChangePct);
            }
        }
    }

    @Asynchronous
    public void updateRealtimeData(int reqId, int field, int size) {
        String contractSymbol = mktDataMaps.getRealtimeDataRequestMap().get(reqId);
        if (contractSymbol == null) {
            return;
        }
        RealtimeData rtd = mktDataMaps.getRealtimeDataMap().get(contractSymbol);
        String updateMessage = rtd.createUpdateMessage(field, size);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
        }
    }
}
