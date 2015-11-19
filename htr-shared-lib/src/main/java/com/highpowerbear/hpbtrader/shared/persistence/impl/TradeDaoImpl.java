package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.entity.TradeLog;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
public class TradeDaoImpl implements TradeDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Trade> getTradesByStrategy(Strategy strategy, boolean ascending) {
        return null;
    }

    @Override
    public List<Trade> getTradesByOrder(IbOrder ibOrder) {
        return null;
    }

    @Override
    public Long getNumTrades(Strategy strategy) {
        return null;
    }

    @Override
    public Trade getActiveTrade(Strategy strategy) {
        return null;
    }

    @Override
    public Trade getLastTrade(Strategy strategy) {
        return null;
    }

    @Override
    public void updateTrade(Trade trade, Double price) {

    }

    @Override
    public List<TradeLog> getTradeLogs(Trade trade, boolean ascending) {
        return null;
    }
}
