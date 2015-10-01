package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertk on 30.9.2015.
 */
@Entity
@Table(name = "opt_strategy")
public class Strategy {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private OptEnums.StrategyType strategyType = OptEnums.StrategyType.values()[0];
    private Boolean isActive = false;
    @Enumerated(EnumType.STRING)
    private OptEnums.Underlying underlying;
    @OneToMany(mappedBy = "strategy", fetch = FetchType.EAGER)
    private List<StrategyParam> params = new ArrayList<>();
    private Integer numAllOrders = 0;
    private Integer numFilledOrders = 0;
    private Integer numPutOrders = 0;
    private Integer numCallOrders = 0;
    private Integer numUndlOrders = 0;
    private Integer numWinners = 0;
    private Integer numLosers = 0;
    private Double cumulativePl = 0d;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<StrategyParam> getParams() {
        return params;
    }

    public void setParams(List<StrategyParam> params) {
        this.params = params;
    }

    public OptEnums.StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(OptEnums.StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public OptEnums.Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(OptEnums.Underlying underlying) {
        this.underlying = underlying;
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

    public Integer getNumPutOrders() {
        return numPutOrders;
    }

    public void setNumPutOrders(Integer numPutOrders) {
        this.numPutOrders = numPutOrders;
    }

    public Integer getNumCallOrders() {
        return numCallOrders;
    }

    public void setNumCallOrders(Integer numCallOrders) {
        this.numCallOrders = numCallOrders;
    }

    public Integer getNumUndlOrders() {
        return numUndlOrders;
    }

    public void setNumUndlOrders(Integer numUndlOrders) {
        this.numUndlOrders = numUndlOrders;
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

    public Double getCumulativePl() {
        return cumulativePl;
    }

    public void setCumulativePl(Double cumulativePl) {
        this.cumulativePl = cumulativePl;
    }
}
