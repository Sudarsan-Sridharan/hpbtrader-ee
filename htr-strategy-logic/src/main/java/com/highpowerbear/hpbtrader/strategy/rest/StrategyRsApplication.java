package com.highpowerbear.hpbtrader.strategy.rest;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rkolar
 */
@javax.ws.rs.ApplicationPath("rest")
public class StrategyRsApplication extends Application {
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> classes = new HashSet<>();

    public StrategyRsApplication(){
        classes.add(StrategyService.class);
        // singletons.add(new RestService());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}