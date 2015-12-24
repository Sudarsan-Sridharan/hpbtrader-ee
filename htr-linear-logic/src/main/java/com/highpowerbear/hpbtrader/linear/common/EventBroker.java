package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@Singleton
public class EventBroker {

    @Inject private WebsocketController websocketController;
    private int numBarUpdates = 0;
    private int numStrategyUpdates = 0;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void trigger(HtrEnums.DataChangeEvent dataChangeEvent) {
        if (HtrEnums.DataChangeEvent.BAR_UPDATE.equals(dataChangeEvent)) {
            websocketController.broadcastSeriesMessage("qu=" + ++numBarUpdates);
        }  else if (HtrEnums.DataChangeEvent.STRATEGY_UPDATE.equals(dataChangeEvent)) {
            websocketController.broadcastSeriesMessage("su=" + ++numStrategyUpdates);
        }
    }
}