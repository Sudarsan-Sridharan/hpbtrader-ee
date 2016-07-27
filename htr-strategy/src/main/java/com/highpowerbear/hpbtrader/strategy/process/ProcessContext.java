package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.entity.*;

import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 5/13/2016.
 */
public interface ProcessContext {
    ProcessContext configure(Strategy strategy);

    void setCurrentDate(Calendar currentDate);
    Calendar getCurrentDate();

    Strategy getStrategy();
    void updateStrategy();

    Trade getActiveTrade();
    void updateOrCreateTrade(Trade trade, Double currentPrice);
    Trade findTrade(Long id);
    List<Trade> getTradesByOrder(IbOrder ibOrder);

    void createIbOrder(IbOrder ibOrder);
    void updateIbOrder(IbOrder ibOrder);

    List<StrategyPerformance> getStrategyPerformances(int start, int limit);
    List<IbOrder> getIbOrders(int start, int limit);
    List<Trade> getTrades(int start, int limit);
    List<TradeLog> getTradeLogs(Trade trade, int start, int limit);

    Long getNumStrategyPerformances();
    Long getNumIbOrders();
    Long getNumTrades();
    Long getNumTradeLogs(Trade trade);
}