package com.highpowerbear.hpbtrader.options.common;

import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.data.DataRetriever;
import com.highpowerbear.hpbtrader.options.execution.SignalProcessor;
import com.highpowerbear.hpbtrader.shared.persistence.OptionDao;

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

    @Inject private OptionDao optionDao;
    @Inject private SignalProcessor signalProcessor;
    @Inject private DataRetriever dataRetriever;
    @Inject private OptData optData;
    @Inject private EventBroker eventBroker;

    public OptionDao getOptionDao() {
        return optionDao;
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