package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.entity.IbOrder;

/**
 *
 * @author robertk
 */
public interface StrategyLogic {
    void updateContext(StrategyLogicContext ctx);
    IbOrder processSignals();
    void setInitialStopAndTarget();
    String getName();
}