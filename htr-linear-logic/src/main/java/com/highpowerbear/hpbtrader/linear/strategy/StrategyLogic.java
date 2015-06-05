package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.strategy.model.StrategyLogicContext;

/**
 *
 * @author robertk
 */
public interface StrategyLogic {
    void updateContext(StrategyLogicContext ctx);
    Order processSignals();
    void setInitialStopAndTarget();
    String getName();
}