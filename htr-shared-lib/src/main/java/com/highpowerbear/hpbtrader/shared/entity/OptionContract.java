package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@Entity
@Table(name = "optioncontract")
public class OptionContract implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="optioncontract")
    @Id
    @GeneratedValue(generator="optioncontract")
    private String optionSymbol;
    private String underlying;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiry;
    @Enumerated(EnumType.STRING)
    private HtrEnums.OptionType optionType;
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

    public HtrEnums.OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(HtrEnums.OptionType optionType) {
        this.optionType = optionType;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }
}
