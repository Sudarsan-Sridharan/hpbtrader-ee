package com.highpowerbear.hpbtrader.strategy.linear.logic;

import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.strategy.linear.AbstractStrategyLogic;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Macd;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Stochastics;

import java.util.List;

/**
 *
 * @author robertk
 */
public class MacdCrossStrategyLogic extends AbstractStrategyLogic {
    // strategy parameters
    private Integer stochOversold;
    private Integer stochOverbought;

    // required indicators
    private Double prevMacdSl;
    private Double macdSl;
    private Double prevMacdL;
    private Double macdL;
    private Double stochD;

    public MacdCrossStrategyLogic(Strategy strategy) {
        super(strategy);
    }

    @Override
    public IbOrder process() {
        createOrder();
        if (activeTrade != null) {
            setPl();
            if (((activeTrade.isLong() && crossBelowMacd()) || (activeTrade.isShort() && crossAboveMacd()))) {
                resultIbOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
                resultIbOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.REVERSE));
                resultIbOrder.setQuantity(resultIbOrder.getQuantity() * 2);
            }
        } else if ((crossAboveMacd() || crossBelowMacd())) {
            resultIbOrder.setOrderAction(crossAboveMacd() ? HtrEnums.OrderAction.BTO : HtrEnums.OrderAction.STO);
            resultIbOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.OPEN));
            activeTrade = new Trade().initOpen(resultIbOrder, null, null);
        }
        if (resultIbOrder.getOrderAction() == null) {
            resultIbOrder = null;
        }
        return resultIbOrder;
    }


    @Override
    protected void reloadParameters() {
        String params[] = strategy.getParams().split(",");
        stochOversold = Integer.valueOf(params[0].trim());
        stochOverbought = Integer.valueOf(params[1].trim());
    }
    
    @Override
    protected void calculateIndicators() {
        List<Macd> macdList = tiCalculator.calculateMacd(dataBars);
        List<Stochastics> stochList = tiCalculator.calculateStoch(dataBars);
        prevMacdL = macdList.get(macdList.size() - 2).getMacdL();
        macdL = macdList.get(macdList.size() - 1).getMacdL();
        prevMacdSl = macdList.get(macdList.size() - 2).getMacdSl();
        macdSl = macdList.get(macdList.size() - 1).getMacdSl();
        stochD = stochList.get(stochList.size() - 1).getStochD();
    }

    private boolean crossAboveMacd() {
        return (prevMacdL <= prevMacdSl && macdL > macdSl && stochD < stochOversold);
    }

    private boolean crossBelowMacd() {
        return (prevMacdL >= prevMacdSl && macdL < macdSl && stochD > stochOverbought);
    }

    private enum TriggerEvent {
        REVERSE, OPEN
    }

    private String getTriggerDesc(TriggerEvent te) {
        String desc = null;
        switch(te) {
            case REVERSE:
                if (activeTrade.isLong()) {
                    desc = "sigSREV: " + getValueDesc(ValueType.MACDL) + " CB " + getValueDesc(ValueType.MACDSL) + " AND " + getValueDesc(ValueType.STOCHD) + " > " + stochOverbought;
                } else {
                    desc = "sigBREV: " + getValueDesc(ValueType.MACDL) + " CA " + getValueDesc(ValueType.MACDSL) + " AND " + getValueDesc(ValueType.STOCHD) + " < " + stochOversold;
                }
                break;
            case OPEN:
                if (crossAboveMacd()) {
                    desc = "sigBTO: " + getValueDesc(ValueType.MACDL) + " CA " + getValueDesc(ValueType.MACDSL) + " AND " + getValueDesc(ValueType.STOCHD) + " < " + stochOversold;
                } else {
                    desc = "sigSTO: " + getValueDesc(ValueType.MACDL) + " CB " + getValueDesc(ValueType.MACDSL) + " AND " + getValueDesc(ValueType.STOCHD) + " > " + stochOverbought;
                }
                break;
        }
        return desc;
    }

    private enum ValueType {
        MACDL, MACDSL, STOCHD
    }

    private String getValueDesc(ValueType vt) {
        String desc = "";
        switch (vt) {
            case MACDL: desc = "macdL = " + doubleValueFormat.format(macdL); break;
            case MACDSL: desc = "macdSl = " + doubleValueFormat.format(macdSl); break;
            case STOCHD: desc = "stochD = " + doubleValueFormat.format(stochD); break;
        }
        return desc;
    }
}