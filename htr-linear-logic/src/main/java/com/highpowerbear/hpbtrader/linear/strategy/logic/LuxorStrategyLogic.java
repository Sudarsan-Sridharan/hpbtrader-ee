package com.highpowerbear.hpbtrader.linear.strategy.logic;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.mktdata.indicator.Ema;
import com.highpowerbear.hpbtrader.linear.strategy.AbstractStrategyLogic;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author rkolar
 */
public class LuxorStrategyLogic extends AbstractStrategyLogic {
    // strategy parameters
    private Double stopPct; // trailing stop calculated from current price
    private Double targetPct; // calculated from entry
    private Integer emaShortPeriod;
    private Integer emaLongPeriod;
    private Integer startHourEst;
    private Integer durationHours;
    
    // required indicators
    private Double prevEmaShortValue;
    private Double emaShortValue;
    private Double prevEmaLongValue;
    private Double emaLongValue;
    
    @Override
    public Order processSignals() {
        createOrder();
        if (ctx.activeTrade != null) {
            setPl();
            setTrailStop(stopPct);
            if (targetMet()) {
                order.setOrderAction(ctx.activeTrade.isLong() ? LinEnums.OrderAction.STC : LinEnums.OrderAction.BTC);
                order.setTriggerDesc(getTriggerDesc(TriggerEvent.TARGET));
            } else if (stopTriggered()) {
                order.setOrderAction(ctx.activeTrade.isLong() ? LinEnums.OrderAction.STC : LinEnums.OrderAction.BTC);
                order.setTriggerDesc(getTriggerDesc(TriggerEvent.STOP));
            } else if (((ctx.activeTrade.isLong() && crossBelowEma()) || (ctx.activeTrade.isShort() && crossAboveEma())) && isTradeTime()) {
                order.setOrderAction(ctx.activeTrade.isLong() ? LinEnums.OrderAction.SREV : LinEnums.OrderAction.BREV);
                order.setTriggerDesc(getTriggerDesc(TriggerEvent.REVERSE));
                order.setQuantity(order.getQuantity() * 2);
            }
        } else if ((crossAboveEma() || crossBelowEma()) && isTradeTime()) {
            order.setOrderAction(crossAboveEma() ? LinEnums.OrderAction.BTO : LinEnums.OrderAction.STO);
            order.setTriggerDesc(getTriggerDesc(TriggerEvent.OPEN));
            ctx.activeTrade = new Trade().initOpen(order);
            setInitialStopAndTarget();
        }
        if (order.getOrderAction() == null) {
            order = null;
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
        emaShortPeriod = Integer.valueOf(params[0].trim());
        emaLongPeriod = Integer.valueOf(params[1].trim());
        stopPct = Double.valueOf(params[2].trim());
        targetPct = Double.valueOf(params[3].trim());
        startHourEst = Integer.valueOf(params[4].trim());
        durationHours = Integer.valueOf(params[5].trim());
    }

    @Override
    protected void calculateIndicators() {
        List<Ema> emaShortList = tiCalculator.calculateEma(ctx.bars, emaShortPeriod);
        List<Ema> emaLongList = tiCalculator.calculateEma(ctx.bars, emaLongPeriod);
        prevEmaShortValue = emaShortList.get(emaShortList.size() - 2).getEma();
        emaShortValue = emaShortList.get(emaShortList.size() - 1).getEma();
        prevEmaLongValue = emaLongList.get(emaLongList.size() - 2).getEma();
        emaLongValue = emaLongList.get(emaLongList.size() - 1).getEma();
    }
      
    private boolean crossAboveEma() {
        return (prevEmaShortValue <= prevEmaLongValue && emaShortValue > emaLongValue);
    }
    
    private boolean crossBelowEma() {
        return (prevEmaShortValue >= prevEmaLongValue && emaShortValue < emaLongValue);
    }
    
    private boolean isTradeTime() {
        int barHour = bar.getqDateBarClose().get(Calendar.HOUR_OF_DAY);
        return (barHour >= this.startHourEst && barHour < (this.startHourEst + this.durationHours));
    }
    
    private enum TriggerEvent {
        TARGET, STOP, REVERSE, OPEN
    }
    
    private String getTriggerDesc(TriggerEvent te) {
        String desc = null;
        switch(te) {
            case TARGET:
                if (ctx.activeTrade.isLong()) {
                    desc = "targetSTC: price=" + getPrice() + " > target=" + ctx.activeTrade.getProfitTarget();
                } else {
                    desc = "targetBTC: price=" + getPrice() + " < target=" + ctx.activeTrade.getProfitTarget();
                }
                break;
            case STOP:
                if (ctx.activeTrade.isLong()) {
                    desc = "stopSTC: price=" + getPrice() + " < stop=" + ctx.activeTrade.getStopLoss();
                } else {
                    desc = "stopBTC: price=" + getPrice() + " > stop=" + ctx.activeTrade.getStopLoss();
                }
                break;
            case REVERSE:
                if (ctx.activeTrade.isLong()) {
                    desc = "sigSREV: " + getEmaDesc(EmaPeriod.SHORT) + " CB " + getEmaDesc(EmaPeriod.LONG);
                } else {
                    desc = "sigBREV: " + getEmaDesc(EmaPeriod.SHORT) + " CA " + getEmaDesc(EmaPeriod.LONG);
                }
                break;
            case OPEN:
                if (crossAboveEma()) {
                    desc = "sigBTO: " + getEmaDesc(EmaPeriod.SHORT) + " CA " + getEmaDesc(EmaPeriod.LONG);
                } else {
                    desc = "sigSTO: " + getEmaDesc(EmaPeriod.SHORT) + " CB " + getEmaDesc(EmaPeriod.LONG);
                }
                break;
        }
        return desc;
    }
    
    private enum EmaPeriod {
        SHORT, LONG
    }
    
    private String getEmaDesc(EmaPeriod ep) {
        String desc = "";
        switch (ep) {
            case SHORT: desc = "ema " + emaShortPeriod + "=" + doubleValueFormat.format(emaShortValue); break;
            case LONG: desc = "ema " + emaLongPeriod + "=" + doubleValueFormat.format(emaLongValue); break;
        }
        return desc;
    }
}
