package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.strategy.model.BacktestResult;
import com.highpowerbear.hpbtrader.linear.strategy.model.StrategyLogicContext;
import com.highpowerbear.hpbtrader.persistence.DatabaseDao;
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
        List<Quote> quotes = databaseDao.getQuotes(dbStrategy.getSeries().getId(), null);
        quotes = filterQuotes(quotes, startDate, endDate);
        int numIterations = quotes.size() - LinSettings.BARS_REQUIRED - INDICATORS_LIST_SIZE;
        l.info("START " + logMessage + ", iterations=" + numIterations);
        BacktestResult backtestResult = new BacktestResult(dbStrategy);
        if (quotes.size() < LinSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END " + logMessage + ", not enough  quotes available");
            return backtestResult;
        }
        
        for (int i = LinSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE; i < quotes.size(); i++) {
            Strategy strategy = new Strategy();
            backtestResult.getStrategy().deepCopy(strategy);
            List<Quote> iterationQuotes = quotes.subList(0, i);
            Quote quote = iterationQuotes.get(iterationQuotes.size() - 1);
            StrategyLogicContext ctx = new StrategyLogicContext();
            ctx.quotes = iterationQuotes;
            ctx.strategy = strategy;
            ctx.activeTrade = backtestResult.getActiveTrade();
            ctx.isBacktest = true;
            
            strategyLogic.updateContext(ctx);
            Order order = strategyLogic.processSignals();
            
            if (order == null) {
                if (ctx.activeTrade != null) {
                    backtestResult.updateTrade(ctx.activeTrade, quote);
                }
                continue;
            }
            l.fine("Backtest iteration=" + i + ", new order, trigger=" + order.getTriggerDesc() + ", date=" + df.format(quote.getqDateBarClose().getTime()));
            backtestResult.addOrder(order);
            ctx.activeTrade.addTradeOrder(order);
            order.addEvent(LinEnums.OrderStatus.SUBMIT_REQ, quote.getqDateBarClose());
            order.addEvent(LinEnums.OrderStatus.SUBMITTED, quote.getqDateBarClose());
            order.addEvent(LinEnums.OrderStatus.FILLED, quote.getqDateBarClose());
            order.setFillPrice(quote.getqClose());
            backtestResult.updateTrade(ctx.activeTrade, quote);
            
            ctx.strategy.setNumAllOrders(ctx.strategy.getNumAllOrders() + 1);
            ctx.strategy.setNumFilledOrders(ctx.strategy.getNumFilledOrders() + 1);
            ctx.strategy.setCurrentPosition(order.isBuyOrder() ? ctx.strategy.getCurrentPosition() + order.getQuantity() : ctx.strategy.getCurrentPosition() - order.getQuantity());
            
            if (order.isOpeningOrder()) {   
                ctx.activeTrade.open(quote.getqClose());
            } else {
                ctx.activeTrade.initClose();
                backtestResult.updateTrade(ctx.activeTrade, quote);
                ctx.activeTrade.close(quote.getqDateBarClose(), quote.getqClose());
                ctx.strategy.recalculateStats(ctx.activeTrade);
            }
            backtestResult.updateTrade(ctx.activeTrade, quote);
            
            if (order.isReversalOrder()) {
                ctx.activeTrade = new Trade().initOpen(order);
                strategyLogic.setInitialStopAndTarget();
                ctx.activeTrade.addTradeOrder(order);
                backtestResult.updateTrade(ctx.activeTrade, quote);
                ctx.activeTrade.open(quote.getqClose());
                backtestResult.updateTrade(ctx.activeTrade, quote);
            }
            backtestResult.updateStrategy(ctx.strategy, quote);
        }
        l.info("END " + logMessage);
        return backtestResult;
    }
    
    private List<Quote> filterQuotes(List<Quote> quotes, Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return quotes;
        }
        List<Quote> filteredQuotes = new ArrayList<>();
        for (Quote q : quotes) {
            if (q.getTimeInMillisBarClose() >= startDate.getTimeInMillis() && q.getTimeInMillisBarClose() <= endDate.getTimeInMillis()) {
                filteredQuotes.add(q);
            }
        }
        return filteredQuotes;
    }
}
