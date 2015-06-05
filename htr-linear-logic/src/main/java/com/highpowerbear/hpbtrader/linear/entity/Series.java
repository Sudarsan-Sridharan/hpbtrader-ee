package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import javax.persistence.*;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getSymbol() {
        return LinUtil.removeSpace(symbol);
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

    public List<Strategy> getStrategies() {
        return strategies;
    }
    
    public Strategy getActiveStrategy() {
        Strategy activeStrategy = null;
        for (Strategy s : strategies) {
            if (s.getIsActive()) {
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Series)) {
            return false;
        }
        Series other = (Series) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
