package com.highpowerbear.hpbtrader.options.model;

import com.highpowerbear.hpbtrader.options.common.OptEnums;

/**
 *
 * @author robertk
 */
public class ValueStatusHolder<T> {
    private T value;
    private OptEnums.ValueStatus valueStatus;

    public ValueStatusHolder(T value, OptEnums.ValueStatus valueStatus) {
        this.value = value;
        this.valueStatus = valueStatus;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public OptEnums.ValueStatus getValueStatus() {
        return valueStatus;
    }

    public void setValueStatus(OptEnums.ValueStatus valueStatus) {
        this.valueStatus = valueStatus;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}