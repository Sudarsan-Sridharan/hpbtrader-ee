package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rkolar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Cacheable(false)
@Table(name = "series")
public class Series implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="series", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="series")
    private Integer id;
    private String symbol;
    private String underlying;
    @Enumerated(EnumType.STRING)
    @Column(name = "s_interval")
    private HtrEnums.Interval interval;
    private Integer displayOrder;
    @Enumerated(EnumType.STRING)
    private HtrEnums.SecType secType;
    @Enumerated(EnumType.STRING)
    private HtrEnums.Currency currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "s_exchange")
    private HtrEnums.Exchange exchange;
    private Boolean isEnabled = Boolean.TRUE;
    @OneToMany(mappedBy = "series", fetch = FetchType.EAGER)
    @OrderBy("strategyType")
    private List<Strategy> strategies = new ArrayList<>();
    
    public com.ib.client.Contract createIbContract() {
        com.ib.client.Contract contract = new com.ib.client.Contract();
        contract.m_symbol = this.underlying;
        contract.m_localSymbol = this.symbol;
        contract.m_secType = HtrEnums.SecType.getIbSecType(secType);
        contract.m_exchange = this.exchange.toString();
        contract.m_currency = this.currency.toString();
        return contract;
    }
    
    public Strategy getActiveStrategy() {
        Strategy activeStrategy = null;
        for (Strategy s : strategies) {
            if (s.getActive()) {
                activeStrategy = s;
                break;
            }
        }
        return activeStrategy;
    }
    
    public Integer getNumStrategies() {
        return strategies.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Series series = (Series) o;

        return !(id != null ? !id.equals(series.id) : series.id != null);

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

    public HtrEnums.Interval getInterval() {
        return interval;
    }

    public void setInterval(HtrEnums.Interval interval) {
        this.interval = interval;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public List<Strategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<Strategy> strategies) {
        this.strategies = strategies;
    }
}
