package com.highpowerbear.hpbtrader.linear.strategy.logic;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.strategy.AbstractStrategyLogic;

/**
 *
 * @author robertk
 */
public class TestStrategyLogic extends AbstractStrategyLogic {
    // strategy parameters
    private Double stopPct;
    private Double targetPct;

    @Override
    public Order processSignals() {
        createOrder();
        if (ctx.activeTrade != null) {
            order.setTriggerDesc("testReverse: test reverse");
            order.setOrderAction(ctx.activeTrade.isLong() ? LinEnums.OrderAction.SREV : LinEnums.OrderAction.BREV);
            order.setQuantity(order.getQuantity() * 2);
        } else {
            order.setTriggerDesc("testOpen: test open");
            order.setOrderAction(LinEnums.OrderAction.BTO);
            ctx.activeTrade = new Trade().initOpen(order);
            setInitialStopAndTarget();
        }
        return order;
    }
    
    @Override
    public void setInitialStopAndTarget() {
        setInitialStop(stopPct);
        setTarget(targetPct);
    }

    @Override
    protected void reloadParameters() {
        String params[] = ctx.strategy.getParams().split(",");
        stopPct = Double.valueOf(params[0].trim());
        targetPct = Double.valueOf(params[1].trim());
    }

    @Override
    protected void calculateIndicators() {
    }
}
