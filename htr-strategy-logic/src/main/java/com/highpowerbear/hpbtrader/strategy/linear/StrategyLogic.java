package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.OperResult;

import java.util.Calendar;

/**
 *
 * @author robertk
 */
public interface StrategyLogic {
    OperResult<Boolean, String> prepare(int offsetFromLast);
    OperResult<Boolean, String> prepare(Calendar lastDate);
    void process();
    IbOrder getIbOrder();
    Trade getActiveTrade();
    DataBar getLastDataBar();
}