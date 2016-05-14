package com.highpowerbear.hpbtrader.strategy.linear.logic;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Macd;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Stochastics;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;

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

    public MacdCrossStrategyLogic(ProcessContext ctx) {
        super(ctx);
    }

    @Override
    public void process() {
        createIbOrder();
        if (activeTrade != null) {
            setPl();
            if (((activeTrade.isLong() && crossBelowMacd()) || (activeTrade.isShort() && crossAboveMacd()))) {
                ibOrder.setOrderAction(activeTrade.isLong() ? HtrEnums.OrderAction.SREV : HtrEnums.OrderAction.BREV);
                ibOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.REVERSE));
                ibOrder.setQuantity(ibOrder.getQuantity() * 2);
            }
        } else if ((crossAboveMacd() || crossBelowMacd())) {
            ibOrder.setOrderAction(crossAboveMacd() ? HtrEnums.OrderAction.BTO : HtrEnums.OrderAction.STO);
            ibOrder.setTriggerDesc(getTriggerDesc(TriggerEvent.OPEN));
            activeTrade = new Trade().initOpen(ibOrder, null, null);
        }
        if (ibOrder.getOrderAction() == null) {
            ibOrder = null;
        }
    }


    @Override
    protected void reloadParameters() {
        String params[] = ctx.getStrategy().getParams().split(",");
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
        boolean tLong = activeTrade.isLong();
        boolean caMacd = crossAboveMacd();
        String sMacdL = "macdL = " + nf.format(macdL);
        String sMacdSl = "macdSl = " + nf.format(macdSl);
        String sStochD = "stochD = " + nf.format(stochD);

        switch(te) {
            case REVERSE: desc = tLong  ? "sigSREV: " + sMacdL + " CB "  + sMacdSl + " AND " + sStochD + " > " + stochOverbought : "sigBREV: "  + sMacdL + " CA "  + sMacdSl + " AND " + sStochD + " < " + stochOversold ;   break;
            case OPEN:    desc = caMacd ? "sigBTO: " +  sMacdL + " CA "  + sMacdSl + " AND " + sStochD + " < " + stochOversold   : "sigSTO: "   + sMacdL + " CB "  + sMacdSl + " AND " + sStochD + " > " + stochOverbought ; break;
        }
        return desc;
    }
}