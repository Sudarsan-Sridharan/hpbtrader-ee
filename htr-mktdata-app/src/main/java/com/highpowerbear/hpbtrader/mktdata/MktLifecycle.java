package com.highpowerbear.hpbtrader.mktdata;

import com.highpowerbear.hpbtrader.mktdata.common.MktDataDefinitions;
import com.highpowerbear.hpbtrader.mktdata.common.SingletonRepo;

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
public class MktLifecycle {
    private static final Logger l = Logger.getLogger(MktDataDefinitions.LOGGER);

    @Inject private SingletonRepo singletonRepo;

    @PostConstruct
    public void startup() {
        l.info("BEGIN MktLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        l.info("END MktLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN MktLifecycle.shutdown");
        l.info("END MktLifecycle.shutdown");
    }
}
