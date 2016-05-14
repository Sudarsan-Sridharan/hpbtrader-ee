package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.entity.TradeLog;

import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface TradeDao {
    void updateOrCreateTrade(Trade trade, Calendar date, Double price);
    List<Trade> getTrades(Strategy strategy);
    List<Trade> getTradesByOrder(IbOrder ibOrder);
    Trade findTrade(Long id);
    Long getNumTrades(Strategy strategy);
    Trade getActiveTrade(Strategy strategy);
    Trade getLastTrade(Strategy strategy);
    List<Trade> getPagedTrades(Strategy strategy, int start, int limit);
    List<TradeLog> getPagedTradeLogs(Trade trade, int start, int limit);
}
