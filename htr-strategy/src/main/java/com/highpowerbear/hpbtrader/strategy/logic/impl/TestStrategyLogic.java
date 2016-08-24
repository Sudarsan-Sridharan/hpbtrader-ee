package com.highpowerbear.hpbtrader.strategy.logic.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;
import com.highpowerbear.hpbtrader.strategy.logic.AbstractStrategyLogic;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author robertk
 */
@Dependent
public class TestStrategyLogic extends AbstractStrategyLogic {

    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private TiCalculator tiCalculator;

    // strategy parameters
    private Double stopPct;
    private Double targetPct;

    @PostConstruct
    private void postConstruct() {
        super.dataSeriesDao = dataSeriesDao;
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
