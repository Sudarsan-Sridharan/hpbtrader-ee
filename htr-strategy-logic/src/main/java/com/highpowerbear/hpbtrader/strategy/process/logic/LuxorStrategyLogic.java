package com.highpowerbear.hpbtrader.strategy.process.logic;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Ema;
import com.highpowerbear.hpbtrader.strategy.process.ProcessContext;

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

    public LuxorStrategyLogic(ProcessContext ctx) {
         super(ctx);
    }

    @Override
    public void process() {
        createIbOrder();
        if (activeTrade != null) {
            setPl();
            setTrailStop(stopPct);
            if (targetMet()) {
                ibOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.STC : HtrEnums.OrderAction.BTC);
                ibOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.TARGET));
            } else if (stopTriggered()) {
                ibOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.STC : HtrEnums.OrderAction.BTC);
                ibOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.STOP));
            } else if (((activeTrade.isLong() && crossBelowEma()) || (activeTrade.isShort() && crossAboveEma())) && isTradeTime()) {
                ibOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
                ibOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.REVERSE));
                ibOrder.setQuantity(ibOrder.getQuantity() * 2);
            }
        } else if ((crossAboveEma() || crossBelowEma()) && isTradeTime()) {
            ibOrder.setOrderAction(crossAboveEma() ? HtrEnums.OrderAction.BTO : HtrEnums.OrderAction.STO);
            ibOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.OPEN));
            activeTrade = new Trade().initOpen(ibOrder, calculateInitialStop(stopPct), calculateTarget(targetPct));
        }
        if (ibOrder.getOrderAction() == null) {
            ibOrder = null;
        }
    }
    
    @Override
    protected void reloadParameters() {
        String params[] = ctx.getStrategy().getParams().split(",");
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
        boolean tLong = activeTrade.isLong();
        boolean caEma = crossAboveEma();
        String sPrice = "price=" + getPrice();
        String sTarget = "target=" + activeTrade.getProfitTarget();
        String sStop = "stop=" + activeTrade.getStopLoss();
        String sEmaSh = "ema " + emaShortPeriod + "=" + nf.format(emaShortValue);
        String sEmaLo = "ema " + emaLongPeriod  + "=" + nf.format(emaLongValue);

        switch(te) {
            case TARGET:  desc = tLong ? "targetSTC: " + sPrice + " > "  + sTarget : "targetBTC: "  + sPrice + " < "  + sTarget; break;
            case STOP:    desc = tLong ? "stopSTC: "   + sPrice + " < "  + sStop :   "stopBTC: "    + sPrice + " > "  + sStop;   break;
            case REVERSE: desc = tLong ? "sigSREV: "   + sEmaSh + " CB " + sEmaLo :  "sigBREV: "    + sEmaSh + " CA " + sEmaLo;  break;
            case OPEN:    desc = caEma ? "sigBTO: "    + sEmaSh + " CA " + sEmaLo :  "sigSTO: "     + sEmaSh + " CB " + sEmaLo;  break;
        }
        return desc;
    }
}
