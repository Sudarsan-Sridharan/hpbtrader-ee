package com.highpowerbear.hpbtrader.options.execution;

import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.Order;
import com.highpowerbear.hpbtrader.options.entity.InputSentiment;
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
    
    protected Trade createTrade(InputSentiment inputSentiment, Trade activeTrade) {
        ContractProperties cp = optData.getContractPropertiesMap().get(inputSentiment.getUnderlying());
        Trade trade = new Trade();
        trade.setUnderlying(inputSentiment.getUnderlying());
        trade.setOptionType(IbApiEnums.OptionType.CALL);
        //trade.setOptionType(IbApiEnums.OptionType.PUT)
        trade.setOptionSymbol(IbApiEnums.OptionType.CALL.equals(trade.getOptionType()) ? optData.getUnderlyingDataMap().get(inputSentiment.getUnderlying()).getActiveCallSymbol() : optData.getUnderlyingDataMap().get(inputSentiment.getUnderlying()).getActivePutSymbol());
        trade.setTradeQuantity(IbApiEnums.OptionType.CALL.equals(trade.getOptionType())? cp.getTradingQuantCall() : cp.getTradingQuantPut());
        trade.addInitEvent(OptEnums.TradeStatus.INIT_OPEN);
        return trade;
    }
    
    protected Order createOrder(Trade trade, ReadinessStatus rs) {
        MarketData mdSnapshot = (IbApiEnums.OptionType.CALL.equals(trade.getOptionType()) ? rs.getActiveCallMarketDataSnapshot() : rs.getActivePutMarketDataSnapshot());
        ContractProperties cp = optData.getContractPropertiesMap().get(trade.getUnderlying());
        Order order = new Order();
        order.setTrade(trade);
        switch (trade.getTradeStatus()) {
            case INIT_OPEN: order.setAction(IbApiEnums.Action.BUY); break;
            case INIT_CLOSE: order.setAction(IbApiEnums.Action.SELL); break;
        }
        switch (trade.getTradeStatus()) {
            case INIT_OPEN: order.setQuantity(trade.getTradeQuantity()); break;
            case INIT_CLOSE: order.setQuantity(trade.getCurrentPosition()); break;
        }
        order.setOptionSymbol(trade.getOptionSymbol());
        order.setOrderType(IbApiEnums.OrderType.LMT);
        
        Double limitPrice;
        if (cp.getAutoLimit()) {
            limitPrice = (IbApiEnums.Action.BUY.equals(order.getAction()) ? mdSnapshot.getAutoLimitBuy() : mdSnapshot.getAutoLimitSell());
        } else {
            Double bidPrice = mdSnapshot.getBid().getValue();
            limitPrice = (IbApiEnums.Action.BUY.equals(order.getAction()) ? bidPrice + cp.getBidPriceOffsetBuy() : bidPrice + cp.getBidPriceOffsetSell());
        }
        order.setLmtPrice(limitPrice);
        order.addEvent(OptEnums.OrderStatus.NEW);
        return order;
    }

    protected boolean lock(InputSentiment inputSentiment, Trade activeTrade) {
        UnderlyingData ud = optData.getUnderlyingDataMap().get(inputSentiment.getUnderlying());
        ud.lockCallContract();
        ud.lockPutContract();
        return true;
    }
    
    protected void release(InputSentiment inputSentiment, Trade activeTrade) {
        UnderlyingData ud = optData.getUnderlyingDataMap().get(inputSentiment.getUnderlying());
        ud.releaseCallContract();
        ud.releasePutContract();
    }
}
