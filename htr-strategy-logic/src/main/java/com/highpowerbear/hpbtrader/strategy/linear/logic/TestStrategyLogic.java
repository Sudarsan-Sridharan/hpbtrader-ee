package com.highpowerbear.hpbtrader.strategy.linear.logic;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;

/**
 *
 * @author robertk
 */
public class TestStrategyLogic extends AbstractStrategyLogic {
    // strategy parameters
    private Double stopPct;
    private Double targetPct;

    public TestStrategyLogic(ProcessContext ctx) {
        super(ctx);
    }

    @Override
    public void process() {
        createIbOrder();
        if (activeTrade != null) {
            ibOrder.setTriggerDesc("testReverse: test reverse");
            ibOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
            ibOrder.setQuantity(ibOrder.getQuantity() * 2);
        } else {
            ibOrder.setTriggerDesc("testOpen: test open");
            ibOrder.setOrderAction(HtrEnums.OrderAction.BTO);
            activeTrade = new Trade().initOpen(ibOrder, calculateInitialStop(stopPct), calculateTarget(targetPct));
        }
    }

    @Override
    protected void reloadParameters() {
        String params[] = ctx.getStrategy().getParams().split(",");
        stopPct = Double.valueOf(params[0].trim());
        targetPct = Double.valueOf(params[1].trim());
    }

    @Override
    protected void calculateIndicators() {
    }
}
