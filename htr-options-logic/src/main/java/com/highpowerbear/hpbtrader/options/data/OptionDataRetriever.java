package com.highpowerbear.hpbtrader.options.data;

import com.highpowerbear.hpbtrader.options.common.*;
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
    @Inject EventBroker eventBroker;
    
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
}
