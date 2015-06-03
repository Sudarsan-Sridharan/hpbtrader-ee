package com.highpowerbear.hpbtrader.options.process;

import com.highpowerbear.hpbtrader.options.common.OptData;
import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.IbOrder;
import com.highpowerbear.hpbtrader.options.entity.InputSignal;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.options.ibclient.IbController;
import com.highpowerbear.hpbtrader.options.model.ContractProperties;
import com.highpowerbear.hpbtrader.options.model.MarketData;
import com.highpowerbear.hpbtrader.options.model.ReadinessStatus;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
public class GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject protected OptDao optDao;
    @Inject protected IbController ibController;
    @Inject protected StatusChecker statusChecker;
    @Inject protected OptData optData;
    
    protected final String PROC_NOT_ENABLED = "processing not enabled";
    protected final String CONTR_CHANGE = "contract change in progress";
    protected final String OUT_TIME = "outside configured trading hours";
    protected final String CONVERT_OK = "OK";
    
    public Long process(String underlying, String name) {
        return null;
    }
    
    protected InputSignal createSignal(String underlying, String name, OptEnums.SignalAction action) {
        InputSignal inputSignal = new InputSignal();
        inputSignal.setAction(action);
        inputSignal.setSignalDate(OptUtil.getNowCalendar());
        inputSignal.setOrigin(OptEnums.SignalOrigin.API);
        inputSignal.setUnderlying(underlying);
        inputSignal.setSignalName(name);
        inputSignal.setSignalStatus(OptEnums.SignalStatus.NEW);
        return inputSignal;
    }
    
    protected Trade createTrade(InputSignal inputSignal, Trade activeTrade) {
        ContractProperties cp = optData.getContractPropertiesMap().get(inputSignal.getUnderlying());
        Trade trade = new Trade();
        trade.setUnderlying(inputSignal.getUnderlying());
        switch(inputSignal.getAction()) {
            case OPEN_LONG: trade.setOptionType(IbApiEnums.OptionType.CALL); break;
            case OPEN_SHORT: trade.setOptionType(IbApiEnums.OptionType.PUT); break;
            case REVERSE: trade.setOptionType(IbApiEnums.OptionType.CALL.equals(activeTrade.getOptionType()) ? IbApiEnums.OptionType.PUT : IbApiEnums.OptionType.CALL); break;
        }
        trade.setOptionSymbol(IbApiEnums.OptionType.CALL.equals(trade.getOptionType()) ? optData.getUnderlyingDataMap().get(inputSignal.getUnderlying()).getActiveCallSymbol() : optData.getUnderlyingDataMap().get(inputSignal.getUnderlying()).getActivePutSymbol());
        trade.setTradeQuantity(IbApiEnums.OptionType.CALL.equals(trade.getOptionType())? cp.getTradingQuantCall() : cp.getTradingQuantPut());
        trade.addInitEvent(OptEnums.TradeStatus.INIT_OPEN);
        return trade;
    }
    
    protected IbOrder createOrder(Trade trade, ReadinessStatus rs) {
        MarketData mdSnapshot = (IbApiEnums.OptionType.CALL.equals(trade.getOptionType()) ? rs.getActiveCallMarketDataSnapshot() : rs.getActivePutMarketDataSnapshot());
        ContractProperties cp = optData.getContractPropertiesMap().get(trade.getUnderlying());
        IbOrder ibOrder = new IbOrder();
        ibOrder.setTrade(trade);
        switch (trade.getTradeStatus()) {
            case INIT_OPEN: ibOrder.setAction(IbApiEnums.Action.BUY); break;
            case INIT_FIRST_EXIT: ibOrder.setAction(IbApiEnums.Action.SELL); break;
            case INIT_CLOSE: ibOrder.setAction(IbApiEnums.Action.SELL); break;
        }
        switch (trade.getTradeStatus()) {
            case INIT_OPEN: ibOrder.setQuantity(trade.getTradeQuantity()); break;
            case INIT_FIRST_EXIT:
                ibOrder.setQuantity(trade.getCurrentPosition()/2); break;
            case INIT_CLOSE: ibOrder.setQuantity(trade.getCurrentPosition()); break;
        }
        ibOrder.setOptionSymbol(trade.getOptionSymbol());
        ibOrder.setOrderType(IbApiEnums.OrderType.LMT);
        
        Double limitPrice;
        if (cp.getAutoLimit()) {
            limitPrice = (IbApiEnums.Action.BUY.equals(ibOrder.getAction()) ? mdSnapshot.getAutoLimitBuy() : mdSnapshot.getAutoLimitSell());
        } else {
            Double bidPrice = mdSnapshot.getBid().getValue();
            limitPrice = (IbApiEnums.Action.BUY.equals(ibOrder.getAction()) ? bidPrice + cp.getBidPriceOffsetBuy() : bidPrice + cp.getBidPriceOffsetSell());
        }
        ibOrder.setLmtPrice(limitPrice);
        ibOrder.addEvent(OptEnums.OrderStatus.NEW);
        return ibOrder;
    }
    
    protected void setNotAccepted(InputSignal inputSignal, String desc) {
        inputSignal.setSignalStatus(OptEnums.SignalStatus.NOT_ACCEPTED);
        l.info("Signal not accepted, " + desc);
        inputSignal.setStatusDescription(desc);
        optDao.updateSignal(inputSignal);
    }
    
    protected boolean lock(InputSignal inputSignal, Trade activeTrade) {
        UnderlyingData ud = optData.getUnderlyingDataMap().get(inputSignal.getUnderlying());
        if (OptEnums.SignalAction.OPEN_LONG.equals(inputSignal.getAction()) && activeTrade == null) {
            return ud.lockCallContract();
        } else if (OptEnums.SignalAction.OPEN_SHORT.equals(inputSignal.getAction()) && activeTrade == null) {
            return ud.lockPutContract();
        } else if (OptEnums.SignalAction.CLOSE.equals(inputSignal.getAction()) && IbApiEnums.OptionType.CALL.equals(activeTrade.getOptionType())) {
            return ud.lockCallContract();
        } else if (OptEnums.SignalAction.CLOSE.equals(inputSignal.getAction()) && IbApiEnums.OptionType.PUT.equals(activeTrade.getOptionType())) {
            return ud.lockPutContract();
        } else if (OptEnums.SignalAction.REVERSE.equals(inputSignal.getAction()) && activeTrade != null) {
            boolean callLock = ud.lockCallContract();
            boolean putLock = ud.lockPutContract();
            if (callLock && putLock) {
                return true;
            } else {
                if (callLock) {ud.releaseCallContract();}
                if (putLock) {ud.releasePutContract();}
                return false;
            }
        } else {
            return false;
        }
    }
    
    protected void release(InputSignal inputSignal, Trade activeTrade) {
        UnderlyingData ud = optData.getUnderlyingDataMap().get(inputSignal.getUnderlying());
        if (OptEnums.SignalAction.OPEN_LONG.equals(inputSignal.getAction()) && activeTrade == null) {
            ud.releaseCallContract();
        } else if (OptEnums.SignalAction.OPEN_SHORT.equals(inputSignal.getAction()) && activeTrade == null) {
            ud.releasePutContract();
        } else if (OptEnums.SignalAction.CLOSE.equals(inputSignal.getAction()) && IbApiEnums.OptionType.CALL.equals(activeTrade.getOptionType())) {
            ud.releaseCallContract();
        } else if (OptEnums.SignalAction.CLOSE.equals(inputSignal.getAction()) && IbApiEnums.OptionType.PUT.equals(activeTrade.getOptionType())) {
            ud.releasePutContract();
        } else if (OptEnums.SignalAction.REVERSE.equals(inputSignal.getAction()) && activeTrade != null) {
            ud.releaseCallContract();
            ud.releasePutContract();
        }
    }
}
