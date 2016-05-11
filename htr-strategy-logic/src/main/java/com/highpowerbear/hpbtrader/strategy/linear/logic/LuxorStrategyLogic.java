package com.highpowerbear.hpbtrader.strategy.linear.logic;

import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.strategy.linear.AbstractStrategyLogic;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Ema;

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

    public LuxorStrategyLogic(Strategy strategy) {
         super(strategy);
    }

    @Override
    public IbOrder process() {
        createOrder();
        if (activeTrade != null) {
            setPl();
            setTrailStop(stopPct);
            if (targetMet()) {
                resultIbOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.STC : HtrEnums.OrderAction.BTC);
                resultIbOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.TARGET));
            } else if (stopTriggered()) {
                resultIbOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.STC : HtrEnums.OrderAction.BTC);
                resultIbOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.STOP));
            } else if (((activeTrade.isLong() && crossBelowEma()) || (activeTrade.isShort() && crossAboveEma())) && isTradeTime()) {
                resultIbOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
                resultIbOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.REVERSE));
                resultIbOrder.setQuantity(resultIbOrder.getQuantity() * 2);
            }
        } else if ((crossAboveEma() || crossBelowEma()) && isTradeTime()) {
            resultIbOrder.setOrderAction(crossAboveEma() ? HtrEnums.OrderAction.BTO : HtrEnums.OrderAction.STO);
            resultIbOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.OPEN));
            activeTrade = new Trade().initOpen(resultIbOrder, calculateInitialStop(stopPct), calculateTarget(targetPct));
        }
        if (resultIbOrder.getOrderAction() == null) {
            resultIbOrder = null;
        }
        return resultIbOrder;
    }
    
    @Override
    protected void reloadParameters() {
        String params[] = strategy.getParams().split(",");
        emaShortPeriod = Integer.valueOf(params[0].trim());
        emaLongPeriod = Integer.valueOf(params[1].trim());
        stopPct = Double.valueOf(params[2].trim());
        targetPct = Double.valueOf(params[3].trim());
        startHourEst = Integer.valueOf(params[4].trim());
        durationHours = Integer.valueOf(params[5].trim());
    }

    @Override
    protected void calculateIndicators() {
        List<Ema> emaShortList = tiCalculator.calculateEma(dataBars, emaShortPeriod);
        List<Ema> emaLongList = tiCalculator.calculateEma(dataBars, emaLongPeriod);
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
        int barHour = lastDataBar.getBarCloseDate().get(Calendar.HOUR_OF_DAY);
        return (barHour >= this.startHourEst && barHour < (this.startHourEst + this.durationHours));
    }
    
    private enum TriggerEvent {
        TARGET, STOP, REVERSE, OPEN
    }
    
    private String getTriggerDesc(TriggerEvent te) {
        String desc = null;
        switch(te) {
            case TARGET:
                if (activeTrade.isLong()) {
                    desc = "targetSTC: price=" + getPrice() + " > target=" + activeTrade.getProfitTarget();
                } else {
                    desc = "targetBTC: price=" + getPrice() + " < target=" + activeTrade.getProfitTarget();
                }
                break;
            case STOP:
                if (activeTrade.isLong()) {
                    desc = "stopSTC: price=" + getPrice() + " < stop=" + activeTrade.getStopLoss();
                } else {
                    desc = "stopBTC: price=" + getPrice() + " > stop=" + activeTrade.getStopLoss();
                }
                break;
            case REVERSE:
                if (activeTrade.isLong()) {
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
