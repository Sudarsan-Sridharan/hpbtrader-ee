package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;

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
@Table(name = "lin_strategy")
public class Strategy implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="lin_strategy", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_strategy")
    
    // cannot be changed
    private Integer id;
    @ManyToOne
    private Series series;
    @Enumerated(EnumType.STRING)
    private LinEnums.StrategyType strategyType = LinEnums.StrategyType.values()[0];
    
    // can be changed
    private Boolean isActive = false;
    @Enumerated(EnumType.STRING)
    private LinEnums.StrategyMode strategyMode = LinEnums.StrategyMode.SIM;
    private String params = LinEnums.StrategyType.values()[0].getDefaultParams();
    private Integer tradingQuantity = 100;
    private Integer numAllOrders = 0;
    private Integer numFilledOrders = 0;
    private Integer currentPosition = 0;
    private Double cumulativePl = 0d;
    private Integer numShorts = 0;
    private Integer numLongs = 0;
    private Integer numWinners = 0;
    private Integer numLosers = 0;
    
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
    
    public Integer getNumClosedTrades() {
        return numShorts + numLongs;
    }
    
    public StrategyLog copyValues(StrategyLog sl) {
        if (sl == null) {
            return null;
        }
        sl.setIsActive(isActive);
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
        otherStrategy.setSeries(series);
        otherStrategy.setStrategyType(strategyType);
        otherStrategy.setActive(isActive);
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
        setNumAllOrders(0);
        setNumFilledOrders(0);
        setCurrentPosition(0);
        setCumulativePl(0d);
        setNumShorts(0);
        setNumLongs(0);
        setNumWinners(0);
        setNumLosers(0);
    }
    
    public boolean valuesEqual(Strategy otherStrategy) {
        if (otherStrategy == null) {
            return false;
        }
        if (    isActive.equals(otherStrategy.getActive()) &&
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

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public LinEnums.StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(LinEnums.StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LinEnums.StrategyMode getStrategyMode() {
        return strategyMode;
    }

    public void setStrategyMode(LinEnums.StrategyMode strategyMode) {
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

    public Integer getNumShorts() {
        return numShorts;
    }

    public void setNumShorts(Integer numShorts) {
        this.numShorts = numShorts;
    }

    public Integer getNumLongs() {
        return numLongs;
    }

    public void setNumLongs(Integer numLongs) {
        this.numLongs = numLongs;
    }

    public Integer getNumWinners() {
        return numWinners;
    }

    public void setNumWinners(Integer numWinners) {
        this.numWinners = numWinners;
    }

    public Integer getNumLosers() {
        return numLosers;
    }

    public void setNumLosers(Integer numLosers) {
        this.numLosers = numLosers;
    }
}
