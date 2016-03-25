package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.strategy.linear.BacktestResult;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogicContext;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class Backtester {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);
    @Inject private DataSeriesDao dataSeriesDao;

    private DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm");
    private static final int INDICATORS_LIST_SIZE = 10;
    
    BacktestResult backtest(Strategy dbStrategy, StrategyLogic strategyLogic, Calendar startDate, Calendar endDate) {
        String logMessage = "backtesting strategy, " + dbStrategy.getStrategyType().toString() + " --> " + strategyLogic.getName();
        DataSeries dataSeries = dataSeriesDao.getSeriesByAlias(dbStrategy.getDefaultInputSeriesAlias());
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, null);
        dataBars = filterBars(dataBars, startDate, endDate);
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
            IbOrder ibOrder = strategyLogic.processSignals();

            if (ibOrder == null) {
                if (ctx.activeTrade != null) {
                    backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
                }
                continue;
            }
            l.fine("Backtest iteration=" + i + ", new order, trigger=" + ibOrder.getTriggerDesc() + ", date=" + df.format(dataBar.getbCloseDate().getTime()));
            backtestResult.addOrder(ibOrder);
            ctx.activeTrade.addTradeOrder(ibOrder);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, dataBar.getbCloseDate(), null);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMITTED, dataBar.getbCloseDate(), null);
            ibOrder.addEvent(HtrEnums.IbOrderStatus.FILLED, dataBar.getbCloseDate(), null);
            ibOrder.setFillPrice(dataBar.getbClose());
            backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);

            ctx.strategy.setNumAllOrders(ctx.strategy.getNumAllOrders() + 1);
            ctx.strategy.setNumFilledOrders(ctx.strategy.getNumFilledOrders() + 1);
            ctx.strategy.setCurrentPosition(ibOrder.isBuyOrder() ? ctx.strategy.getCurrentPosition() + ibOrder.getQuantity() : ctx.strategy.getCurrentPosition() - ibOrder.getQuantity());

            if (ibOrder.isOpeningOrder()) {
                ctx.activeTrade.open(dataBar.getbClose());
            } else {
                ctx.activeTrade.initClose();
                backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
                ctx.activeTrade.close(dataBar.getbCloseDate(), dataBar.getbClose());
                ctx.strategy.recalculateStats(ctx.activeTrade);
            }
            backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);

            if (ibOrder.isReversalOrder()) {
                ctx.activeTrade = new Trade().initOpen(ibOrder);
                strategyLogic.setInitialStopAndTarget();
                ctx.activeTrade.addTradeOrder(ibOrder);
                backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
                ctx.activeTrade.open(dataBar.getbClose());
                backtestResult.updateOrCreateTrade(ctx.activeTrade, dataBar);
            }
            backtestResult.updateStrategy(ctx.strategy, dataBar);
        }
        l.info("END " + logMessage);
        return backtestResult;
    }
    
    private List<DataBar> filterBars(List<DataBar> dataBars, Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return dataBars;
        }
        return dataBars.stream()
                .filter(q -> q.getbCloseDateMillis() >= startDate.getTimeInMillis() && q.getbCloseDateMillis() <= endDate.getTimeInMillis())
                .collect(Collectors.toList());
    }
}
