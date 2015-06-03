package com.highpowerbear.hpbtrader.options.common;

import com.highpowerbear.hpbtrader.options.entity.IbOrder;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.options.ibclient.IbController;
import com.highpowerbear.hpbtrader.options.model.ContractProperties;
import com.highpowerbear.hpbtrader.options.model.MarketData;
import com.highpowerbear.hpbtrader.options.model.ReadinessStatus;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.process.OptionDataRetriever;
import com.highpowerbear.hpbtrader.options.process.StatusChecker;
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
           eventBroker.trigger(OptEnums.DataChangeEvent.MARKET_DATA);
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
                IbOrder ibOrder = optDao.getOrder(dbId);
                if (!OptEnums.OrderStatus.UNKNOWN.equals(ibOrder.getOrderStatus())) {
                    ibOrder.addEvent(OptEnums.OrderStatus.UNKNOWN);
                    optDao.updateOrder(ibOrder);
                    Trade trade = ibOrder.getTrade();
                    trade.addEventByOrderUnknown(ibOrder);
                    optDao.updateTrade(trade);
                }
                optData.getOpenOrderHeartbeatMap().remove(dbId);
            } else {
                optData.getOpenOrderHeartbeatMap().put(dbId, failedHeartbeatsLeft - 1);
                eventBroker.trigger(OptEnums.DataChangeEvent.ORDER);
            }
        }
    }

    @Schedule(dayOfWeek="Mon-Fri", hour = "9", minute = "5", second="1", timezone="US/Eastern", persistent=false)
    private void reloadOptionChains() {
        if (!ibController.isConnected()) {
            return;
        }
        l.info("Periodic reload of option chains");
        optionDataRetriever.reloadOptionChains();
    }

    @Schedule(dayOfWeek="Mon-Fri", hour = "*", minute = "*", second="31", timezone="US/Eastern", persistent=false)
    private void checkActiveContractsSpread() {
        if (!ibController.isConnected()) {
            return;
        }
        l.info("Check active active contracts spread");
        for (String underlying : optData.getUnderlyingDataMap().keySet()) {
            UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
            ContractProperties cp = optData.getContractPropertiesMap().get(underlying);
            ReadinessStatus rs = statusChecker.getReadinessStatus(underlying);
            if (!rs.isReady()) {
                continue;
            }
            if (ud.lockCallContract()) {
                if (optDao.getActiveTrade(underlying, IbApiEnums.OptionType.CALL) == null) {
                    Double basActive = rs.getActiveCallMarketDataSnapshot().getBidAskSpread();
                    boolean isBasActiveValid = (basActive >= 0d && basActive <= cp.getMaxValidSpread());
                    Double basFront = optData.getMarketDataMap().get(ud.getFrontExpiryCallSymbol()).getBidAskSpread();
                    boolean isBasFrontValid = (basFront >= 0d && basFront <= cp.getMaxValidSpread());
                    Double basNext = optData.getMarketDataMap().get(ud.getNextExpiryCallSymbol()).getBidAskSpread();
                    boolean isBasNextValid = (basNext >= 0d && basNext <= cp.getMaxValidSpread());
                    l.info(underlying + ", activeCallSymbol=" + ud.getActiveCallSymbol() + ", bidAskSpread=" + basActive + ", valid=" + isBasActiveValid);
                    l.info(underlying + ", frontExpiryCallSymbol=" + ud.getFrontExpiryCallSymbol() + ", bidAskSpread=" + basFront + ", valid=" + isBasFrontValid);
                    l.info(underlying + ", nextExpiryCallSymbol=" + ud.getNextExpiryCallSymbol() + ", bidAskSpread=" + basNext + ", valid=" + isBasNextValid);
                    if (ud.getActiveCallSymbol().equals(ud.getFrontExpiryCallSymbol())) {
                        if (!isBasActiveValid && isBasNextValid) {
                            l.info(underlying + ", CALL switching to next expiry");
                            ud.setActiveCallSymbol(ud.getNextExpiryCallSymbol());
                        }
                    } else if (ud.getActiveCallSymbol().equals(ud.getNextExpiryCallSymbol())) {
                        if (isBasFrontValid) {
                            l.info(underlying + ", CALL switching to front expiry");
                            ud.setActiveCallSymbol(ud.getFrontExpiryCallSymbol());
                        }
                    }
                }
                ud.releaseCallContract();
            }
            if (ud.lockPutContract()) {
                if (optDao.getActiveTrade(underlying, IbApiEnums.OptionType.PUT) == null) {
                    Double basActive = rs.getActivePutMarketDataSnapshot().getBidAskSpread();
                    boolean isBasActiveValid = (basActive >= 0d && basActive <= cp.getMaxValidSpread());
                    Double basFront = optData.getMarketDataMap().get(ud.getFrontExpiryPutSymbol()).getBidAskSpread();
                    boolean isBasFrontValid = (basFront >= 0d && basFront <= cp.getMaxValidSpread());
                    Double basNext = optData.getMarketDataMap().get(ud.getNextExpiryPutSymbol()).getBidAskSpread();
                    boolean isBasNextValid = (basNext >= 0d && basNext <= cp.getMaxValidSpread());
                    l.info(underlying + ", activePutSymbol=" + ud.getActivePutSymbol() + ", bidAskSpread=" + basActive + ", valid=" + isBasActiveValid);
                    l.info(underlying + ", frontExpiryPutSymbol=" + ud.getFrontExpiryPutSymbol() + ", bidAskSpread=" + basFront + ", valid=" + isBasFrontValid);
                    l.info(underlying + ", nextExpiryPutSymbol=" + ud.getNextExpiryPutSymbol() + ", bidAskSpread=" + basNext + ", valid=" + isBasNextValid);
                    if (ud.getActivePutSymbol().equals(ud.getFrontExpiryPutSymbol())) {
                        if (!isBasActiveValid && isBasNextValid) {
                            l.info(underlying + ", PUT switching to next expiry");
                            ud.setActivePutSymbol(ud.getNextExpiryPutSymbol());
                        }
                    } else if (ud.getActivePutSymbol().equals(ud.getNextExpiryPutSymbol())) {
                        if (isBasFrontValid) {
                            l.info(underlying + ", PUT switching to front expiry");
                            ud.setActivePutSymbol(ud.getFrontExpiryPutSymbol());
                        }
                    }
                }
                ud.releasePutContract();
            }
        }
        eventBroker.trigger(OptEnums.DataChangeEvent.OPTION_CONTRACT);
        eventBroker.trigger(OptEnums.DataChangeEvent.CONTRACT_LOG);
        eventBroker.trigger(OptEnums.DataChangeEvent.MARKET_DATA);
    }

    @Schedule(dayOfWeek="Mon-Fri", hour = "*", minute = "*", second="41", timezone="US/Eastern", persistent=false)
    private void pruneStaleActiveContracts() {
        if (!ibController.isConnected()) {
            return;
        }
        l.info("Prune stale active contracts");
        for (String underlying : optData.getUnderlyingDataMap().keySet()) {
            UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
            Double price = optData.getMarketDataMap().get(underlying).getLast().getValue();
            ReadinessStatus rs = statusChecker.getReadinessStatus(underlying);
            if (!rs.isReady()) {
                continue;
            }
            if (ud.lockCallContract()) {
                boolean prepareCallContracts = false;
                if (optDao.getActiveTrade(underlying, IbApiEnums.OptionType.CALL) == null) {
                    if (!ud.getActiveCallSymbol().equals(ud.getFrontExpiryCallSymbol()) && !ud.getActiveCallSymbol().equals(ud.getNextExpiryCallSymbol())) {
                        prepareCallContracts = true;
                        optionDataRetriever.prepareCallContracts(ud, price);
                        l.info(underlying + " prune CALL contract");
                    }
                }
                if (!prepareCallContracts) {
                    ud.releaseCallContract();
                }
            }
            if (ud.lockPutContract()) {
                boolean preparePutContracts = false;
                if (optDao.getActiveTrade(underlying, IbApiEnums.OptionType.PUT) == null) {
                    if (!ud.getActivePutSymbol().equals(ud.getFrontExpiryPutSymbol()) && !ud.getActivePutSymbol().equals(ud.getNextExpiryPutSymbol())) {
                        preparePutContracts = true;
                        optionDataRetriever.preparePutContracts(ud, price);
                        l.info(underlying + " prune PUT contract");
                    }
                }
                if (!preparePutContracts) {
                    ud.releasePutContract();
                }
            }
        }
        eventBroker.trigger(OptEnums.DataChangeEvent.OPTION_CONTRACT);
        eventBroker.trigger(OptEnums.DataChangeEvent.CONTRACT_LOG);
        eventBroker.trigger(OptEnums.DataChangeEvent.MARKET_DATA);
    }
}
