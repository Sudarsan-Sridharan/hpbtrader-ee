package com.highpowerbear.hpbtrader.linear.model;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

/**
 * Created by rkolar on 5/23/14.
 */
public class RealtimeField<T> {
    private T value;
    private HtrEnums.RealtimeStatus status;
    private String fieldName;
    private String colorClass;

    public RealtimeField(T value, HtrEnums.RealtimeStatus status, String fieldName, String colorClass) {
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

    public HtrEnums.RealtimeStatus getStatus() {
        return status;
    }

    public void setValueStatus(HtrEnums.RealtimeStatus realtimeStatus) {
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