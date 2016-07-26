package com.highpowerbear.hpbtrader.shared.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "strategyperformance", schema = "hpbtrader", catalog = "hpbtrader")
public class StrategyPerformance implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="strategyperformance", table="sequence", schema = "hpbtrader", catalog = "hpbtrader", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="strategyperformance")
    private Long id;
    @ManyToOne
    @XmlTransient
    private Strategy strategy;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar performanceDate;

    private Long numAllOrders;
    private Long numFilledOrders;
    private Integer currentPosition;
    private Double cumulativePl;
    private Long numShorts;
    private Long numLongs;
    private Long numWinners;
    private Long numLosers;
    
    @XmlElement
    public Integer getStrategyId() {
        return strategy.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StrategyPerformance that = (StrategyPerformance) o;

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

    public Calendar getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(Calendar logDate) {
        this.performanceDate = logDate;
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
