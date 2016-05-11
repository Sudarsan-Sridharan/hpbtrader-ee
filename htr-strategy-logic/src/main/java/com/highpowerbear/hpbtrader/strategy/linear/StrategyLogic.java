package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.entity.IbOrder;

/**
 *
 * @author robertk
 */
public interface StrategyLogic {
    boolean preflight(boolean backtest);
    IbOrder process();
    String getInfo();
}