package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.entity.IbOrder;
import com.highpowerbear.hpbtrader.linear.model.StrategyLogicContext;

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