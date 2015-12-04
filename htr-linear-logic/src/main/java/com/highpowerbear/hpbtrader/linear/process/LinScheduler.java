package com.highpowerbear.hpbtrader.linear.process;

import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

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
    @Inject private LinData linData;
    @Inject private IbAccountDao ibAccountDao;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="21", timezone="US/Eastern", persistent=false)
    public void reconnect() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> {
            IbConnection c = ibController.getIbConnectionMap().get(ibAccount);
            if (c != null && c.getClientSocket() != null) {
                ibController.connect(ibAccount);
            }
        });
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="31", timezone="US/Eastern", persistent=false)
    private void requestOpenOrders() {
        ibAccountDao.getIbAccounts().stream().filter(ibController::isConnected).forEach(ibAccount -> {
            heartbeatControl.updateHeartbeats(ibAccount);
            ibController.requestOpenOrders(ibAccount);
        });
    }
}
