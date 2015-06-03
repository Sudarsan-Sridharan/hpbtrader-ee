package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@Entity
public class OptionContract implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String optionSymbol;
    private String underlying;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiry;
    @Enumerated(EnumType.STRING)
    private IbApiEnums.OptionType optionType;
    private Double strike;
    
    public String getOptionSymbol() {
        return optionSymbol;
    }

    public void setOptionSymbol(String optionSymbol) {
        this.optionSymbol = optionSymbol;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public Calendar getExpiry() {
        return expiry;
    }

    public void setExpiry(Calendar expiry) {
        this.expiry = expiry;
    }

    public IbApiEnums.OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(IbApiEnums.OptionType optionType) {
        this.optionType = optionType;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionContract)) return false;

        OptionContract that = (OptionContract) o;

        return !(optionSymbol != null ? !optionSymbol.equals(that.optionSymbol) : that.optionSymbol != null);

    }

    @Override
    public int hashCode() {
        return optionSymbol != null ? optionSymbol.hashCode() : 0;
    }
}
