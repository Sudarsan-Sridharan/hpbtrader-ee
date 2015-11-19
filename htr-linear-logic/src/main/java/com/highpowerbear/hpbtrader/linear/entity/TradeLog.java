package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "lin_tradelog")
public class TradeLog implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="lin_tradelog", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_tradelog")
    private Long id;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar logDate;
    @ManyToOne
    private Trade trade;
    private Integer tradePosition;
    @XmlElement
    private Double stopLoss;
    @XmlElement
    private Double price;
    @XmlElement
    private Double profitTarget;
    private Double unrealizedPl;
    private Double realizedPl;
    @Enumerated(EnumType.STRING)
    private LinEnums.TradeStatus tradeStatus;
    
    @XmlElement
    public long getTimeInMillis() {
        return (logDate.getTimeInMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeLog tradeLog = (TradeLog) o;

        return !(id != null ? !id.equals(tradeLog.id) : tradeLog.id != null);

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

    public Calendar getLogDate() {
        return logDate;
    }

    public void setLogDate(Calendar logDate) {
        this.logDate = logDate;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Integer getTradePosition() {
        return tradePosition;
    }

    public void setTradePosition(Integer tradePosition) {
        this.tradePosition = tradePosition;
    }

    public Double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(Double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getProfitTarget() {
        return profitTarget;
    }

    public void setProfitTarget(Double profitTarget) {
        this.profitTarget = profitTarget;
    }

    public Double getUnrealizedPl() {
        return unrealizedPl;
    }

    public void setUnrealizedPl(Double unrealizedPl) {
        this.unrealizedPl = unrealizedPl;
    }

    public Double getRealizedPl() {
        return realizedPl;
    }

    public void setRealizedPl(Double realizedPl) {
        this.realizedPl = realizedPl;
    }

    public LinEnums.TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(LinEnums.TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }
}
