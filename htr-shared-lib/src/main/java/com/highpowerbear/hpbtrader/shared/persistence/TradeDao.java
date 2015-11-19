package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.entity.TradeLog;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface TradeDao {
    List<Trade> getTradesByStrategy(Strategy strategy, boolean ascending);
    List<Trade> getTradesByOrder(IbOrder ibOrder);
    Long getNumTrades(Strategy strategy);
    Trade getActiveTrade(Strategy strategy);
    Trade getLastTrade(Strategy strategy);
    void updateTrade(Trade trade, Double price);
    List<TradeLog> getTradeLogs(Trade trade, boolean ascending);
}
