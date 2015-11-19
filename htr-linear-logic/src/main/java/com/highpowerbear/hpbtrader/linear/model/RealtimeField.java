package com.highpowerbear.hpbtrader.linear.model;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;

/**
 * Created by rkolar on 5/23/14.
 */
public class RealtimeField<T> {
    private T value;
    private LinEnums.RealtimeStatus status;
    private String fieldName;
    private String colorClass;

    public RealtimeField(T value, LinEnums.RealtimeStatus status, String fieldName, String colorClass) {
        this.value = value;
        this.status = status;
        this.fieldName = fieldName;
        this.colorClass = colorClass;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T raeltimeValue) {
        this.value = raeltimeValue;
    }

    public LinEnums.RealtimeStatus getStatus() {
        return status;
    }

    public void setValueStatus(LinEnums.RealtimeStatus realtimeStatus) {
        this.status = realtimeStatus;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColorClass() {
        return colorClass;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}