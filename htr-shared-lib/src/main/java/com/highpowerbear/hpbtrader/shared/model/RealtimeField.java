package com.highpowerbear.hpbtrader.shared.model;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by rkolar on 5/23/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
class RealtimeField<T> {
    private T value;
    private HtrEnums.RealtimeStatus status;
    private HtrEnums.RealtimeFieldName fieldName;

    RealtimeField(T value, HtrEnums.RealtimeStatus status, HtrEnums.RealtimeFieldName fieldName) {
        this.value = value;
        this.status = status;
        this.fieldName = fieldName;
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

    public HtrEnums.RealtimeFieldName getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}