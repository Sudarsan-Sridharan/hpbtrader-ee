package com.highpowerbear.hpbtrader.options;

import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

/**
 * Created by robertk on 6/2/15.
 */
@Singleton
@Startup
public class OptLifecycle {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @PostConstruct
    public void startup() {
        l.info("BEGIN OptLifecycle.startup");
        l.info("END OptLifecycle.startup");
    }

    @PreDestroy
    public void shutdown() {
        l.info("BEGIN OptLifecycle.shutdown");
        l.info("END OptLifecycle.shutdown");
    }
}
