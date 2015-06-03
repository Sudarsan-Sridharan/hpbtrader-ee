package com.highpowerbear.hpbtrader.options.process;

import com.highpowerbear.hpbtrader.options.common.*;
import com.highpowerbear.hpbtrader.options.entity.ContractLog;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.options.ibclient.IbController;
import com.highpowerbear.hpbtrader.options.model.MarketData;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Named
@Singleton
public class OptionDataRetriever {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject
    OptDao optDao;
    @Inject private IbController ibController;
    @Inject private OptData optData;
    @Inject
    EventBroker eventBroker;
    
    public void reloadOptionChains() {
        optData.getUnderlyingDataMap().keySet().forEach(this::retrieveOptionChains);
    }
    
    private void retrieveOptionChains(String underlying) {
        l.info("START request for loading option chains for underlying=" + underlying);
        UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
        Calendar cal = OptUtil.getNowCalendar();
        String thisMonth = OptUtil.toExpiryStringShort(cal);
        cal.add(Calendar.MONTH, +1);
        String nextMonth = OptUtil.toExpiryStringShort(cal);
        
        // call contracts, this month
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        com.ib.client.Contract ibContract = new com.ib.client.Contract();
        ibContract.m_symbol = underlying;
        ibContract.m_secType = IbApiEnums.SecType.OPT.getName();
        ibContract.m_expiry = thisMonth;
        ibContract.m_right = IbApiEnums.OptionType.CALL.getName();
        ibContract.m_exchange = IbApiEnums.Exchange.SMART.getName();
        ibContract.m_currency = IbApiEnums.Currency.USD.getName();
        ibContract.m_multiplier = IbApiEnums.Multiplier.M_100.getName();
        ibContract.m_includeExpired = false;
        int reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_CALL_CURRENT_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            ibController.requestOptionChain(reqId, ibContract);
        }

