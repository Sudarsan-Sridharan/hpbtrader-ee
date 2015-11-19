package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.mktdata.MktDataController;
import com.highpowerbear.hpbtrader.linear.mktdata.TiCalculator;
import com.highpowerbear.hpbtrader.linear.persistence.LinDao;
import com.highpowerbear.hpbtrader.linear.strategy.OrderStateHandler;

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

    @Inject private TiCalculator tiCalculator;
    @Inject private LinData linData;
    @Inject private LinDao linDao;
    @Inject private IbController ibController;
    @Inject private MktDataController mktDataController;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private EventBroker eventBroker;

    public TiCalculator getTiCalculator() {
        return tiCalculator;
    }

    public LinData getLinData() {
        return linData;
    }

    public LinDao getLinDao() {
        return linDao;
    }

    public IbController getIbController() {
        return ibController;
    }

    public MktDataController getMktDataController() {
        return mktDataController;
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
}
