package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;

import javax.persistence.*;

/**
 * Created by robertk on 30.9.2015.
 */
@Entity
@Table(name = "opt_strategyparam")
public class StrategyParam {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @ManyToOne
    private Strategy strategy;
    @Enumerated(EnumType.STRING)
    private OptEnums.Underlying underlying;
    @Enumerated(EnumType.STRING)
    private OptEnums.ParamType paramType;
    private String paramValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StrategyParam that = (StrategyParam) o;

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

    public OptEnums.Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(OptEnums.Underlying underlying) {
        this.underlying = underlying;
    }

    public OptEnums.ParamType getParamType() {
        return paramType;
    }

    public void setParamType(OptEnums.ParamType param) {
        this.paramType = param;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String settingValue) {
        this.paramValue = settingValue;
    }
}
