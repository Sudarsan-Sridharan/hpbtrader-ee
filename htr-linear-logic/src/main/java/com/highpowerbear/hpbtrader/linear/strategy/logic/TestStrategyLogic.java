package com.highpowerbear.hpbtrader.linear.strategy.logic;

import com.highpowerbear.hpbtrader.linear.strategy.AbstractStrategyLogic;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;
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

    @Override
    public IbOrder processSignals() {
        createOrder();
        if (ctx.activeTrade != null) {
            ibOrder.setTriggerDesc("testReverse: test reverse");
            ibOrder.setOrderAction(ctx.activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
            ibOrder.setQuantity(ibOrder.getQuantity() * 2);
        } else {
            ibOrder.setTriggerDesc("testOpen: test open");
            ibOrder.setOrderAction(HtrEnums.OrderAction.BTO);
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
