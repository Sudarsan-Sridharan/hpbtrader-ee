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
@Entity
@Table(name = "lin_tradelog")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"timeInMillis", "stopLoss", "price", "profitTarget"})
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TradeLog)) {
            return false;
        }
        TradeLog other = (TradeLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
