package com.highpowerbear.hpbtrader.strategy.common;

import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;
import com.highpowerbear.hpbtrader.strategy.process.OrderStateHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Created by rkolar on 4/25/14.
 */
@ApplicationScoped
public class SingletonRepo {
    private static SingletonRepo srepo;

    // should be used only within main to initialize
    public static void setInstance(SingletonRepo instance) {
        srepo = instance;
    }
    // should be used only in cases where spring cannot be used (jersey)
    public static SingletonRepo getInstance() {
        return srepo;
    }

    @PostConstruct
    private void init() {
        SingletonRepo.setInstance(this);
    }

    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private StrategyDao strategyDao;
    @Inject private TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private TiCalculator tiCalculator;

    public DataSeriesDao getDataSeriesDao() {
        return dataSeriesDao;
    }

    public StrategyDao getStrategyDao() {
        return strategyDao;
    }

    public TradeDao getTradeDao() {
        return tradeDao;
    }

    public IbOrderDao getIbOrderDao() {
        return ibOrderDao;
    }

    public OrderStateHandler getOrderStateHandler() {
        return orderStateHandler;
    }

    public TiCalculator getTiCalculator() {
        return tiCalculator;
    }
}
