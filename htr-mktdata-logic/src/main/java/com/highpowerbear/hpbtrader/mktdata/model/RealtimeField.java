package com.highpowerbear.hpbtrader.mktdata.model;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

/**
 * Created by rkolar on 5/23/14.
 */
class RealtimeField<T> {
    private T value;
    private HtrEnums.RealtimeStatus status;
    private String fieldName;
    private String colorClass;

    RealtimeField(T value, HtrEnums.RealtimeStatus status, String fieldName, String colorClass) {
        this.value = value;
        this.status = status;
        this.fieldName = fieldName;
        this.colorClass = colorClass;
    }

    T getValue() {
        return value;
    }

    void setValue(T raeltimeValue) {
        this.value = raeltimeValue;
    }

    HtrEnums.RealtimeStatus getStatus() {
        return status;
    }

    void setValueStatus(HtrEnums.RealtimeStatus realtimeStatus) {
        this.status = realtimeStatus;
    }

    String getFieldName() {
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