package com.highpowerbear.hpbtrader.linear.quote;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinConstants;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.quote.model.RealtimeData;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogic;
import com.highpowerbear.hpbtrader.linear.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.persistence.DatabaseDao;
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
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@Singleton
public class QuoteController {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private DatabaseDao databaseDao;
    @Inject private LinData linData;
    @Inject private StrategyController strategyController;
    @Inject private IbController ibController;
    @Inject private EventBroker eventBroker;
    @Inject private WebsocketController websocketController;

    private DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public void requestFiveMinQuotes() {
        l.info("START requestFiveMinQuotes");
        if (!ibController.isConnected()) {
            l.info("Not connected");
            l.info("END requestFiveMinQuotes");
            return;
        }
        for (Series s : databaseDao.getSeriesByInterval(LinEnums.Interval.INT_5_MIN)) {
            if (!s.getIsEnabled()) {
                continue;
            }
            linData.getQuotesReceivedMap().put(s.getId(), new ArrayList<>());
            linData.getBackfillStatusMap().put(s.getId(), null);
            Contract contract = s.createIbContract();
            Calendar now = LinUtil.getCalendar();
            int isUseRTH = (LinEnums.SecType.FUT.equals(s.getSecType()) ? LinConstants.IB_ETH_TOO : LinConstants.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * LinSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + LinConstants.IB_TIMEZONE,
                    (LinEnums.SecType.CASH.equals(s.getSecType()) ? LinConstants.IB_DURATION_1_DAY : LinConstants.IB_DURATION_2_DAY),
                    LinConstants.IB_BAR_5_MIN,
                    s.getSecType().getIbQuoteType(),
                    isUseRTH,
                    LinConstants.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END requestFiveMinQuotes");
    }

    public void requestSixtyMinQuotes() {
        l.info("START requestSixtyMinQuotes");
        if (!ibController.isConnected()) {
            l.info("Not connected");
            l.info("END requestSixtyMinQuotes");
            return;
        }
        for (Series s : databaseDao.getSeriesByInterval(LinEnums.Interval.INT_60_MIN)) {
            if (!s.getIsEnabled()) {
                continue;
            }
            linData.getQuotesReceivedMap().put(s.getId(), new ArrayList<>());
            linData.getBackfillStatusMap().put(s.getId(), null);
            Contract contract = s.createIbContract();
            Calendar now = LinUtil.getCalendar();
            int isUseRTH = (LinEnums.SecType.FUT.equals(s.getSecType()) ? LinConstants.IB_ETH_TOO : LinConstants.IB_RTH_ONLY);
            ibController.reqHistoricalData(
                    s.getId() * LinSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + LinConstants.IB_TIMEZONE,
                    LinConstants.IB_DURATION_1_WEEK,
                    LinConstants.IB_BAR_1_HOUR,
                    s.getSecType().getIbQuoteType(),
                    isUseRTH,
                    LinConstants.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END requestSixtyMinQuotes");
    }

    public void backfillManual(Series s) {
        if (!s.getIsEnabled()) {
            l.info("Series not enabled, backfill won't be performed, seriesId=" + s.getId() + ", symbol=" + s.getSymbol());
            return;
        }
        l.info("START backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
        if (!ibController.isConnected()) {
            l.info("Not connected");
            l.info("END backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
            return;
        }
        linData.getQuotesReceivedMap().put(s.getId(), new ArrayList<>());
        Contract contract = s.createIbContract();
        Calendar now = LinUtil.getCalendar();
        int isUseRTH = (LinEnums.SecType.FUT.equals(s.getSecType()) ? LinConstants.IB_ETH_TOO : LinConstants.IB_RTH_ONLY);
        if (LinEnums.Interval.INT_5_MIN.equals(s.getInterval())) {
            linData.getBackfillStatusMap().put(s.getId(), 0);
            ibController.reqHistoricalData(
                    s.getId() * LinSettings.IB_REQUEST_MULT,
                    contract,
                    df.format(now.getTime()) + " " + LinConstants.IB_TIMEZONE,
                    LinConstants.IB_DURATION_10_DAY,
                    LinConstants.IB_BAR_5_MIN,
                    s.getSecType().getIbQuoteType(),
                    isUseRTH,
                    LinConstants.IB_FORMAT_DATE_MILLIS);
        } else if (LinEnums.Interval.INT_60_MIN.equals(s.getInterval())) {
            int backfillStatus = 4;
            linData.getBackfillStatusMap().put(s.getId(), backfillStatus);
            int reqId = (s.getId() * LinSettings.IB_REQUEST_MULT) + backfillStatus;
            Calendar his = LinUtil.getCalendar();
            his.add(Calendar.MONTH, -3);
            for (int i = 0; i < 4; i++) {
                ibController.reqHistoricalData(
                        reqId--,
                        contract,
                        df.format(his.getTime()) + " " + LinConstants.IB_TIMEZONE,
                        LinConstants.IB_DURATION_1_MONTH,
                        LinConstants.IB_BAR_1_HOUR,
                        s.getSecType().getIbQuoteType(),
                        isUseRTH,
                        LinConstants.IB_FORMAT_DATE_MILLIS);
                his.add(Calendar.MONTH, 1);
                his.add(Calendar.DAY_OF_MONTH, -3); // some overlap
            }
            ibController.reqHistoricalData(
                    reqId,
                    contract,
                    df.format(now.getTime()) + " " + LinConstants.IB_TIMEZONE,
                    LinConstants.IB_DURATION_1_MONTH,
                    LinConstants.IB_BAR_1_HOUR,
                    s.getSecType().getIbQuoteType(),
                    isUseRTH,
                    LinConstants.IB_FORMAT_DATE_MILLIS);
        }
        l.info("END backfillManual, series=" + s.getId() + ", symbol=" + s.getSymbol());
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void processQuotes(Series series) {
        if (!series.getIsEnabled()) {
            l.info("Series not enabled, quotes won't be processed, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
            return;
        }
        l.info("START processQuotes, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
        List<Quote> quotesReceived = linData.getQuotesReceivedMap().get(series.getId());
        Integer backfillStatus = linData.getBackfillStatusMap().get(series.getId());
        l.info("backfillStatus=" + backfillStatus);
        if (backfillStatus == null || backfillStatus == 0) {
            databaseDao.addQuotes(quotesReceived);
            Strategy activeStrategy = databaseDao.getActiveStrategy(series);
            StrategyLogic strategyLogic = linData.getStrategyLogicMap().get(series.getId());
            if (databaseDao.getNumQuotes(series) >= LinSettings.BARS_REQUIRED) {
                strategyController.process(activeStrategy, strategyLogic);
            }
            eventBroker.trigger(LinEnums.DataChangeEvent.QUOTE_UPDATE);
        } else {
            linData.getBackfillStatusMap().put(series.getId(), --backfillStatus);
        }
        l.info("END processQuotes, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
    }

    public void toggleRealtimeData(Series series) {
        RealtimeData rtd = linData.getRealtimeDataMap().get(series.getSymbol());
        if (rtd == null) {
            rtd = new RealtimeData(series);
            l.fine("Requesting realtime data for " + rtd.getSeries().getSymbol());
            linData.getRealtimeDataRequestMap().put(rtd.getIbRequestId(), rtd.getSeries().getSymbol());
            linData.getRealtimeDataMap().put(rtd.getSeries().getSymbol(), rtd);
            boolean requested = ibController.requestRealtimeData(rtd.getIbRequestId(), rtd.getSeries().createIbContract());
            if (!requested) {
                linData.getRealtimeDataRequestMap().remove(rtd.getIbRequestId());
                linData.getRealtimeDataMap().remove(rtd.getSeries().getSymbol());
            }
        } else {
            l.fine("Canceling realtime data for " + rtd.getSeries().getSymbol());
            boolean canceled = ibController.cancelRealtimeData(rtd.getIbRequestId());
            if (canceled) {
                linData.getRealtimeDataRequestMap().remove(rtd.getIbRequestId());
                linData.getRealtimeDataMap().remove(rtd.getSeries().getSymbol());
            }
        }
    }

    @Asynchronous
    public void updateRealtimeData(int reqId, int field, double price) {
        String contractSymbol = linData.getRealtimeDataRequestMap().get(reqId);
        if (contractSymbol == null) {
            return;
        }
        RealtimeData rtd = linData.getRealtimeDataMap().get(contractSymbol);
        String updateMessage = rtd.createUpdateMessage(field, price);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
            if (field == TickType.LAST || (field == TickType.ASK && LinEnums.SecType.CASH.equals(rtd.getSeries().getSecType()))) {
                String updateMessageChangePct = rtd.createChangePctUpdateMsg();
                websocketController.broadcastSeriesMessage(updateMessageChangePct);
            }
        }
    }

    @Asynchronous
    public void updateRealtimeData(int reqId, int field, int size) {
        String contractSymbol = linData.getRealtimeDataRequestMap().get(reqId);
        if (contractSymbol == null) {
            return;
        }
        RealtimeData rtd = linData.getRealtimeDataMap().get(contractSymbol);
        String updateMessage = rtd.createUpdateMessage(field, size);
        if (updateMessage != null) {
            websocketController.broadcastSeriesMessage(updateMessage);
        }
    }
}
