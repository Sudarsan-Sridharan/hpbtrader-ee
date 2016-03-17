package com.highpowerbear.hpbtrader.strategy.options.model;

import com.highpowerbear.hpbtrader.strategy.common.StrategyDefinitions;
import com.highpowerbear.hpbtrader.strategy.common.StrategyUtil;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.model.ValueStatusHolder;
import com.ib.client.TickType;

/**
 *
 * @author rkolar
 */
public class MarketData implements Comparable<MarketData> {
    private String underlying;
    private HtrEnums.SecType secType;
    private String symbol;
    
    // price fields
    private ValueStatusHolder<Double> bid = new ValueStatusHolder<>(StrategyDefinitions.INVALID_PRICE, HtrEnums.ValueStatus.UNCHANGED);
    private ValueStatusHolder<Double> ask = new ValueStatusHolder<>(StrategyDefinitions.INVALID_PRICE, HtrEnums.ValueStatus.UNCHANGED);
    private ValueStatusHolder<Double> last = new ValueStatusHolder<>(StrategyDefinitions.INVALID_PRICE, HtrEnums.ValueStatus.UNCHANGED);
    
    // size fields
    private ValueStatusHolder<Integer> bidSize = new ValueStatusHolder<>(StrategyDefinitions.INVALID_SIZE, HtrEnums.ValueStatus.UNCHANGED);
    private ValueStatusHolder<Integer> askSize = new ValueStatusHolder<>(StrategyDefinitions.INVALID_SIZE, HtrEnums.ValueStatus.UNCHANGED);
    private ValueStatusHolder<Integer> lastSize = new ValueStatusHolder<>(StrategyDefinitions.INVALID_SIZE, HtrEnums.ValueStatus.UNCHANGED);
    
    private ValueStatusHolder<Integer> volume = new ValueStatusHolder<>(StrategyDefinitions.INVALID_SIZE, HtrEnums.ValueStatus.UNCHANGED);
    private ValueStatusHolder<Integer> callOpenInterest = new ValueStatusHolder<>(StrategyDefinitions.INVALID_SIZE, HtrEnums.ValueStatus.UNCHANGED);
    private ValueStatusHolder<Integer> putOpenInterest = new ValueStatusHolder<>(StrategyDefinitions.INVALID_SIZE, HtrEnums.ValueStatus.UNCHANGED);

    public MarketData(String underlying, HtrEnums.SecType secType, String symbol) {
        this.underlying = underlying;
        this.secType = secType;
        this.symbol = symbol;
    }

    @Override
    public int compareTo(MarketData other) {
        return (this.symbol.compareTo(other.symbol));
    }
    
    public void invalidatePrices() {
        bid.setValue(StrategyDefinitions.INVALID_PRICE);
        ask.setValue(StrategyDefinitions.INVALID_PRICE);
        last.setValue(StrategyDefinitions.INVALID_PRICE);
    }
    
    public void invalidateSizes() {
        bidSize.setValue(StrategyDefinitions.INVALID_SIZE);
        askSize.setValue(StrategyDefinitions.INVALID_SIZE);
        lastSize.setValue(StrategyDefinitions.INVALID_SIZE);
        volume.setValue(StrategyDefinitions.INVALID_SIZE);
        callOpenInterest.setValue(StrategyDefinitions.INVALID_SIZE);
        putOpenInterest.setValue(StrategyDefinitions.INVALID_SIZE);
    }
    
    public void setField(int field, double price) {
        switch(field) {
            case TickType.BID: setValueStatus(bid, price); break;
            case TickType.ASK: setValueStatus(ask, price); break;
            case TickType.LAST: setValueStatus(last, price); break;
        }
    }
    
    public void setField(int field, int size) {
        switch(field) {
            case TickType.BID_SIZE: setValueStatus(bidSize, size); break;
            case TickType.ASK_SIZE: setValueStatus(askSize, size); break;
            case TickType.LAST_SIZE: setValueStatus(lastSize, size); break;
            case TickType.VOLUME: setValueStatus(volume, size); break;
            case TickType.OPTION_CALL_OPEN_INTEREST: setValueStatus(callOpenInterest, size); break;
            case TickType.OPTION_PUT_OPEN_INTEREST: setValueStatus(putOpenInterest, size); break;
        }
    }
    
    private void setValueStatus(ValueStatusHolder<Double> v, Double price) {
        v.setValueStatus(price > v.getValue() ? HtrEnums.ValueStatus.UPTICK : (price < v.getValue() ? HtrEnums.ValueStatus.DOWNTICK : HtrEnums.ValueStatus.UNCHANGED));
        v.setValue(price);
    }
    
    private void setValueStatus(ValueStatusHolder<Integer> v, Integer size) {
        v.setValueStatus(size > v.getValue() ? HtrEnums.ValueStatus.UPTICK : (size < v.getValue() ? HtrEnums.ValueStatus.DOWNTICK : HtrEnums.ValueStatus.UNCHANGED));
        v.setValue(size);
    }
    
    public String printData() {
        return (bidSize + " " + bid + " " + ask + " " + askSize + " " + last + " " + lastSize + " " + volume + " " + callOpenInterest + " " + putOpenInterest);
    }
    
    public Double getBidAskSpread() {
        return StrategyUtil.round5(ask.getValue() - bid.getValue());
    }
    
    public Double getAutoLimitBuy() {
        Double bas = getBidAskSpread();
        if (bas == 0d) {
            return getBid().getValue();
        } else {
            return getBid().getValue() + ((Math.ceil((bas * 100d) / 2d)) / 100d);
        }
    }
    
    public Double getAutoLimitSell() {
        Double bas = getBidAskSpread();
        if (bas == 0d) {
            return getBid().getValue();
        } else {
            return getBid().getValue() + ((Math.floor((bas * 100d) / 2d)) / 100d);
        }
    }
    
    public String getUnderlying() {
        return underlying;
    }

    public HtrEnums.SecType getSecType() {
        return secType;
    }

    public String getSymbol() {
        return symbol;
    }

    public ValueStatusHolder<Double> getBid() {
        return bid;
    }

    public ValueStatusHolder<Double> getAsk() {
        return ask;
    }

    public ValueStatusHolder<Double> getLast() {
        return last;
    }

    public ValueStatusHolder<Integer> getBidSize() {
        return bidSize;
    }

    public ValueStatusHolder<Integer> getAskSize() {
        return askSize;
    }

    public ValueStatusHolder<Integer> getLastSize() {
        return lastSize;
    }

    public ValueStatusHolder<Integer> getVolume() {
        return volume;
    }

    public ValueStatusHolder<Integer> getCallOpenInterest() {
        return callOpenInterest;
    }

    public ValueStatusHolder<Integer> getPutOpenInterest() {
        return putOpenInterest;
    }
    
    public MarketData createSnapshot() {
        MarketData snapshot = new MarketData(underlying, secType, symbol);
        snapshot.setField(TickType.BID, bid.getValue());
        snapshot.setField(TickType.ASK, ask.getValue());
        snapshot.setField(TickType.LAST, last.getValue());
        
        snapshot.setField(TickType.BID_SIZE, bidSize.getValue());
        snapshot.setField(TickType.ASK_SIZE, askSize.getValue());
        snapshot.setField(TickType.LAST_SIZE, lastSize.getValue());
        snapshot.setField(TickType.VOLUME, volume.getValue());
        snapshot.setField(TickType.OPTION_CALL_OPEN_INTEREST, callOpenInterest.getValue());
        snapshot.setField(TickType.OPTION_PUT_OPEN_INTEREST, putOpenInterest.getValue());
        
        return snapshot;
    }
}
