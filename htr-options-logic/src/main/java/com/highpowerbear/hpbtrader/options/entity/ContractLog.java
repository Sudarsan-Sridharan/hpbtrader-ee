package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@Entity
public class ContractLog implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="contractLog")
    @Id
    @GeneratedValue(generator="contractLog")
    private Long id;
    private String underlying;
    @Enumerated(EnumType.STRING)
    private IbApiEnums.OptionType optionType;
    private String optionSymbol;
    private Double triggerPrice;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateActiveFrom;
    
    public ContractLog() {      
    }
    
    public ContractLog(String underlying, IbApiEnums.OptionType optionType, String optionSymbol, Double triggerPrice) {
        this.underlying = underlying;
        this.optionType = optionType;
        this.optionSymbol = optionSymbol;
        this.triggerPrice = triggerPrice;
        this.dateActiveFrom = OptUtil.getNowCalendar();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public IbApiEnums.OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(IbApiEnums.OptionType optionType) {
        this.optionType = optionType;
    }

    public String getOptionSymbol() {
        return optionSymbol;
    }

    public void setOptionSymbol(String optionSymbol) {
        this.optionSymbol = optionSymbol;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public Calendar getDateActiveFrom() {
        return dateActiveFrom;
    }

    public void setDateActiveFrom(Calendar dateActiveFrom) {
        this.dateActiveFrom = dateActiveFrom;
    }
    
    public String print() {
        return underlying + ", " + optionType.getName() + ", " + optionSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractLog)) return false;

        ContractLog that = (ContractLog) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
