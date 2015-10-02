package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@Entity
@Table(name = "opt_optioncontract")
public class OptionContract implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="opt_optioncontract")
    @Id
    @GeneratedValue(generator="opt_optioncontract")
    private String optionSymbol;
    @Enumerated(EnumType.STRING)
    private OptEnums.Underlying underlying;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiry;
    @Enumerated(EnumType.STRING)
    private IbApiEnums.OptionType optionType;
    private Double strike;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionContract that = (OptionContract) o;

        return !(optionSymbol != null ? !optionSymbol.equals(that.optionSymbol) : that.optionSymbol != null);

    }

    @Override
    public int hashCode() {
        return optionSymbol != null ? optionSymbol.hashCode() : 0;
    }

    public String getOptionSymbol() {
        return optionSymbol;
    }

    public void setOptionSymbol(String optionSymbol) {
        this.optionSymbol = optionSymbol;
    }

    public OptEnums.Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(OptEnums.Underlying underlying) {
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
}
