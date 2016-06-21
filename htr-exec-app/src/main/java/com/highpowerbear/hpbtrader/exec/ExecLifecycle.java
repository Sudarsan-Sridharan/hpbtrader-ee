package com.highpowerbear.hpbtrader.exec;

import com.highpowerbear.hpbtrader.exec.common.SingletonRepo;
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
public class ExecLifecycle {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject
    private SingletonRepo singletonRepo;

    @PostConstruct
    public void startup() {
        l.info("BEGIN ExecLifecycle.startup");
        SingletonRepo.setInstance(singletonRepo);
        l.info("END ExecLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN ExecLifecycle.shutdown");
        l.info("END ExecLifecycle.shutdown");
    }
}
