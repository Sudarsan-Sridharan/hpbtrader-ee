package com.highpowerbear.hpbtrader.linear.strategy.logic;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.IbOrder;
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
    public IbOrder processSignals() {
        createOrder();
        if (ctx.activeTrade != null) {
            ibOrder.setTriggerDesc("testReverse: test reverse");
            ibOrder.setOrderAction(ctx.activeTrade.isLong() ? LinEnums.OrderAction.SREV : LinEnums.OrderAction.BREV);
            ibOrder.setQuantity(ibOrder.getQuantity() * 2);
        } else {
            ibOrder.setTriggerDesc("testOpen: test open");
            ibOrder.setOrderAction(LinEnums.OrderAction.BTO);
            ctx.activeTrade = new Trade().initOpen(ibOrder);
            setInitialStopAndTarget();
        }
        return ibOrder;
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
