package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 2.10.2015.
 */
@Entity
@Table(name = "opt_combotrade")
public class ComboTrade implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="opt_combotrade")
    @Id
    @GeneratedValue(generator="opt_combotrade")
    private Long id;
    @OneToMany(mappedBy = "comboTrade", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Trade> trades = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private OptEnums.Underlying underlying;
    @ManyToOne
    private Strategy strategy;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateInitOpen;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateClosed;
    private Double realizedPl = 0d;

    public Double getUnrealizedPl() {
        return 0d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComboTrade that = (ComboTrade) o;

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

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public OptEnums.Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(OptEnums.Underlying underlying) {
        this.underlying = underlying;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Calendar getDateInitOpen() {
        return dateInitOpen;
    }

    public void setDateInitOpen(Calendar dateInitOpen) {
        this.dateInitOpen = dateInitOpen;
    }

    public Calendar getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Calendar dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Double getRealizedPl() {
        return realizedPl;
    }

    public void setRealizedPl(Double realizedPl) {
        this.realizedPl = realizedPl;
    }
}
