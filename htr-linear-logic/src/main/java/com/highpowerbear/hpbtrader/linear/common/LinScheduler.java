package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.quote.QuoteController;
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
    @Inject private QuoteController quoteController;

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="21", timezone="US/Eastern", persistent=false)
    public void reconnect() {
        if (ibController.getIbClient() != null) {
            ibController.connect();
        }
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*", second="31", timezone="US/Eastern", persistent=false)
    private void requestOpenOrders() {
        if (ibController.isConnected()) {
            heartbeatControl.updateHeartbeats();
            ibController.requestOpenOrders();
        }
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "*/5", second="1", timezone="US/Eastern", persistent=false)
    public void requestFiveMinQuotes() {
        quoteController.requestFiveMinQuotes();
    }

    @Schedule(dayOfWeek="Sun-Fri", hour = "*", minute = "0", second="11", timezone="US/Eastern", persistent=false)
    public void requestSixtyMinQuotes() {
        quoteController.requestSixtyMinQuotes();
    }
}
