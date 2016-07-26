package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.shared.rest.CodeMapService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertk on 19.11.2015.
 */
@ApplicationPath("rest")
public class MktDataRsApplication extends Application {
    private Set<Class<?>> classes = new HashSet<>();

    public MktDataRsApplication() {
        classes.add(IbAccountService.class);
        classes.add(DataSeriesService.class);
        classes.add(TechAnalysisService.class);
        classes.add(CodeMapService.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}