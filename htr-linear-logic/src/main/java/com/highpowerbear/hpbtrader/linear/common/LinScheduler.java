package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.entity.IbAccount;
import com.highpowerbear.hpbtrader.linear.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.mktdata.MktDataController;
import com.highpowerbear.hpbtrader.linear.model.IbConnection;
import com.highpowerbear.hpbtrader.linear.persistence.LinDao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by rkolar on 4/10/14.
 */

@Singleton
public class LinScheduler {
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private MktDataController mktDataController;
    @Inject private LinData linData;
    @Inject private LinDao linDao;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="21", timezone="US/Eastern", persistent=false)
    public void reconnect() {
        for (IbAccount ibAccount : linDao.getIbAccounts()) {
            IbConnection c = linData.getIbConnectionMap().get(ibAccount);
            if (c == null) { // can happen at application startup when not fully initialized yet
                return;
            }
            if (c.getClientSocket() != null) {
                ibController.connect(ibAccount);
            }
        }
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="31", timezone="US/Eastern", persistent=false)
    private void requestOpenOrders() {
        linDao.getIbAccounts().stream().filter(ibController::isConnected).forEach(ibAccount -> {
            heartbeatControl.updateHeartbeats(ibAccount);
            ibController.requestOpenOrders(ibAccount);
        });
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="1", timezone="US/Eastern", persistent=false)
    public void requestFiveMinBars() {
        linDao.getIbAccounts().forEach(mktDataController::requestFiveMinBars);
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "0", second="11", timezone="US/Eastern", persistent=false)
    public void requestSixtyMinBars() {
        linDao.getIbAccounts().forEach(mktDataController::requestSixtyMinBars);
    }
}
