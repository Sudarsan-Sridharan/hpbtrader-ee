package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.OperResult;

/**
 *
 * @author robertk
 */
public interface StrategyLogic {
    OperResult<Boolean, String> prepare(int offset);
    IbOrder process();
    DataSeries getInputDataSeries();
    Strategy getStrategy();
    Trade getActiveTrade();
    DataBar getLastDataBar();
}