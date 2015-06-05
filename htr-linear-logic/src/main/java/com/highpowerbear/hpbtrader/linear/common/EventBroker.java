package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.websocket.WebsocketController;
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
    private int numQuoteUpdates = 0;
    private int numStrategyUpdates = 0;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void trigger(LinEnums.DataChangeEvent dataChangeEvent) {
        if (LinEnums.DataChangeEvent.QUOTE_UPDATE.equals(dataChangeEvent)) {
            websocketController.broadcastSeriesMessage("qu=" + ++numQuoteUpdates);
        }  else if (LinEnums.DataChangeEvent.STRATEGY_UPDATE.equals(dataChangeEvent)) {
            websocketController.broadcastSeriesMessage("su=" + ++numStrategyUpdates);
        }
    }
}