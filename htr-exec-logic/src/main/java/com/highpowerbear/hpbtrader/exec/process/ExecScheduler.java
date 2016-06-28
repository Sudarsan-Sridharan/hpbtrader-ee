package com.highpowerbear.hpbtrader.exec.process;

import com.highpowerbear.hpbtrader.exec.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.exec.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Created by rkolar on 4/10/14.
 */

@Singleton
public class ExecScheduler {
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private IbAccountDao ibAccountDao;
    @Inject private IbOrderDao ibOrderDao;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="31", timezone="US/Eastern", persistent=false)
    public void reconnect() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> {
            IbConnection c = ibController.getIbConnection(ibAccount);
            if (!c.isConnected() && c.isMarkConnected()) {
                c.connect();
            }
        });
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="41", timezone="US/Eastern", persistent=false)
    private void requestOpenOrders() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> {
            IbConnection c = ibController.getIbConnection(ibAccount);
            if (c.isConnected()) {
                heartbeatControl.updateHeartbeats(ibAccount);
                ibController.requestOpenOrders(ibAccount);
            }
        });
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="51", timezone="US/Eastern", persistent=false)
    public void retrySubmitOrders() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> ibOrderDao.getNewRetryIbOrders(ibAccount).forEach(ibController::submitIbOrder));
    }
}
