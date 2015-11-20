package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "strategylog")
public class StrategyLog  implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="strategylog", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="strategylog")
    private Long id;
    @ManyToOne
    private Strategy strategy;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar logDate;
    
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private HtrEnums.StrategyMode strategyMode;
    private Integer tradingQuantity;
    private String params;
    private Integer numAllOrders;
    private Integer numFilledOrders;
    private Integer currentPosition;
    @XmlElement
    private Double cumulativePl;
    private Integer numShorts;
    private Integer numLongs;
    private Integer numWinners;
    private Integer numLosers;
    
    @XmlElement
    public long getTimeInMillis() {
        return (logDate.getTimeInMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StrategyLog that = (StrategyLog) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public HtrEnums.StrategyMode getStrategyMode() {
        return strategyMode;
    }

    public void setStrategyMode(HtrEnums.StrategyMode strategyMode) {
        this.strategyMode = strategyMode;
    }

    public Calendar getLogDate() {
        return logDate;
    }

    public void setLogDate(Calendar logDate) {
        this.logDate = logDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getTradingQuantity() {
        return tradingQuantity;
    }

    public void setTradingQuantity(Integer tradingQuantity) {
        this.tradingQuantity = tradingQuantity;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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
