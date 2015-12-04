package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by robertk on 11/26/2015.
 */
@Singleton
public class MktScheduler {

    @Inject private HistDataController histDataController;
    @Inject private IbAccountDao ibAccountDao;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="1", timezone="US/Eastern", persistent=false)
    public void requestFiveMinBars() {
        ibAccountDao.getIbAccounts().forEach(histDataController::requestFiveMinBars);
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "0", second="11", timezone="US/Eastern", persistent=false)
    public void requestSixtyMinBars() {
        ibAccountDao.getIbAccounts().forEach(histDataController::requestSixtyMinBars);
    }
}
