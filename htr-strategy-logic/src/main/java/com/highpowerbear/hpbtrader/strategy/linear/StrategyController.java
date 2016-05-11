package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.common.EmailSender;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.strategy.linear.logic.LuxorStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.MacdCrossStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.TestStrategyLogic;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@ApplicationScoped
public class StrategyController implements Serializable {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private StrategyDao strategyDao;
    @Inject private TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;

    @Inject private OrderStateHandler orderStateHandler;
    @Inject private EmailSender emailSender;

    private Map<Strategy, StrategyLogic> strategyLogicMap = new HashMap<>();

    @PostConstruct
    public void init() {
        strategyDao.getAllStrategies(false).forEach(this::swapStrategyLogic);
    }

    public void swapStrategyLogic(Strategy strategy) {
        if (strategy.isActive()) {
            strategyLogicMap.put(strategy, createStrategyLogic(strategy));
        } else {
            strategyLogicMap.remove(strategy);
        }
    }

    private StrategyLogic createStrategyLogic(Strategy strategy) {
        StrategyLogic strategyLogic = null;
        if (HtrEnums.StrategyType.MACD_CROSS.equals(strategy.getStrategyType())) {
            strategyLogic = new MacdCrossStrategyLogic(strategy);

        } else if (HtrEnums.StrategyType.TEST.equals(strategy.getStrategyType())) {
            strategyLogic = new TestStrategyLogic(strategy);

        } else if (HtrEnums.StrategyType.LUXOR.equals(strategy.getStrategyType())) {
            strategyLogic = new LuxorStrategyLogic(strategy);
        }
        return strategyLogic;
    }

    public void process(Strategy strategy) {
        StrategyLogic strategyLogic = strategyLogicMap.get(strategy);
        if (!strategyLogic.preflight(false)) {
            return;
        }
        l.info("START process " + strategyLogic.getInfo());
        IbOrder ibOrder = strategyLogic.process();
        postProcess(ibOrder, strategy, strategyLogic);
    }

    private void postProcess(IbOrder ibOrder, Strategy strategy, StrategyLogic strategyLogic) {
        Trade activeTrade = tradeDao.getActiveTrade(strategy);
        DataSeries inputDataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        DataBar lastDataBar = dataSeriesDao.getLastBar(inputDataSeries);
        if (ibOrder == null) {
            if (activeTrade != null) {
                tradeDao.updateOrCreateTrade(activeTrade, lastDataBar.getbBarClose());
            }
            l.info("END process " + strategyLogic.getInfo() + ", no new order");
            return;
        }
        ibOrderDao.createIbOrder(ibOrder);
        activeTrade.addTradeOrder(ibOrder);
        tradeDao.updateOrCreateTrade(activeTrade, lastDataBar.getbBarClose());

        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        activeTrade = tradeDao.getActiveTrade(strategy);

        if (ibOrder.isClosingOrder()) {
            activeTrade.initClose();
            tradeDao.updateOrCreateTrade(activeTrade, lastDataBar.getbBarClose());
        }
        if (ibOrder.isReversalOrder()) {
            activeTrade = new Trade().initOpen(ibOrder, activeTrade.getInitialStop(), activeTrade.getProfitTarget());
            activeTrade.addTradeOrder(ibOrder);
            tradeDao.updateOrCreateTrade(activeTrade, lastDataBar.getbBarClose());
        }

        strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        strategyDao.updateStrategy(strategy);

        // send email, notifying about new order
        String subject = ibOrder.getDescription();
        String content = ibOrder.getTriggerDesc() + "\n" + lastDataBar.print();
        emailSender.sendEmail(subject, content);

        l.info("END " + strategyLogic.getInfo() + ", new order, trigger=" + ibOrder.getTriggerDesc());

        if (HtrEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            //ibController.submitIbOrder(ibOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(ibOrder, lastDataBar);
        }
    }

    public void processManual(IbOrder manualIbOrder, Trade activeTrade, DataBar dataBar) {
        Strategy strategy = manualIbOrder.getStrategy();
        DataSeries dataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        String logMessage = "processing " + dataSeries.getInstrument().getSymbol() + ", " + dataSeries.getInstrument().getCurrency() + ", " + dataSeries.getInterval().name() + ", " +  strategy.getStrategyType() + " --> manual order";

        l.info("START " + logMessage);
        ibOrderDao.createIbOrder(manualIbOrder);
        activeTrade.addTradeOrder(manualIbOrder);
        tradeDao.updateOrCreateTrade(activeTrade, dataBar.getbBarClose());
        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        activeTrade = tradeDao.getActiveTrade(strategy);

        if (manualIbOrder.isClosingOrder()) {
            activeTrade.initClose();
            tradeDao.updateOrCreateTrade(activeTrade, dataBar.getbBarClose());
        }
        strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        strategyDao.updateStrategy(strategy);

        // send email, notifying about new order
        String subject = manualIbOrder.getDescription();
        String content = manualIbOrder.getTriggerDesc() + "\n" + dataBar.print();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + manualIbOrder.getTriggerDesc());

