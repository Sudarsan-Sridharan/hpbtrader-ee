package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * Created by robertk on 16.3.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "instrument", schema = "hpbtrader", catalog = "hpbtrader")
public class Instrument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;
    private String symbol;
    private String underlying;
    @Enumerated(EnumType.STRING)
    private HtrEnums.SecType secType;
    @Enumerated(EnumType.STRING)
    private HtrEnums.Currency currency;
    @Enumerated(EnumType.STRING)
    private HtrEnums.Exchange exchange;

    public com.ib.client.Contract createContract() {
        com.ib.client.Contract ctr = new com.ib.client.Contract();
        ctr.m_symbol = this.underlying;
        ctr.m_localSymbol = this.symbol;
        ctr.m_secType = secType.name();
        ctr.m_exchange = this.exchange.toString();
        ctr.m_currency = this.currency.toString();
        return ctr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instrument that = (Instrument) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public HtrEnums.SecType getSecType() {
        return secType;
    }

    public void setSecType(HtrEnums.SecType secType) {
        this.secType = secType;
    }

    public HtrEnums.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(HtrEnums.Currency currency) {
        this.currency = currency;
    }

    public HtrEnums.Exchange getExchange() {
        return exchange;
    }

    public void setExchange(HtrEnums.Exchange exchange) {
        this.exchange = exchange;
    }
}
