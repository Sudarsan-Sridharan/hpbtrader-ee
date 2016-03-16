package com.highpowerbear.hpbtrader.mktdata.common;

import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.message.MqSender;
import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.mktdata.process.RtDataController;
import com.highpowerbear.hpbtrader.mktdata.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;

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
    @Inject private MqSender mqSender;
    @Inject private IbController ibController;
    @Inject private HistDataController histDataController;
    @Inject private RtDataController rtDataController;
    @Inject private WebsocketController websocketController;

    public IbOrderDao getIbOrderDao() {
        return ibOrderDao;
    }

    public DataSeriesDao getDataSeriesDao() {
        return dataSeriesDao;
    }

    public MqSender getMqSender() {
        return mqSender;
    }

    public IbController getIbController() {
        return ibController;
    }

    public HistDataController getHistDataController() {
        return histDataController;
    }

    public RtDataController getRtDataController() {
        return rtDataController;
    }

    public WebsocketController getWebsocketController() {
        return websocketController;
    }
}
