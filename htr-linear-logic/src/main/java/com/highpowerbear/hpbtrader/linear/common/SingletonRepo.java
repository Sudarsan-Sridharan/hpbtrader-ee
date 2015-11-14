package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.mktdata.TiCalculator;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rkolar on 4/25/14.
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

    @Inject private TiCalculator tiCalculator;

    public TiCalculator getTiCalculator() {
        return tiCalculator;
    }
}
