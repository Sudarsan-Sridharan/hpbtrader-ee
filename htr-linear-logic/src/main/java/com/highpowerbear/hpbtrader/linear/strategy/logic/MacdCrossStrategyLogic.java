package com.highpowerbear.hpbtrader.linear.strategy.logic;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.mktdata.indicator.Macd;
import com.highpowerbear.hpbtrader.linear.mktdata.indicator.Stochastics;
import com.highpowerbear.hpbtrader.linear.strategy.AbstractStrategyLogic;
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
    
    @Override
    public Order processSignals() {
        createOrder();
        if (ctx.activeTrade != null) {
            setPl();
            if (((ctx.activeTrade.isLong() && crossBelowMacd()) || (ctx.activeTrade.isShort() && crossAboveMacd()))) {
                order.setOrderAction(ctx.activeTrade.isLong() ? LinEnums.OrderAction.SREV : LinEnums.OrderAction.BREV);
                order.setTriggerDesc(getTriggerDesc(TriggerEvent.REVERSE));
                order.setQuantity(order.getQuantity() * 2);
            }
        } else if ((crossAboveMacd() || crossBelowMacd())) {
            order.setOrderAction(crossAboveMacd() ? LinEnums.OrderAction.BTO : LinEnums.OrderAction.STO);
            order.setTriggerDesc(getTriggerDesc(TriggerEvent.OPEN));
            ctx.activeTrade = new Trade().initOpen(order);
        }
        if (order.getOrderAction() == null) {
            order = null;
        }
        return order;
    }
    
    @Override
    public void setInitialStopAndTarget() {
    }

    @Override
    protected void reloadParameters() {
        String params[] = ctx.strategy.getParams().split(",");
        stochOversold = Integer.valueOf(params[0].trim());
        stochOverbought = Integer.valueOf(params[1].trim());
    }
    
    @Override
    protected void calculateIndicators() {
        List<Macd> macdList = tiCalculator.calculateMacd(ctx.bars);
        List<Stochastics> stochList = tiCalculator.calculateStoch(ctx.bars);
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
                if (ctx.activeTrade.isLong()) {
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