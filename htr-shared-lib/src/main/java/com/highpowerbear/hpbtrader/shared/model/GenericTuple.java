package com.highpowerbear.hpbtrader.shared.model;

/**
 * Created by robertk on 1.6.2016.
 */
public class GenericTuple<T, U> {
    private T first;
    private U second;

    public GenericTuple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}
