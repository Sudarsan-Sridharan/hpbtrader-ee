package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by robertk on 11/26/2015.
 */
@Singleton
public class MktDataScheduler {

    @Inject private HistDataController histDataController;
    @Inject private IbAccountDao ibAccountDao;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="1", timezone="US/Eastern", persistent=false)
    public void requestFiveMinBars() {
        histDataController.requestFiveMinBars();
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "0", second="11", timezone="US/Eastern", persistent=false)
    public void requestSixtyMinBars() {
        histDataController.requestSixtyMinBars();
    }
}
