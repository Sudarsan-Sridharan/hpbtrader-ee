package com.highpowerbear.hpbtrader.options.common;

import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.process.DataRetriever;
import com.highpowerbear.hpbtrader.options.process.SignalProcessor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author rkolar
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

    @Inject private OptDao optDao;
    @Inject private SignalProcessor signalProcessor;
    @Inject private DataRetriever dataRetriever;
    @Inject private OptData optData;
    @Inject private EventBroker eventBroker;

    public OptDao getOptDao() {
        return optDao;
    }

    public SignalProcessor getSignalProcessor() {
        return signalProcessor;
    }

    public DataRetriever getDataRetriever() {
        return dataRetriever;
    }

    public OptData getOptData() {
        return optData;
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }
}