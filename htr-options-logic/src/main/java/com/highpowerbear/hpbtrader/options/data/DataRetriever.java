package com.highpowerbear.hpbtrader.options.data;

import com.highpowerbear.hpbtrader.options.common.*;
import com.highpowerbear.hpbtrader.options.model.MarketData;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;
import com.ib.client.TickType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class DataRetriever {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject private OptData optData;
    @Inject private ChainsRetriever chainsRetriever;
    @Inject private EventBroker eventBroker;

    public void start() throws Exception {
        int i = 1;
        for (String underlying : optData.getUnderlyingDataMap().keySet()) {
            optData.getUnderlyingDataMap().get(underlying).setIbRequestIdBase(OptDefinitions.REQUEST_ID_MULTIPLIER * i++);
        }
        chainsRetriever.reloadOptionChains();
        HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS * 4);
        requestRtDataForUnderlyings();
    }

    public void stop() {
        for (Integer reqId : optData.getMarketDataRequestMap().keySet()) {
            //ibController.cancelRealtimeData(reqId);
            // TODO
            HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS / 2);
        }
    }
    
    private void requestRtDataForUnderlyings() {
        for (String underlying : optData.getUnderlyingDataMap().keySet()) {
            HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
            optData.getMarketDataMap().put(underlying, new MarketData(underlying, HtrEnums.SecType.STK, underlying));
            com.ib.client.Contract ibContract = OptUtil.constructIbContract(underlying);
            int reqId = optData.getUnderlyingDataMap().get(underlying).getIbRequestIdBase() + OptEnums.RequestIdOffset.MKTDATA_UNDERLYING.getValue();
            optData.getMarketDataRequestMap().put(reqId, underlying);
            //ibController.requestRealtimeData(reqId, ibContract);
            // TODO
        }
    }
    
    public void updateRealtimeData(int reqId, int field, double price) {
        String symbol = optData.getMarketDataRequestMap().get(reqId);
        if (symbol == null) {
            return;
        }
        MarketData marketData = optData.getMarketDataMap().get(symbol);
        if (marketData == null) {
            return;
        }
        marketData.setField(field, price);
        eventBroker.trigger(OptEnums.DataChangeEvent.MKT_DATA);
        if (TickType.LAST != field || OptUtil.isOptionSymbol(symbol)) {
            return;
        }
        UnderlyingData ud = optData.getUnderlyingDataMap().get(symbol);
        if (!ud.isChainsReady()) {
            return;
        }
        if (ud.isCallContractChangeTimoutElapsed() && triggerContractChange(price, ud.getCallContractChangeTriggerPrice())) {
            if (ud.lockCallContract()) {
                //chainsRetriever.prepareCallContracts(ud, price);
            }
        }
        if (ud.isPutContractChangeTimoutElapsed() && triggerContractChange(price, ud.getPutContractChangeTriggerPrice())) {
            if (ud.lockPutContract()) {
                //chainsRetriever.preparePutContracts(ud, price);
            }
        }
    }
    
    public void updateRealtimeData(int reqId, int field, int size) {
        String symbol = optData.getMarketDataRequestMap().get(reqId);
        if (symbol == null) {
            return;
        }
        MarketData marketData = optData.getMarketDataMap().get(symbol);
        if (marketData == null) {
            return;
        }
        marketData.setField(field, size);
        eventBroker.trigger(OptEnums.DataChangeEvent.MKT_DATA);
    }
    
    private boolean triggerContractChange(Double currentPrice, Double lastContractChangeTriggerPrice) {
        if (OptDefinitions.INVALID_PRICE.equals(lastContractChangeTriggerPrice)) {
            return true;
        } else if (!OptUtil.roundDownToHalf(currentPrice).equals(OptUtil.roundDownToHalf(lastContractChangeTriggerPrice))) {
            return true;
        }
        return false;
    }
}
