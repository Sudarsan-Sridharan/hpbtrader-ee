package com.highpowerbear.hpbtrader.strategy.process.context;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.strategy.process.ProcessContext;

import javax.enterprise.context.Dependent;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author robertk
 */
@Dependent
public class InMemoryCtx implements ProcessContext {

    private Strategy strategy;
    private Calendar currentDate;

    private List<StrategyPerformance> strategyPerformances = new ArrayList<>();
    private List<IbOrder> ibOrders = new ArrayList<>(); // will include also OrderEvent list
    private List<Trade> trades = new ArrayList<>(); // will include also TradeIbOrder list
    private List<TradeLog> tradeLogs = new ArrayList<>();

    private Long nextStrategyLogId = 1L;
    private Long nextOrderId = 1L;
    private Long nextTradeId = 1L;
    private Long nextTradeLogId = 1L;

    @Override
    public ProcessContext configure(Strategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public Calendar getCurrentDate() {
        return this.currentDate;
    }

    @Override
    public Strategy getStrategy() {
        return this.strategy;
    }

    @Override
    public void updateStrategy() {
        StrategyPerformance p = strategy.createPerformance();
        p.setId(nextStrategyLogId++);
        strategyPerformances.add(p);
    }

    @Override
    public Trade getActiveTrade() {
        Trade dbActiveTrade = null;
        Trade activeTrade = null; // need to create a copy to simulate jpa detachment
        for (Trade t : trades) {
            if (HtrEnums.TradeStatus.INIT_OPEN.equals(t.getTradeStatus() ) || HtrEnums.TradeStatus.OPEN.equals(t.getTradeStatus())) {
                dbActiveTrade = t;
                break;
            }
        }
        if (dbActiveTrade != null) {
            activeTrade = dbActiveTrade.deepCopyTo(new Trade());
        }
        return activeTrade;
    }

    @Override
    public void updateOrCreateTrade(Trade trade, Double currentPrice) {
        Trade ctxTrade = findTrade(trade.getId());
        if (ctxTrade == null || !ctxTrade.valuesEqual(trade)) {
            TradeLog tradeLog = new TradeLog();
            tradeLog.setId(nextTradeLogId++);
            tradeLog.setTrade(trade);
            tradeLog.setLogDate(this.getCurrentDate());
            trade.copyValues(tradeLog);
            tradeLog.setPrice(currentPrice);
            tradeLogs.add(tradeLog);
        }
        if (ctxTrade == null) {
            trade.setId(nextTradeId++);
            trades.add(trade);
        } else {
            trade.deepCopyTo(ctxTrade);
        }
    }

    @Override
    public Trade findTrade(Long tradeId) {
        if (tradeId == null) {
            return null;
        }
        Optional<Trade> optional = trades.stream().filter(t -> (t.getId().equals(tradeId))).findAny();
        return optional.isPresent() ? optional.get() : null;
    }

    @Override
    public List<Trade> getTradesByOrder(IbOrder ibOrder) {
        return null;
    }

    @Override
    public void createIbOrder(IbOrder ibOrder) {
        ibOrder.setId(nextOrderId++);
        ibOrder.setStrategyMode(HtrEnums.StrategyMode.BTEST);
        ibOrders.add(ibOrder);
    }

    @Override
    public void updateIbOrder(IbOrder ibOrder) {
        // ignore
    }

    @Override
    public List<StrategyPerformance> getPagedStrategyLogs(int start, int limit) {
        Collections.reverse(strategyPerformances);
        List<StrategyPerformance> strategyPerformancePage = new ArrayList<>();
        for (int i = 0; i < strategyPerformances.size(); i++) {
            if (i >= start && i < (start + limit)) {
                strategyPerformancePage.add(strategyPerformances.get(i));
            }
        }
        return strategyPerformancePage;
    }

    @Override
    public List<IbOrder> getPagedIbOrders(int start, int limit) {
        Collections.reverse(ibOrders);
        List<IbOrder> ibOrdersPage = new ArrayList<>();
        for (int i = 0; i < ibOrders.size(); i++) {
            if (i >= start && i < (start + limit)) {
                ibOrdersPage.add(ibOrders.get(i));
            }
        }
        return ibOrdersPage;
    }

    @Override
    public List<Trade> getPagedTrades(int start, int limit) {
        Collections.reverse(trades);
        List<Trade> tradesPage = new ArrayList<>();
        for (int i = 0; i < trades.size(); i++) {
            if (i >= start && i < (start + limit)) {
                tradesPage.add(trades.get(i));
            }
        }
        return tradesPage;
    }

    @Override
    public List<TradeLog> getPagedTradeLogs(Trade trade, int start, int limit) {
        List<TradeLog> tradeLogsForTrade = getTradeLogsForTrade(trade);
        List<TradeLog> tradeLogsForTradePage = new ArrayList<>();
        for (int i = 0; i < tradeLogsForTrade.size(); i++) {
            if (i >= start && i < (start + limit)) {
                tradeLogsForTradePage.add(tradeLogsForTrade.get(i));
            }
        }
        return tradeLogsForTradePage;
    }

    @Override
    public Long getNumStrategyLogs() {
        return (long) strategyPerformances.size();
    }

    @Override
    public Long getNumIbOrders() {
        return (long) ibOrders.size();
    }

    @Override
    public Long getNumTrades() {
        return (long) trades.size();
    }

    @Override
    public Long getNumTradeLogs(Trade trade) {
        return (long) getTradeLogsForTrade(trade).size();
    }

    private List<TradeLog> getTradeLogsForTrade(Trade trade) {
        return tradeLogs.stream().filter(tl -> tl.getTrade().equals(trade)).collect(Collectors.toList());
    }
}