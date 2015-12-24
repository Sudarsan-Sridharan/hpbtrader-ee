package com.highpowerbear.hpbtrader.shared.model;

/**
 *
 * @author rkolar
 */
public class Highlighted<T> {
    private T value;
    private String color;

    public Highlighted(T value, String color) {
        this.value = value;
        this.color = color;
    }

    public T getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public Boolean isHighlight() {
        return (color != null);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
