package com.highpowerbear.hpbtrader.mktdata;

import com.highpowerbear.hpbtrader.mktdata.common.SingletonRepo;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
@Singleton
@Startup
public class MktDataLifecycle {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private SingletonRepo singletonRepo;

    @PostConstruct
    public void startup() {
        l.info("BEGIN MktDataLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        l.info("END MktDataLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN MktDataLifecycle.shutdown");
        l.info("END MktDataLifecycle.shutdown");
    }
}
