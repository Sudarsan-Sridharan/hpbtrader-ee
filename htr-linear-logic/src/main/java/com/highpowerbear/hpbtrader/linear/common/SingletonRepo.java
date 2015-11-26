package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.strategy.OrderStateHandler;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
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

    @Inject private LinData linData;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private SeriesDao seriesDao;
    @Inject private IbController ibController;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private EventBroker eventBroker;
    @Inject private TiCalculator tiCalculator;

    public LinData getLinData() {
        return linData;
    }

    public IbOrderDao getIbOrderDao() {
        return ibOrderDao;
    }

    public SeriesDao getSeriesDao() {
        return seriesDao;
    }

    public IbController getIbController() {
        return ibController;
    }

    public OrderStateHandler getOrderStateHandler() {
        return orderStateHandler;
    }

    public HeartbeatControl getHeartbeatControl() {
        return heartbeatControl;
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public TiCalculator getTiCalculator() {
        return tiCalculator;
    }
}
