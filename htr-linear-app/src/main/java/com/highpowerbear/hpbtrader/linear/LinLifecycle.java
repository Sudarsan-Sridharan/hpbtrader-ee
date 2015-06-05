package com.highpowerbear.hpbtrader.linear;

import com.highpowerbear.hpbtrader.linear.common.SingletonRepo;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.ibclient.classifier.DefaultListener;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.ib.client.EWrapper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by robertk on 6/2/15.
 */
@Singleton
@Startup
public class LinLifecycle {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);

    @Inject private IbController ibController;
    @Inject private StrategyController strategyController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject @DefaultListener private EWrapper ibListener;
    @Inject private SingletonRepo singletonRepo;


    @PostConstruct
    public void startup() {
        l.info("BEGIN OptLinLifecycleLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        ibController.setIbListener(ibListener); // to prevent circural dependency
        strategyController.init();
        heartbeatControl.init();
        l.info("END LinLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN LinLifecycle.shutdown");
        l.info("END LinLifecycle.shutdown");
    }
}
