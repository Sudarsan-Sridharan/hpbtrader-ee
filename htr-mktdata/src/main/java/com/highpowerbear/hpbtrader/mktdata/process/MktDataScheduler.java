package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * Created by robertk on 11/26/2015.
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MktDataScheduler {

    @Inject private HistDataController histDataController;
    @Inject private IbAccountDao ibAccountDao;
    @Inject private IbController ibController;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="1", timezone="US/Eastern", persistent=false)
    public void requestFiveMinBars() {
        histDataController.requestFiveMinBars();
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "0", second="11", timezone="US/Eastern", persistent=false)
    public void requestSixtyMinBars() {
        histDataController.requestSixtyMinBars();
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="21", timezone="US/Eastern", persistent=false)
    public void reconnect() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> {
            IbConnection c = ibController.getIbConnection(ibAccount);
            if (!c.isConnected() && c.isMarkConnected()) {
                c.connect();
            }
        });
    }
}
