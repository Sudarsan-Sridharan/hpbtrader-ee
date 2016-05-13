package com.highpowerbear.hpbtrader.strategy.linear;

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
    IbOrder findIbOrder(IbOrder ibOrder);

    List<StrategyLog> getPagedStrategyLogs();
    List<IbOrder> getPagedIbOrders();
    List<Trade> getPagedTrades();
    List<TradeLog> getPagedTradeLogs();
}