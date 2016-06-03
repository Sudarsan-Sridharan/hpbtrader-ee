package com.highpowerbear.hpbtrader.exec.common;

import com.highpowerbear.hpbtrader.exec.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.exec.ibclient.IbController;
import com.highpowerbear.hpbtrader.exec.message.MqSender;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;

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

    @Inject private IbOrderDao ibOrderDao;
    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private TiCalculator tiCalculator;
    @Inject private MqSender mqSender;

    public IbOrderDao getIbOrderDao() {
        return ibOrderDao;
    }

    public DataSeriesDao getDataSeriesDao() {
        return dataSeriesDao;
    }

    public IbController getIbController() {
        return ibController;
    }

    public HeartbeatControl getHeartbeatControl() {
        return heartbeatControl;
    }

    public TiCalculator getTiCalculator() {
        return tiCalculator;
    }

    public MqSender getMqSender() {
        return mqSender;
    }
}