        // call contracts, next month
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        ibContract = cloneIbOptionContract(ibContract);
        ibContract.m_expiry = nextMonth;
        reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_CALL_NEXT_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            ibController.requestOptionChain(reqId, ibContract);
        }

        // put contracts, this month
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        ibContract = cloneIbOptionContract(ibContract);
        ibContract.m_expiry = thisMonth;
        ibContract.m_right = IbApiEnums.OptionType.PUT.getName();
        reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_PUT_CURRENT_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            ibController.requestOptionChain(reqId, ibContract);
        }

        // put contracts, next month
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        ibContract = cloneIbOptionContract(ibContract);
        ibContract.m_expiry = nextMonth;
        reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_PUT_NEXT_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            ibController.requestOptionChain(reqId, ibContract);
        }
        l.info("END request for loading option chains for underlying=" + underlying);
    }
    
    public void optionChainRequestCompleted(int reqId) {
        eventBroker.trigger(OptEnums.DataChangeEvent.OPTION_CONTRACT);
        String underlying = optData.getOptionChainRequestMap().get(reqId);
        optData.getOptionChainRequestMap().remove(reqId);
        if (!optData.getOptionChainRequestMap().containsValue(underlying)) {
            optData.getUnderlyingDataMap().get(underlying).markChainsReady();
        }
    }
    
    @Asynchronous
    public synchronized void prepareCallContracts(UnderlyingData ud, Double triggerPrice) {
        Trade activeTradeCall = optDao.getActiveTrade(ud.getUnderlying(), IbApiEnums.OptionType.CALL);
        if (activeTradeCall != null && ud.getActiveCallSymbol() != null && activeTradeCall.getOptionSymbol().equals(ud.getActiveCallSymbol())) {
            ud.releaseCallContract();
            return;
        }
        ud.setCallContractChangeTriggerPrice(triggerPrice);
        String oldActiveCallSymbol = ud.getActiveCallSymbol();
        Double callStrikeDiff = optData.getContractPropertiesMap().get(ud.getUnderlying()).getCallStrikeDiff();
        ud.setFrontExpiryCallSymbol(optDao.getCallSymbol(ud.getUnderlying(), calculateExpirationFriday(OptEnums.ExpiryDistance.FRONT_WEEK), (OptUtil.round5(triggerPrice - callStrikeDiff))));
        ud.setNextExpiryCallSymbol(optDao.getCallSymbol(ud.getUnderlying(), calculateExpirationFriday(OptEnums.ExpiryDistance.NEXT_WEEK), (OptUtil.round5(triggerPrice - callStrikeDiff))));
        ud.setActiveCallSymbol(activeTradeCall != null ? activeTradeCall.getOptionSymbol() : ud.getFrontExpiryCallSymbol());
        if (oldActiveCallSymbol == null || !ud.getActiveCallSymbol().equals(oldActiveCallSymbol)) {
            l.fine("Identified active call option symbol: " + ud.getUnderlying() + " --> " + ud.getActiveCallSymbol());
            optDao.addContractLog(new ContractLog(ud.getUnderlying(), IbApiEnums.OptionType.CALL, ud.getActiveCallSymbol(), triggerPrice));
        }
        if (ud.getActiveCallSymbol().equals(ud.getFrontExpiryCallSymbol()) || ud.getActiveCallSymbol().equals(ud.getNextExpiryCallSymbol())) {
            cancelRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CALL_ACTIVE.getValue()); // if subscribed
        } else {
            requestRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CALL_ACTIVE.getValue(), ud.getUnderlying(), ud.getActiveCallSymbol()); // if not already subscribed
        }
        requestRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CALL_FRONT_WEEK.getValue(), ud.getUnderlying(), ud.getFrontExpiryCallSymbol());
        requestRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CALL_NEXT_WEEK.getValue(), ud.getUnderlying(), ud.getNextExpiryCallSymbol());
        initPurchaseMade(activeTradeCall, ud);
        triggerOptionEvents();
        ud.callContractChanged();
        ud.releaseCallContract();
    }

    @Asynchronous
    public synchronized void preparePutContracts(UnderlyingData ud, Double triggerPrice) {
        Trade activeTradePut = optDao.getActiveTrade(ud.getUnderlying(), IbApiEnums.OptionType.PUT);
        if (activeTradePut != null && ud.getActivePutSymbol() != null && activeTradePut.getOptionSymbol().equals(ud.getActivePutSymbol())) {
            ud.releasePutContract();
            return;
        }
        ud.setPutContractChangeTriggerPrice(triggerPrice);
        String oldActivePutSymbol = ud.getActivePutSymbol();
        Double putStrikeDiff = optData.getContractPropertiesMap().get(ud.getUnderlying()).getPutStrikeDiff();
        ud.setFrontExpiryPutSymbol(optDao.getPutSymbol(ud.getUnderlying(), calculateExpirationFriday(OptEnums.ExpiryDistance.FRONT_WEEK), (OptUtil.round5(triggerPrice + putStrikeDiff))));
        ud.setNextExpiryPutSymbol(optDao.getPutSymbol(ud.getUnderlying(), calculateExpirationFriday(OptEnums.ExpiryDistance.NEXT_WEEK), (OptUtil.round5(triggerPrice + putStrikeDiff))));
        ud.setActivePutSymbol(activeTradePut != null ? activeTradePut.getOptionSymbol() : ud.getFrontExpiryPutSymbol());
        if (oldActivePutSymbol == null || !ud.getActivePutSymbol().equals(oldActivePutSymbol)) {
            l.fine("Identified active put option symbol: " + ud.getUnderlying() + " --> " + ud.getActivePutSymbol());
            optDao.addContractLog(new ContractLog(ud.getUnderlying(), IbApiEnums.OptionType.PUT, ud.getActivePutSymbol(), triggerPrice));
        }
        if (ud.getActivePutSymbol().equals(ud.getFrontExpiryPutSymbol()) || ud.getActivePutSymbol().equals(ud.getNextExpiryPutSymbol())) {
            cancelRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.PUT_ACTIVE.getValue()); // if subscribed
        } else {
            requestRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.PUT_ACTIVE.getValue(), ud.getUnderlying(), ud.getActivePutSymbol()); // if not already subscribed
        }
        requestRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.PUT_FRONT_WEEK.getValue(), ud.getUnderlying(), ud.getFrontExpiryPutSymbol());
        requestRtData(ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.PUT_NEXT_WEEK.getValue(), ud.getUnderlying(), ud.getNextExpiryPutSymbol());
        initPurchaseMade(activeTradePut, ud);
        triggerOptionEvents();
        ud.putContractChanged();
        ud.releasePutContract();
    }
    
    private void cancelRtData(int reqId) {
        String currentOptionSymbol = optData.getMarketDataRequestMap().get(reqId);
        if (currentOptionSymbol != null) {
           ibController.cancelRealtimeData(reqId);  // cancel existing symbol realtime data request
           optData.getMarketDataRequestMap().remove(reqId);
           optData.getMarketDataMap().remove(currentOptionSymbol);
        }
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS / 5);
    }
    
    private void requestRtData(int reqId, String underlying, String optionSymbol) {
        String currentOptionSymbol = optData.getMarketDataRequestMap().get(reqId);
        if (currentOptionSymbol != null) {
            if (optionSymbol.equals(currentOptionSymbol)) {
                return; // already subscribed to market data for the option symbol
            } else {
                ibController.cancelRealtimeData(reqId);  // cancel existing symbol realtime data request
                optData.getMarketDataMap().remove(currentOptionSymbol);
            }
        }
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS / 2);
        optData.getMarketDataMap().put(optionSymbol, new MarketData(underlying, IbApiEnums.SecType.OPT, optionSymbol));
        optData.getMarketDataRequestMap().put(reqId, optionSymbol);
        ibController.requestRealtimeData(reqId, OptUtil.constructIbContract(optionSymbol));
        OptUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS / 5);
    }
    
    private void initPurchaseMade(Trade activeTrade, UnderlyingData ud) {
        if (activeTrade == null || activeTrade.getCurrentPosition() == 0) {
            return;
        }
        if ((ud.getIsActiveCallSymbolPurchased() == null && IbApiEnums.OptionType.CALL.equals(activeTrade.getOptionType())) || (ud.getIsActivePutSymbolPurchased() == null && IbApiEnums.OptionType.PUT.equals(activeTrade.getOptionType()))) {
            ud.purchaseMade(activeTrade);
        }
    }
    
    private com.ib.client.Contract cloneIbOptionContract(com.ib.client.Contract contract) {
        com.ib.client.Contract clonedContract = new com.ib.client.Contract();
        clonedContract.m_symbol = contract.m_symbol;
        clonedContract.m_secType = contract.m_secType;
        clonedContract.m_expiry = contract.m_expiry;
        clonedContract.m_right = contract.m_right;
        clonedContract.m_exchange = contract.m_exchange;
        clonedContract.m_currency = contract.m_currency;
        clonedContract.m_multiplier = contract.m_multiplier;
        clonedContract.m_includeExpired = contract.m_includeExpired;
        return clonedContract;
    }
    
    private Calendar calculateExpirationFriday(OptEnums.ExpiryDistance expiryDistance) { // 0 means front week
        Calendar cal = OptUtil.getNowCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // If today is not Friday, shift to Friday, else shift to the next Friday and then shift numWeeksAhead weeks
        if ((Calendar.FRIDAY - day) > 0) {
            cal.add(Calendar.DAY_OF_WEEK, Calendar.FRIDAY - day);
            cal.add(Calendar.DAY_OF_MONTH, 7 * expiryDistance.getWeek());
        } else {
            cal.add(Calendar.DAY_OF_MONTH, 7 * (expiryDistance.getWeek() + 1));
        }
        return cal;
    }
    
    private void triggerOptionEvents() {
        eventBroker.trigger(OptEnums.DataChangeEvent.OPTION_CONTRACT);
        eventBroker.trigger(OptEnums.DataChangeEvent.CONTRACT_LOG);
        eventBroker.trigger(OptEnums.DataChangeEvent.MARKET_DATA);
    }
}
