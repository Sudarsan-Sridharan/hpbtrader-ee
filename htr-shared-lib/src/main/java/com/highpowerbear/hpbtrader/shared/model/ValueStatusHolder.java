package com.highpowerbear.hpbtrader.shared.model;

import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;

/**
 *
 * @author robertk
 */
public class ValueStatusHolder<T> {
    private T value;
    private HtrEnums.ValueStatus valueStatus;

    public ValueStatusHolder(T value, HtrEnums.ValueStatus valueStatus) {
        this.value = value;
        this.valueStatus = valueStatus;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public HtrEnums.ValueStatus getValueStatus() {
        return valueStatus;
    }

    public void setValueStatus(HtrEnums.ValueStatus valueStatus) {
        this.valueStatus = valueStatus;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}