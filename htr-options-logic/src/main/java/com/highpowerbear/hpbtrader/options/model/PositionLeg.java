package com.highpowerbear.hpbtrader.options.model;

import com.highpowerbear.hpbtrader.options.entity.OptionContract;

/**
 * Created by robertk on 29.9.2015.
 */
public class PositionLeg {
    private Integer id;
    private Integer quantity;
    private OptionContract optionContract;

    public PositionLeg(Integer id, Integer quantity, OptionContract optionContract) {
        this.id = id;
        this.quantity = quantity;
        this.optionContract = optionContract;
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OptionContract getOptionContract() {
        return optionContract;
    }
}
