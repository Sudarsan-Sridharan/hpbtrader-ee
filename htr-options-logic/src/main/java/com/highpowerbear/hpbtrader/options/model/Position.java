package com.highpowerbear.hpbtrader.options.model;

/**
 *
 * @author robertk
 */
public class Position {
    private String symbol;
    private Integer position;

    public Position(String symbol, Integer position) {
        this.symbol = symbol;
        this.position = position;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getPosition() {
        return position;
    }
}
