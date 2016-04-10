package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "strategy", schema = "hpbtrader", catalog = "hpbtrader")
public class Strategy implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    // cannot be changed
    private Integer id;
    @ManyToOne
    private Instrument tradeInstrument;
    @ManyToOne
    private IbAccount ibAccount;
    private String inputSeriesAliases; // csv
    @Enumerated(EnumType.STRING)
    private HtrEnums.StrategyType strategyType;
    
    // can be changed
    private Boolean active;
    private Integer displayOrder;
    @Enumerated(EnumType.STRING)
    private HtrEnums.StrategyMode strategyMode = HtrEnums.StrategyMode.SIM;
    private String params = HtrEnums.StrategyType.values()[0].getDefaultParams();
    private Integer tradingQuantity = 100;
    private Long numAllOrders = 0L;
    private Long numFilledOrders = 0L;
    private Integer currentPosition = 0;
    private Double cumulativePl = 0d;
    private Long numShorts = 0L;
    private Long numLongs = 0L;
    private Long numWinners = 0L;
    private Long numLosers = 0L;

    public String getDefaultInputSeriesAlias() {
        return this.inputSeriesAliases.split(",")[0];
    }

    public void recalculateStats(Trade closedTrade) {
        if (closedTrade.isLong()) {
            numLongs++;
        } else {
            numShorts++;
        }
        if (closedTrade.getRealizedPl() >= 0d) {
            numWinners++;
        } else {
            numLosers++;
        }
        cumulativePl += closedTrade.getRealizedPl();
    }
    
    public Long getNumClosedTrades() {
        return numShorts + numLongs;
    }
    
    public StrategyLog copyValues(StrategyLog sl) {
        if (sl == null) {
            return null;
        }
        sl.setActive(active);
        sl.setStrategyMode(strategyMode);
        sl.setTradingQuantity(tradingQuantity);
        sl.setParams(getParams());
        sl.setNumAllOrders(numAllOrders);
        sl.setNumFilledOrders(numFilledOrders);
        sl.setCurrentPosition(currentPosition);
        sl.setCumulativePl(cumulativePl);
        sl.setNumShorts(numShorts);
        sl.setNumLongs(numLongs);
        sl.setNumWinners(numWinners);
        sl.setNumLosers(numLosers);
        return sl;
    }
    
    public Strategy deepCopy(Strategy otherStrategy) {
        if (otherStrategy == null) {
            return null;
        }
        otherStrategy.setId(id);
        otherStrategy.setTradeInstrument(tradeInstrument);
        otherStrategy.setStrategyType(strategyType);
        otherStrategy.setActive(active);
        otherStrategy.setStrategyMode(strategyMode);
        otherStrategy.setTradingQuantity(tradingQuantity);
        otherStrategy.setParams(getParams());
        otherStrategy.setNumAllOrders(numAllOrders);
        otherStrategy.setNumFilledOrders(numFilledOrders);
        otherStrategy.setCurrentPosition(currentPosition);
        otherStrategy.setCumulativePl(cumulativePl);
        otherStrategy.setNumShorts(numShorts);
        otherStrategy.setNumLongs(numLongs);
        otherStrategy.setNumWinners(numWinners);
        otherStrategy.setNumLosers(numLosers);
        return otherStrategy;
    }
    
    public void resetStatistics() {
        setNumAllOrders(0L);
        setNumFilledOrders(0L);
        setCurrentPosition(0);
        setCumulativePl(0d);
        setNumShorts(0L);
        setNumLongs(0L);
        setNumWinners(0L);
        setNumLosers(0L);
    }
    
    public boolean valuesEqual(Strategy otherStrategy) {
        if (otherStrategy == null) {
            return false;
        }
        if (    active.equals(otherStrategy.getActive()) &&
                strategyMode.equals(otherStrategy.getStrategyMode()) &&
                tradingQuantity.equals(otherStrategy.getTradingQuantity()) &&
                params.equals(otherStrategy.getParams()) &&
                numAllOrders.equals(otherStrategy.getNumAllOrders()) &&
                numFilledOrders.equals(otherStrategy.getNumFilledOrders()) &&
                currentPosition.equals(otherStrategy.getCurrentPosition()) &&
                cumulativePl.equals(otherStrategy.getCumulativePl()) &&
                numShorts.equals(otherStrategy.getNumShorts()) &&
                numLongs.equals(otherStrategy.getNumLongs()) &&
                numWinners.equals(otherStrategy.getNumWinners()) &&
                numLosers.equals(otherStrategy.getNumLosers())
           )
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Strategy strategy = (Strategy) o;

        return !(id != null ? !id.equals(strategy.id) : strategy.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instrument getTradeInstrument() {
        return tradeInstrument;
    }

    public void setTradeInstrument(Instrument tradeInstrument) {
        this.tradeInstrument = tradeInstrument;
    }

    public IbAccount getIbAccount() {
        return ibAccount;
    }

    public String getInputSeriesAliases() {
        return inputSeriesAliases;
    }

    public void setInputSeriesAliases(String inputSeriesAliases) {
        this.inputSeriesAliases = inputSeriesAliases;
    }

    public void setIbAccount(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
    }

    public HtrEnums.StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(HtrEnums.StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public HtrEnums.StrategyMode getStrategyMode() {
        return strategyMode;
    }

    public void setStrategyMode(HtrEnums.StrategyMode strategyMode) {
        this.strategyMode = strategyMode;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getTradingQuantity() {
        return tradingQuantity;
    }

    public void setTradingQuantity(Integer tradingQuantity) {
        this.tradingQuantity = tradingQuantity;
    }

    public Long getNumAllOrders() {
        return numAllOrders;
    }

    public void setNumAllOrders(Long numAllOrders) {
        this.numAllOrders = numAllOrders;
    }

    public Long getNumFilledOrders() {
        return numFilledOrders;
    }

    public void setNumFilledOrders(Long numFilledOrders) {
        this.numFilledOrders = numFilledOrders;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Double getCumulativePl() {
        return cumulativePl;
    }

    public void setCumulativePl(Double cumulativePl) {
        this.cumulativePl = cumulativePl;
    }

    public Long getNumShorts() {
        return numShorts;
    }

    public void setNumShorts(Long numShorts) {
        this.numShorts = numShorts;
    }

    public Long getNumLongs() {
        return numLongs;
    }

    public void setNumLongs(Long numLongs) {
        this.numLongs = numLongs;
    }

    public Long getNumWinners() {
        return numWinners;
    }

    public void setNumWinners(Long numWinners) {
        this.numWinners = numWinners;
    }

    public Long getNumLosers() {
        return numLosers;
    }

    public void setNumLosers(Long numLosers) {
        this.numLosers = numLosers;
    }
}
