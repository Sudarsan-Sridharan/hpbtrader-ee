package com.highpowerbear.hpbtrader.strategy.common;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.strategy.websocket.WebsocketController;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class EventBroker {

    @Inject private WebsocketController websocketController;
    private int numBarUpdates = 0;
    private int numStrategyUpdates = 0;

    public void trigger(HtrEnums.DataChangeEvent dataChangeEvent) {
        if (HtrEnums.DataChangeEvent.BAR_UPDATE.equals(dataChangeEvent)) {
            websocketController.broadcastMessage("qu=" + ++numBarUpdates);

        }  else if (HtrEnums.DataChangeEvent.STRATEGY_UPDATE.equals(dataChangeEvent)) {
            websocketController.broadcastMessage("su=" + ++numStrategyUpdates);
        }
    }
}