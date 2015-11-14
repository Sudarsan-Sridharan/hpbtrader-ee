package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Entity
@Cacheable(false)
@Table(name = "lin_series")
public class Series implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="lin_series", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_series")
    private Integer id;
    @XmlTransient
    @ManyToOne
    private IbAccount ibAccount;
    private String symbol;
    private String underlying;
    @Enumerated(EnumType.STRING)
    @Column(name = "s_interval")
    private LinEnums.Interval interval;
    private Integer displayOrder;
    @Enumerated(EnumType.STRING)
    private LinEnums.SecType secType;
    @Enumerated(EnumType.STRING)
    private LinEnums.Currency currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "s_exchange")
    private LinEnums.Exchange exchange;
    private Boolean isEnabled = Boolean.TRUE;
    @OneToMany(mappedBy = "series", fetch = FetchType.EAGER)
    @OrderBy("strategyType")
    private List<Strategy> strategies = new ArrayList<>();
    
    public com.ib.client.Contract createIbContract() {
        com.ib.client.Contract contract = new com.ib.client.Contract();
        contract.m_symbol = this.underlying;
        contract.m_localSymbol = this.symbol;
        contract.m_secType = LinEnums.SecType.getIbSecType(secType);
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

    public IbAccount getIbAccount() {
        return ibAccount;
    }

    public void setIbAccount(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
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

    public LinEnums.Interval getInterval() {
        return interval;
    }

    public void setInterval(LinEnums.Interval interval) {
        this.interval = interval;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LinEnums.SecType getSecType() {
        return secType;
    }

    public void setSecType(LinEnums.SecType secType) {
        this.secType = secType;
    }

    public LinEnums.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(LinEnums.Currency currency) {
        this.currency = currency;
    }

    public LinEnums.Exchange getExchange() {
        return exchange;
    }

    public void setExchange(LinEnums.Exchange exchange) {
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
