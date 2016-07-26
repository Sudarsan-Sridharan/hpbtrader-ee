package com.highpowerbear.hpbtrader.strategy.rest;

import com.highpowerbear.hpbtrader.shared.rest.CodeMapService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rkolar
 */
@javax.ws.rs.ApplicationPath("rest")
public class StrategyRsApplication extends Application {
    private Set<Class<?>> classes = new HashSet<>();

    public StrategyRsApplication(){
        classes.add(StrategyService.class);
        classes.add(CodeMapService.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}