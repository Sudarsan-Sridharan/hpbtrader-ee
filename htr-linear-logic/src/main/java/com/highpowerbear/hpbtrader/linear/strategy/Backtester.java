package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Bar;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.strategy.model.BacktestResult;
import com.highpowerbear.hpbtrader.linear.strategy.model.StrategyLogicContext;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class Backtester {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private DatabaseDao databaseDao;

    private DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm");
    private static final int INDICATORS_LIST_SIZE = 10;
    
    BacktestResult backtest(Strategy dbStrategy, StrategyLogic strategyLogic, Calendar startDate, Calendar endDate) {
        String logMessage = "backtesting strategy, " + dbStrategy.getStrategyType().toString() + " --> " + strategyLogic.getName();
        List<Bar> bars = databaseDao.getBars(dbStrategy.getSeries().getId(), null);
        bars = filterBars(bars, startDate, endDate);
        int numIterations = bars.size() - LinSettings.BARS_REQUIRED - INDICATORS_LIST_SIZE;
        l.info("START " + logMessage + ", iterations=" + numIterations);
        BacktestResult backtestResult = new BacktestResult(dbStrategy);
        if (bars.size() < LinSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END " + logMessage + ", not enough  bars available");
            return backtestResult;
        }
        
        for (int i = LinSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE; i < bars.size(); i++) {
            Strategy strategy = new Strategy();
            backtestResult.getStrategy().deepCopy(strategy);
            List<Bar> iterationBars = bars.subList(0, i);
            Bar bar = iterationBars.get(iterationBars.size() - 1);
            StrategyLogicContext ctx = new StrategyLogicContext();
            ctx.bars = iterationBars;
            ctx.strategy = strategy;
            ctx.activeTrade = backtestResult.getActiveTrade();
            ctx.isBacktest = true;
            
            strategyLogic.updateContext(ctx);
            Order order = strategyLogic.processSignals();
            
            if (order == null) {
                if (ctx.activeTrade != null) {
                    backtestResult.updateTrade(ctx.activeTrade, bar);
                }
                continue;
            }
            l.fine("Backtest iteration=" + i + ", new order, trigger=" + order.getTriggerDesc() + ", date=" + df.format(bar.getqDateBarClose().getTime()));
            backtestResult.addOrder(order);
            ctx.activeTrade.addTradeOrder(order);
            order.addEvent(LinEnums.OrderStatus.SUBMIT_REQ, bar.getqDateBarClose());
            order.addEvent(LinEnums.OrderStatus.SUBMITTED, bar.getqDateBarClose());
            order.addEvent(LinEnums.OrderStatus.FILLED, bar.getqDateBarClose());
            order.setFillPrice(bar.getqClose());
            backtestResult.updateTrade(ctx.activeTrade, bar);
            
            ctx.strategy.setNumAllOrders(ctx.strategy.getNumAllOrders() + 1);
            ctx.strategy.setNumFilledOrders(ctx.strategy.getNumFilledOrders() + 1);
            ctx.strategy.setCurrentPosition(order.isBuyOrder() ? ctx.strategy.getCurrentPosition() + order.getQuantity() : ctx.strategy.getCurrentPosition() - order.getQuantity());
            
            if (order.isOpeningOrder()) {   
                ctx.activeTrade.open(bar.getqClose());
            } else {
                ctx.activeTrade.initClose();
                backtestResult.updateTrade(ctx.activeTrade, bar);
                ctx.activeTrade.close(bar.getqDateBarClose(), bar.getqClose());
                ctx.strategy.recalculateStats(ctx.activeTrade);
            }
            backtestResult.updateTrade(ctx.activeTrade, bar);
            
            if (order.isReversalOrder()) {
                ctx.activeTrade = new Trade().initOpen(order);
                strategyLogic.setInitialStopAndTarget();
                ctx.activeTrade.addTradeOrder(order);
                backtestResult.updateTrade(ctx.activeTrade, bar);
                ctx.activeTrade.open(bar.getqClose());
                backtestResult.updateTrade(ctx.activeTrade, bar);
            }
            backtestResult.updateStrategy(ctx.strategy, bar);
        }
        l.info("END " + logMessage);
        return backtestResult;
    }
    
    private List<Bar> filterBars(List<Bar> bars, Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return bars;
        }
        List<Bar> filteredBars = new ArrayList<>();
        for (Bar q : bars) {
            if (q.getTimeInMillisBarClose() >= startDate.getTimeInMillis() && q.getTimeInMillisBarClose() <= endDate.getTimeInMillis()) {
                filteredBars.add(q);
            }
        }
        return filteredBars;
    }
}
