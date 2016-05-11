package com.highpowerbear.hpbtrader.strategy.linear.logic;

import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.strategy.linear.AbstractStrategyLogic;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Trade;

/**
 *
 * @author robertk
 */
public class TestStrategyLogic extends AbstractStrategyLogic {
    // strategy parameters
    private Double stopPct;
    private Double targetPct;

    public TestStrategyLogic(Strategy strategy) {
        super(strategy);
    }

    @Override
    public IbOrder process() {
        createOrder();
        if (activeTrade != null) {
            resultIbOrder.setTriggerDesc("testReverse: test reverse");
            resultIbOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
            resultIbOrder.setQuantity(resultIbOrder.getQuantity() * 2);
        } else {
            resultIbOrder.setTriggerDesc("testOpen: test open");
            resultIbOrder.setOrderAction(HtrEnums.OrderAction.BTO);
            activeTrade = new Trade().initOpen(resultIbOrder, calculateInitialStop(stopPct), calculateTarget(targetPct));
        }
        return resultIbOrder;
    }

    @Override
    protected void reloadParameters() {
        String params[] = strategy.getParams().split(",");
        stopPct = Double.valueOf(params[0].trim());
        targetPct = Double.valueOf(params[1].trim());
    }

    @Override
    protected void calculateIndicators() {
    }
}
