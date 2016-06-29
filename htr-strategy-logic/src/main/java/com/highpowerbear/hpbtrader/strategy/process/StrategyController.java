package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.common.EmailSender;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.OperResult;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.strategy.message.MqSender;
import com.highpowerbear.hpbtrader.strategy.process.context.DatabaseCtx;
import com.highpowerbear.hpbtrader.strategy.process.context.InMemoryCtx;
import com.highpowerbear.hpbtrader.strategy.process.logic.LuxorStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.process.logic.MacdCrossStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.process.logic.TestStrategyLogic;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@ApplicationScoped
public class StrategyController implements Serializable {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private StrategyDao strategyDao;
    @Inject private DataSeriesDao dataSeriesDao;

    @Inject private OrderStateHandler orderStateHandler;
    @Inject private EmailSender emailSender;
    @Inject private MqSender mqSender;

    Map<Strategy, ProcessContext> tradingContextMap = new ConcurrentHashMap<>();
    Map<Strategy, StrategyLogic> tradingLogicMap = new ConcurrentHashMap<>();
    Map<Strategy, ProcessContext> backtestContextMap = new ConcurrentHashMap<>();
    Map<Strategy, Boolean> backtestStatusMap = new ConcurrentHashMap<>(); // false = in progress, true = finished

