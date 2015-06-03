package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author rkolar
 */
@Entity
public class InputSignal implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="signal")
    @Id
    @GeneratedValue(generator="signal")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar signalDate;
    @Enumerated(EnumType.STRING)
    private OptEnums.SignalOrigin origin;
    private String underlying;
    @Enumerated(EnumType.STRING)
    private OptEnums.SignalAction action;
    private String signalName;
    @Enumerated(EnumType.STRING)
    private OptEnums.SignalStatus signalStatus;
    private String statusDescription;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getSignalDate() {
        return signalDate;
    }

    public void setSignalDate(Calendar signalDate) {
        this.signalDate = signalDate;
    }

    public OptEnums.SignalOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(OptEnums.SignalOrigin origin) {
        this.origin = origin;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public OptEnums.SignalAction getAction() {
        return action;
    }

    public void setAction(OptEnums.SignalAction action) {
        this.action = action;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public OptEnums.SignalStatus getSignalStatus() {
        return signalStatus;
    }

    public void setSignalStatus(OptEnums.SignalStatus signalStatus) {
        this.signalStatus = signalStatus;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputSignal)) return false;

        InputSignal inputSignal = (InputSignal) o;

        return !(id != null ? !id.equals(inputSignal.id) : inputSignal.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String print() {
        return underlying + ", " + action + ", " + signalName;
    }
    
}
