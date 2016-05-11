package com.highpowerbear.hpbtrader.strategy.common;

import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.strategy.linear.OrderStateHandler;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rkolar on 4/25/14.
 */
@Named
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

    @Inject private IbOrderDao ibOrderDao;
    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private TradeDao tradeDao;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private EventBroker eventBroker;
    @Inject private TiCalculator tiCalculator;

    public IbOrderDao getIbOrderDao() {
        return ibOrderDao;
    }

    public DataSeriesDao getDataSeriesDao() {
        return dataSeriesDao;
    }

    public TradeDao getTradeDao() {
        return tradeDao;
    }

    public OrderStateHandler getOrderStateHandler() {
        return orderStateHandler;
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public TiCalculator getTiCalculator() {
        return tiCalculator;
    }
}
