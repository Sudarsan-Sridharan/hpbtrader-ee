package com.highpowerbear.hpbtrader.linear.persistence.model;

import com.highpowerbear.hpbtrader.linear.entity.Series;
import java.io.Serializable;

/**
 * Created by robertk on 4/28/15.
 */
public class SeriesRecord implements Serializable {
    private Integer id;
    private Series series;

    private Integer displayOrder;
    private String underlying;
    private String symbol;
    private String currency;
    private String interval;
    private String secType;
    private String exchange;
    private Boolean enabled;
    private Boolean realtimeDataEnabled;
    private Long numBars;
    private Integer numStrategies;
    private Double lastQuote;
    private String activeStrategy;
    private String strategyMode;
    private String strategyModeClass;
    private Integer currentPosition;
    private Long numTrades;
    private Integer numAllOrders;
    private Integer numFilledOrders;
    private Double cumulativePl;
    private String cumulativePlClass;
    private String tradeType;
    private String tradeTypeClass;
    private Double unrealizedPl;
    private String unrealizedPlClass;
    private String tradeStatus;
    private String tradeStatusClass;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getRealtimeDataEnabled() {
        return realtimeDataEnabled;
    }

    public void setRealtimeDataEnabled(Boolean realtimeDataEnabled) {
        this.realtimeDataEnabled = realtimeDataEnabled;
    }

    public Long getNumBars() {
        return numBars;
    }

    public void setNumBars(Long numBars) {
        this.numBars = numBars;
    }

    public Integer getNumStrategies() {
        return numStrategies;
    }

    public void setNumStrategies(Integer numStrategies) {
        this.numStrategies = numStrategies;
    }

    public Double getLastQuote() {
        return lastQuote;
    }

    public void setLastQuote(Double lastQuote) {
        this.lastQuote = lastQuote;
    }

    public String getActiveStrategy() {
        return activeStrategy;
    }

    public void setActiveStrategy(String activeStrategy) {
        this.activeStrategy = activeStrategy;
    }

    public String getStrategyMode() {
        return strategyMode;
    }

    public void setStrategyMode(String strategyMode) {
        this.strategyMode = strategyMode;
    }

    public String getStrategyModeClass() {
        return strategyModeClass;
    }

    public void setStrategyModeClass(String strategyModeClass) {
        this.strategyModeClass = strategyModeClass;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Long getNumTrades() {
        return numTrades;
    }

    public void setNumTrades(Long numTrades) {
        this.numTrades = numTrades;
    }

    public Integer getNumAllOrders() {
        return numAllOrders;
    }

    public void setNumAllOrders(Integer numAllOrders) {
        this.numAllOrders = numAllOrders;
    }

    public Integer getNumFilledOrders() {
        return numFilledOrders;
    }

    public void setNumFilledOrders(Integer numFilledOrders) {
        this.numFilledOrders = numFilledOrders;
    }

    public Double getCumulativePl() {
        return cumulativePl;
    }

    public void setCumulativePl(Double cumulativePl) {
        this.cumulativePl = cumulativePl;
    }

    public String getCumulativePlClass() {
        return cumulativePlClass;
    }

    public void setCumulativePlClass(String cumulativePlClass) {
        this.cumulativePlClass = cumulativePlClass;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeTypeClass() {
        return tradeTypeClass;
    }

    public void setTradeTypeClass(String tradeTypeClass) {
        this.tradeTypeClass = tradeTypeClass;
    }

    public Double getUnrealizedPl() {
        return unrealizedPl;
    }

    public void setUnrealizedPl(Double unrealizedPl) {
        this.unrealizedPl = unrealizedPl;
    }

    public String getUnrealizedPlClass() {
        return unrealizedPlClass;
    }

    public void setUnrealizedPlClass(String unrealizedPlClass) {
        this.unrealizedPlClass = unrealizedPlClass;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeStatusClass() {
        return tradeStatusClass;
    }

    public void setTradeStatusClass(String tradeStatusClass) {
        this.tradeStatusClass = tradeStatusClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeriesRecord)) return false;

        SeriesRecord that = (SeriesRecord) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
