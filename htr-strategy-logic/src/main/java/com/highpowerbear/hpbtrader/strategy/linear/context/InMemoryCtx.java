package com.highpowerbear.hpbtrader.strategy.linear.context;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author robertk
 */
public class InMemoryCtx implements ProcessContext {
    private Strategy strategy;
    private Calendar currentDate;

    private List<StrategyLog> strategyLogs = new ArrayList<>();
    private List<IbOrder> ibOrders = new ArrayList<>(); // will include also OrderEvent list
    private List<Trade> trades = new ArrayList<>(); // will include also TradeIbOrder list
    private List<TradeLog> tradeLogs = new ArrayList<>();

    private Long nextStrategyLogId = 1L;
    private Long nextOrderId = 1L;
    private Long nextTradeId = 1L;
    private Long nextTradeLogId = 1L;

    public InMemoryCtx(Strategy strategy) {
        this.strategy = strategy;
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
        StrategyLog strategyLog = new StrategyLog();
        strategyLog.setId(nextStrategyLogId++);
        strategyLog.setStrategy(strategy);
        strategyLog.setStrategyMode(HtrEnums.StrategyMode.BTEST);
        strategyLog.setLogDate(currentDate);
        strategy.copyValuesTo(strategyLog);
        strategyLogs.add(strategyLog);
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
            activeTrade = dbActiveTrade.deepCopy(new Trade());
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
            trade.deepCopy(ctxTrade);
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
    public IbOrder findIbOrder(IbOrder ibOrder) {
        return ibOrder; // return the same
    }

    @Override
    public List<StrategyLog> getPagedStrategyLogs() {
        // TODO
        return null;
    }

    @Override
    public List<IbOrder> getPagedIbOrders() {
        // TODO
        return null;
    }

    @Override
    public List<Trade> getPagedTrades() {
        // TODO
        return null;
    }

    @Override
    public List<TradeLog> getPagedTradeLogs() {
        // TODO
        return null;
    }
}