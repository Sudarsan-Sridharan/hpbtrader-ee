package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.entity.*;

import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 5/13/2016.
 */
public interface ProcessContext {
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

    List<StrategyLog> getPagedStrategyLogs(int start, int limit);
    List<IbOrder> getPagedIbOrders(int start, int limit);
    List<Trade> getPagedTrades(int start, int limit);
    List<TradeLog> getPagedTradeLogs(Trade trade, int start, int limit);

    Long getNumStrategyLogs();
    Long getNumIbOrders();
    Long getNumTrades();
    Long getNumTradeLogs(Trade trade);
}