    @PostConstruct
    private void init() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            ProcessContext ctx = new DatabaseCtx(strategy);
            tradingContextMap.put(strategy, ctx);
            tradingLogicMap.put(strategy, createStrategyLogic(ctx));
        }
    }

    public ProcessContext getTradingContext(Strategy strategy) {
        return tradingContextMap.get(strategy);
    }

    public ProcessContext getBacktestContext(Strategy strategy) {
        return backtestContextMap.get(strategy);
    }

    public boolean isBacktestFinished(Strategy strategy) {
        Boolean ready = backtestStatusMap.get(strategy);
        return ready != null && Boolean.TRUE.equals(ready);
    }

    StrategyLogic createStrategyLogic(ProcessContext ctx) {
        StrategyLogic strategyLogic = null;
        if (HtrEnums.StrategyType.MACD_CROSS.equals(ctx.getStrategy().getStrategyType())) {
            strategyLogic = new MacdCrossStrategyLogic(ctx);

        } else if (HtrEnums.StrategyType.TEST.equals(ctx.getStrategy().getStrategyType())) {
            strategyLogic = new TestStrategyLogic(ctx);

        } else if (HtrEnums.StrategyType.LUXOR.equals(ctx.getStrategy().getStrategyType())) {
            strategyLogic = new LuxorStrategyLogic(ctx);
        }
        return strategyLogic;
    }

    void processStrategy(StrategyLogic sl) {
        ProcessContext ctx = sl.getProcessContext();
        Strategy str = sl.getStrategy();
        if (str.isActive()) {
            return;
        }
        String logMessage = " strategy, id=" + str.getId() + ", " + str.getDefaultInputSeriesAlias() + ", " + str.getStrategyType() + " --> " + sl.getClass().getSimpleName();

        l.info("BEGIN prepare " + logMessage);
        OperResult<Boolean, String> result;
        if (HtrEnums.StrategyMode.BTEST.equals(str.getStrategyMode())) {
            result = sl.prepare(ctx.getCurrentDate());
        } else {
            result = sl.prepare();
        }
        l.info("END prepare " + logMessage + ", " + result.getContent());
        if (!result.getStatus()) {
            return;
        }

        l.info("BEGIN process " + logMessage);
        sl.process();
        l.info("END process " + logMessage + (sl.getIbOrder() == null ? ", no new order" : ", new order, trigger=" + sl.getIbOrder().getTriggerDesc()));

        if (sl.getIbOrder() == null) {
            if (sl.getActiveTrade() != null) {
                ctx.updateOrCreateTrade(sl.getActiveTrade(), sl.getLastDataBar().getbBarClose());
            }
            return;
        }

        l.info("BEGIN postProcess " + logMessage);
        ctx.createIbOrder(sl.getIbOrder());
        sl.getActiveTrade().addTradeOrder(sl.getIbOrder());
        ctx.updateOrCreateTrade(sl.getActiveTrade(), sl.getLastDataBar().getbBarClose());
        Trade activeTrade = ctx.getActiveTrade(); // fresh copy
        if (sl.getIbOrder().isClosingOrder()) {
            activeTrade.initClose();
            ctx.updateOrCreateTrade(activeTrade, sl.getLastDataBar().getbBarClose());
        }
        if (sl.getIbOrder().isReversalOrder()) {
            activeTrade = new Trade().initOpen(sl.getIbOrder(), activeTrade.getInitialStop(), activeTrade.getProfitTarget());
            activeTrade.addTradeOrder(sl.getIbOrder());
            ctx.updateOrCreateTrade(activeTrade, sl.getLastDataBar().getbBarClose());
        }
        str.setNumAllOrders(str.getNumAllOrders() + 1);
        ctx.updateStrategy();

        if (HtrEnums.StrategyMode.IB.equals(str.getStrategyMode())) {
            emailSender.sendEmail(sl.getIbOrder().getDescription(), sl.getIbOrder().getTriggerDesc() + "\n" + sl.getLastDataBar().print());
            mqSender.newOrder(sl.getIbOrder());
        } else {
            orderStateHandler.simulateFill(ctx, sl.getIbOrder(), sl.getLastDataBar().getbBarClose());
        }
        l.info("END postProcess " + logMessage);
    }

    void backtestStrategy(Strategy str, Calendar fromDate, Calendar toDate) {
        Strategy btStrategy = str.deepCopyTo(new Strategy()).resetStatistics();
        btStrategy.setStrategyMode(HtrEnums.StrategyMode.BTEST);
        ProcessContext ctx = new InMemoryCtx(btStrategy);
        StrategyLogic sl = createStrategyLogic(ctx);

        String logMessage = " strategy, id=" + str.getId() + ", " + str.getDefaultInputSeriesAlias() + ", " + str.getStrategyType() + " --> " + sl.getClass().getSimpleName();
        l.info("BEGIN backtestStrategy " + logMessage);

        DataSeries inputDataSeries = dataSeriesDao.getDataSeriesByAlias(ctx.getStrategy().getDefaultInputSeriesAlias());
        int numIterations = (int) ((toDate.getTimeInMillis() - fromDate.getTimeInMillis()) / inputDataSeries.getBarType().getMillis());
        Calendar iterDate = Calendar.getInstance();
        iterDate.setTimeInMillis(fromDate.getTimeInMillis());
        int i = 0;
        while (iterDate.before(toDate)) {
            ctx.setCurrentDate(iterDate);
            l.info("id=" + str.getId() + ", iter=" + i + "/" + numIterations + ", date=" + HtrDefinitions.DF.format(iterDate.getTime()));
            this.processStrategy(sl);
        }
        backtestContextMap.put(str, ctx);
        backtestStatusMap.put(str, Boolean.TRUE);
        l.info("END backtestStrategy " + logMessage);
    }

    public void manualOrder(IbOrder ibOrder) {
        Strategy str = ibOrder.getStrategy();
        ProcessContext ctx = tradingContextMap.get(str);
        DataSeries inputDataSeries = dataSeriesDao.getDataSeriesByAlias(str.getDefaultInputSeriesAlias());
        DataBar dataBar = dataSeriesDao.getLastDataBars(inputDataSeries, 1).get(0);
        String logMessage = " strategy, id=" + str.getId() + ", " + str.getDefaultInputSeriesAlias() + ", " + str.getStrategyType() + " --> " + "manual order";

        l.info("BEGIN manualOrder " + logMessage);
        ctx.createIbOrder(ibOrder);
        ctx.getActiveTrade().addTradeOrder(ibOrder);
        ctx.updateOrCreateTrade(ctx.getActiveTrade(), dataBar.getbBarClose());
        Trade activeTrade = ctx.getActiveTrade(); // fresh copy

        if (ibOrder.isClosingOrder()) {
            activeTrade.initClose();
            ctx.updateOrCreateTrade(activeTrade, dataBar.getbBarClose());
        }
        str.setNumAllOrders(str.getNumAllOrders() + 1);
        ctx.updateStrategy();
        emailSender.sendEmail(ibOrder.getDescription(), ibOrder.getTriggerDesc() + "\n" + dataBar.print());

        if (HtrEnums.StrategyMode.IB.equals(str.getStrategyMode())) {
            mqSender.newOrder(ibOrder);
        } else {
            orderStateHandler.simulateFill(ctx, ibOrder, dataBar.getbBarClose());
        }
        l.info("END manualOrder " + logMessage + ", new order, trigger=" + ibOrder.getTriggerDesc());
    }
}
