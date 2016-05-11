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
@Table(name = "strategylog", schema = "hpbtrader", catalog = "hpbtrader")
public class StrategyLog  implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="strategylog", table="sequence", schema = "hpbtrader", catalog = "hpbtrader", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="strategylog")
    private Long id;
    @ManyToOne
    private Strategy strategy;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar logDate;
    
    private boolean active;
    @Enumerated(EnumType.STRING)
    private HtrEnums.StrategyMode strategyMode;
    private Integer tradingQuantity;
    private String params;
    private Long numAllOrders;
    private Long numFilledOrders;
    private Integer currentPosition;
    @XmlElement
    private Double cumulativePl;
    private Long numShorts;
    private Long numLongs;
    private Long numWinners;
    private Long numLosers;
    
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
