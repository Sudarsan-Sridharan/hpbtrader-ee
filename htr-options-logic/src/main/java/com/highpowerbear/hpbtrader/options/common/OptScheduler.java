package com.highpowerbear.hpbtrader.options.common;

import com.highpowerbear.hpbtrader.options.data.ChainsRetriever;
import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.entity.OptionOrder;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.options.ibclient.IbController;
import com.highpowerbear.hpbtrader.options.model.ContractProperties;
import com.highpowerbear.hpbtrader.options.model.MarketData;
import com.highpowerbear.hpbtrader.options.model.ReadinessStatus;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.data.OptionDataRetriever;
import com.highpowerbear.hpbtrader.options.execution.StatusChecker;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Singleton
public class OptScheduler {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject private IbController ibController;
    @Inject private OptData optData;
    @Inject private OptDao optDao;
    @Inject private EventBroker eventBroker;
    @Inject private OptionDataRetriever optionDataRetriever;
    @Inject private ChainsRetriever chainsRetriever;
    @Inject private StatusChecker statusChecker;

    @Schedule(dayOfWeek="Mon-Fri", hour = "*", minute = "*", second="11", timezone="US/Eastern", persistent=false)
    private void ibReconnect() {
        if (!ibController.isConnected()) {
            ibController.connect();
            OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS * 2);
            if (ibController.isConnected()) {
                // request market data again for underlyings and active option contracts
                for (Integer reqId : optData.getMarketDataRequestMap().keySet()) {
                    String symbol = optData.getMarketDataRequestMap().get(reqId);
                    com.ib.client.Contract ibContract = OptUtil.constructIbContract(symbol);
                    ibController.requestRealtimeData(reqId, ibContract);
                }
            }
        }
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS * 2);
        if (ibController.isConnected()) {
            ibController.retrySubmit();
        } else {
           for (MarketData md : optData.getMarketDataMap().values()) {
               md.invalidatePrices();
               md.invalidateSizes();
           }
           eventBroker.trigger(OptEnums.DataChangeEvent.STRATEGY);
        }
    }

    @Schedule(dayOfWeek="Mon-Fri", hour = "*", minute = "*", second="21", timezone="US/Eastern", persistent=false)
    private void ibRequestOpenOrders() {
        if (ibController.isConnected()) {
            updateHeartbeat();
            ibController.requestOpenOrders();
        }
    }
    
    private void updateHeartbeat() {
        for (Long dbId : optData.getOpenOrderHeartbeatMap().keySet()) {
            Integer failedHeartbeatsLeft = optData.getOpenOrderHeartbeatMap().get(dbId);
            if (failedHeartbeatsLeft <= 0) {
                OptionOrder optionOrder = optDao.getOrder(dbId);
                if (!OptEnums.OrderStatus.UNKNOWN.equals(optionOrder.getOrderStatus())) {
                    optionOrder.addEvent(OptEnums.OrderStatus.UNKNOWN);
                    optDao.updateOrder(optionOrder);
                    Trade trade = optionOrder.getTrade();
                    trade.addEventByOrderUnknown(optionOrder);
                    optDao.updateTrade(trade);
                }
                optData.getOpenOrderHeartbeatMap().remove(dbId);
            } else {
                optData.getOpenOrderHeartbeatMap().put(dbId, failedHeartbeatsLeft - 1);
                eventBroker.trigger(OptEnums.DataChangeEvent.STRATEGY);
            }
        }
    }

    @Schedule(dayOfWeek="Mon-Fri", hour = "9", minute = "5", second="1", timezone="US/Eastern", persistent=false)
    private void reloadOptionChains() {
        if (!ibController.isConnected()) {
            return;
        }
        l.info("Periodic reload of option chains");
        chainsRetriever.reloadOptionChains();
    }
}
