package com.highpowerbear.hpbtrader.linear;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.strategy.common.SingletonRepo;

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
public class StrategyLifecycle {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private SingletonRepo singletonRepo;

    @PostConstruct
    public void startup() {
        l.info("BEGIN StrategyLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        l.info("END StrategyLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN StrategyLifecycle.shutdown");
        l.info("END StrategyLifecycle.shutdown");
    }
}