        if (HtrEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            //ibController.submitIbOrder(manualIbOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(manualIbOrder, dataBar);
        }
    }

    public BacktestResult backtest(Strategy strategy, Calendar startDate, Calendar endDate) {
        Strategy backtestStrategy = new Strategy(); // need to create new instance for backtest, copy required parameters
        strategy.deepCopy(backtestStrategy);
        backtestStrategy.resetStatistics();
        StrategyLogic backtestStrategyLogic = createStrategyLogic(backtestStrategy); // need to create new instance for backtest
        String logMessage = "backtesting strategy, " + strategy.getStrategyType().toString() + " --> " + backtestStrategyLogic.getClass().getSimpleName();
        DataSeries dataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, null);
        dataBars = filterBars(dataBars, startDate, endDate);
        /*
        int numIterations = dataBars.size() - HtrDefinitions.BARS_REQUIRED - INDICATORS_LIST_SIZE;
        l.info("START " + logMessage + ", iterations=" + numIterations);

        BacktestResult backtestResult = new BacktestResult(dbStrategy);
        if (dataBars.size() < HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END " + logMessage + ", not enough  bars available");
            return backtestResult;
        }

        for (int i = HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE; i < dataBars.size(); i++) {
            Strategy strategy = new Strategy();
            backtestResult.getStrategy().deepCopy(strategy);
            List<DataBar> iterationDataBars = dataBars.subList(0, i);
            DataBar dataBar = iterationDataBars.get(iterationDataBars.size() - 1);
            StrategyLogicContext ctx = new StrategyLogicContext();
            ctx.dataBars = iterationDataBars;
            ctx.strategy = strategy;
            ctx.activeTrade = backtestResult.getActiveTrade();
            ctx.isBacktest = true;

            strategyLogic.updateContext(ctx);
            IbOrder ibOrder = strategyLogic.process();

            if (ibOrder == null) {
                if (ctx.activeTrade != null) {
                    backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
                }
                continue;
            }
            l.fine("Backtest iteration=" + i + ", new order, trigger=" + ibOrder.getTriggerDesc() + ", date=" + df.format(dataBar.getBarCloseDate().getTime()));
            backtestResult.addOrder(ibOrder);
            ctx.activeTrade.addTradeOrder(ibOrder);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, dataBar.getBarCloseDate(), null);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMITTED, dataBar.getBarCloseDate(), null);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.FILLED, dataBar.getBarCloseDate(), null);
            ibOrder.setFillPrice(dataBar.getbBarClose());
            backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);

            ctx.strategy.setNumAllOrders(ctx.strategy.getNumAllOrders() + 1);
            ctx.strategy.setNumFilledOrders(ctx.strategy.getNumFilledOrders() + 1);
            ctx.strategy.setCurrentPosition(ibOrder.isBuyOrder() ? ctx.strategy.getCurrentPosition() + ibOrder.getQuantity() : ctx.strategy.getCurrentPosition() - ibOrder.getQuantity());

            if (ibOrder.isOpeningOrder()) {
                ctx.activeTrade.open(dataBar.getbBarClose());
            } else {
                ctx.activeTrade.initClose();
                backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
                ctx.activeTrade.close(dataBar.getBarCloseDate(), dataBar.getbBarClose());
                ctx.strategy.recalculateStats(ctx.activeTrade);
            }
            backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);

            if (ibOrder.isReversalOrder()) {
                ctx.activeTrade = new Trade().initOpen(ibOrder);
                strategyLogic.setInitialStopAndTarget();
                ctx.activeTrade.addTradeOrder(ibOrder);
                backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
                ctx.activeTrade.open(dataBar.getbBarClose());
                backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
            }
            backtestResult.updateStrategy(ctx.strategy, dataBar);
        }
        l.info("END " + logMessage);
        return backtestResult;
        */
        return null;
    }

    private List<DataBar> filterBars(List<DataBar> dataBars, Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return dataBars;
        }
        return dataBars.stream()
                .filter(q -> q.getBarCloseDateMillis() >= startDate.getTimeInMillis() && q.getBarCloseDateMillis() <= endDate.getTimeInMillis())
                .collect(Collectors.toList());
    }

}
