package com.highpowerbear.hpbtrader.options.model;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.entity.Trade;

/**
 *
 * @author rkolar
 */
public class TradeLot {
    private Trade trade;
    private OptEnums.Lot lot;

    public TradeLot(Trade trade, OptEnums.Lot lot) {
        this.trade = trade;
        this.lot = lot;
    }

    public Trade getTrade() {
        return trade;
    }

    public OptEnums.Lot getLot() {
        return lot;
    }
}
