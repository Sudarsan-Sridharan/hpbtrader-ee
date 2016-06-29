package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.common.EmailSender;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.GenericTuple;
import com.highpowerbear.hpbtrader.shared.model.OperResult;
import com.highpowerbear.hpbtrader.shared.model.TimeFrame;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.strategy.message.MqSender;
import com.highpowerbear.hpbtrader.strategy.process.context.DatabaseCtx;
import com.highpowerbear.hpbtrader.strategy.process.context.InMemoryCtx;
import com.highpowerbear.hpbtrader.strategy.process.logic.LuxorStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.process.logic.MacdCrossStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.process.logic.TestStrategyLogic;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
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

    @Resource
    private ManagedExecutorService managedExecutorService;

    private Map<Strategy, ProcessContext> tradingContextMap = new ConcurrentHashMap<>();
    private Map<Strategy, StrategyLogic> tradingLogicMap = new ConcurrentHashMap<>();
    private Map<Strategy, BlockingQueue<String>> tradingQueueMap = new ConcurrentHashMap<>();
    private Map<Strategy, ProcessContext> backtestContextMap = new ConcurrentHashMap<>();
    private Map<Strategy, Boolean> backtestStatusMap = new ConcurrentHashMap<>(); // false = in progress, true = finished
    private BlockingQueue<GenericTuple<Strategy, TimeFrame>> backtestQueue = new ArrayBlockingQueue<>(HtrDefinitions.BLOCKING_QUEUE_CAPACITY);
    private final String POISON_PROCESS = "NO_SERIES";
    private final GenericTuple<Strategy, TimeFrame> POISON_BACKTEST = new GenericTuple<>(null, null);

    @PostConstruct
    private void init() {
        initProcess();
        initBacktest();
    }

    @PreDestroy
    public void finish() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            queueProcessStrategy(strategy, POISON_PROCESS);
        }
        queueBacktestStrategy(POISON_BACKTEST);
    }

    private void initProcess() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            ProcessContext ctx = new DatabaseCtx(strategy);
            tradingContextMap.put(strategy, ctx);
            tradingLogicMap.put(strategy, createStrategyLogic(ctx));
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(HtrDefinitions.BLOCKING_QUEUE_CAPACITY);
            tradingQueueMap.put(strategy, queue);
            managedExecutorService.submit(() -> {
                String seriesAlias = null;
                while (!Objects.equals(seriesAlias, POISON_PROCESS)) {
                    try {
                        seriesAlias = queue.take();
                        l.info("Process strategy request detected, strategy id=" + strategy.getId() + ", triggered by series=" + seriesAlias);
                        processStrategy(tradingLogicMap.get(strategy));
                    } catch (InterruptedException ie) {
                        l.warning(ie.getMessage());
                    }
                }
            });
        }
    }

    private void initBacktest() {
        managedExecutorService.submit(() -> {
            GenericTuple<Strategy, TimeFrame> backtestParam = null;
            while (!Objects.equals(backtestParam, POISON_BACKTEST)) {
                try {
                    backtestParam = backtestQueue.take();
                    l.info("Backtest strategy request detected, processing...");
                    Strategy strategy = backtestParam.getFirst();
                    TimeFrame timeFrame = backtestParam.getSecond();
                    if (timeFrame.isValid()) {
                        backtestStatusMap.put(strategy, Boolean.FALSE);
                        backtestStrategy(strategy, timeFrame.getFromDate(), timeFrame.getToDate());
                        backtestStatusMap.put(strategy, Boolean.TRUE);
                    }
                } catch (InterruptedException ie) {
                    l.warning(ie.getMessage());
                }
            }
        });
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

    private StrategyLogic createStrategyLogic(ProcessContext ctx) {
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

    public void queueProcessStrategy(Strategy strategy, String seriesAlias) {
        try {
            tradingQueueMap.get(strategy).put(seriesAlias);
        } catch (InterruptedException ie) {
            l.log(Level.SEVERE, "Error", ie);
        }
    }

    public void queueBacktestStrategy(GenericTuple<Strategy, TimeFrame> backtestParam) {
        try {
            backtestQueue.put(backtestParam);
        } catch (InterruptedException ie) {
            l.log(Level.SEVERE, "Error", ie);
        }
    }

    private void processStrategy(StrategyLogic sl) {
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

    private void backtestStrategy(Strategy str, Calendar fromDate, Calendar toDate) {
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
