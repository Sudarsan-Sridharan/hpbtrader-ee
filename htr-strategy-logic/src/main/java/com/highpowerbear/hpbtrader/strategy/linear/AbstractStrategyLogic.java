package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;
import com.highpowerbear.hpbtrader.strategy.common.SingletonRepo;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author rkolar
 */
public abstract class AbstractStrategyLogic implements StrategyLogic {
    protected TiCalculator tiCalculator;
    protected StrategyLogicContext ctx;
    protected IbOrder ibOrder;
    protected DataBar dataBar;
    protected DataBar prevDataBar;
    protected NumberFormat doubleValueFormat = NumberFormat.getInstance(Locale.US);
    
    public AbstractStrategyLogic() {
        tiCalculator = SingletonRepo.getInstance().getTiCalculator();
        doubleValueFormat.setMinimumFractionDigits(6);
        doubleValueFormat.setMaximumFractionDigits(6);
    }
    
    @Override
    public void updateContext(StrategyLogicContext ctx) {
        this.ctx = ctx;
        dataBar = ctx.dataBars.get(ctx.dataBars.size() - 1);
        prevDataBar = ctx.dataBars.get(ctx.dataBars.size() - 2);
        reloadParameters();
        calculateIndicators();
    }
    
    @Override
    public abstract IbOrder processSignals();
    
    @Override
    public abstract void setInitialStopAndTarget();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    protected abstract void calculateIndicators();
    protected abstract void reloadParameters();
    
    protected void createOrder() {
        ibOrder = new IbOrder();
        ibOrder.setStrategy(ctx.strategy);
        ibOrder.setStrategyMode(ctx.strategy.getStrategyMode());
        ibOrder.setSubmitType(HtrEnums.SubmitType.AUTO);
        ibOrder.setQuantity(ctx.strategy.getTradingQuantity());
        ibOrder.setOrderType(HtrEnums.OrderType.MKT);
        ibOrder.setLimitPrice(null); // N/A for market order
        ibOrder.setStopPrice(null); // N/A for market order
        ibOrder.addEvent(HtrEnums.IbOrderStatus.NEW, (ctx.isBacktest ? dataBar.getbCloseDate() : HtrUtil.getCalendar()), null);
    }
    
    protected Double getPrice() {
        return dataBar.getbClose();
    }
    
    protected boolean targetMet() {
        if (ctx.activeTrade == null || ctx.activeTrade.getProfitTarget() == null) {
            return false;
        }
        return (ctx.activeTrade.isLong() ? (dataBar.getbClose() >= ctx.activeTrade.getProfitTarget()) : (dataBar.getbClose() <= ctx.activeTrade.getProfitTarget()));
    }
    
    protected boolean stopTriggered() {
        if (ctx.activeTrade == null || ctx.activeTrade.getStopLoss() == null) {
            return false;
        }
        return (ctx.activeTrade.isLong() ? (dataBar.getbClose() <= ctx.activeTrade.getStopLoss()) : (dataBar.getbClose() >= ctx.activeTrade.getStopLoss()));
    }
    
    protected void setInitialStop(Double stopPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        Double stop = (ctx.activeTrade.isLong() ? HtrUtil.round5(dataBar.getbClose() - (stopPct / 100.0) * dataBar.getbClose()) : HtrUtil.round5(dataBar.getbClose() + (stopPct / 100.0) * dataBar.getbClose()));
        ctx.activeTrade.setStopLoss(stop);
        ctx.activeTrade.setInitialStop(stop);
    }
    
    protected void setTrailStop(Double stopPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        if (ctx.activeTrade.isLong()) {
            if (dataBar.getbClose() > prevDataBar.getbClose()) {
                Double newStop = HtrUtil.round5(dataBar.getbClose() - (stopPct / 100.0) * dataBar.getbClose());
                if (newStop > ctx.activeTrade.getStopLoss()) {
                    ctx.activeTrade.setStopLoss(newStop);
                }
            }
        } else {
            if (dataBar.getbClose() < prevDataBar.getbClose()) {
                Double newStop = HtrUtil.round5(dataBar.getbClose() + (stopPct / 100.0) * dataBar.getbClose());
                if (newStop < ctx.activeTrade.getStopLoss()) {
                    ctx.activeTrade.setStopLoss(newStop);
                }
            }
        }
    }
    
    protected void setTarget(Double targetPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        ctx.activeTrade.setProfitTarget(ctx.activeTrade.isLong() ? HtrUtil.round5(dataBar.getbClose() + (targetPct / 100.0) * dataBar.getbClose()) : HtrUtil.round5(dataBar.getbClose() - (targetPct / 100.0) * dataBar.getbClose()));
    }
    
    protected void setPl() {
        if (ctx.activeTrade == null || ctx.activeTrade.getOpenPrice() == null) {
            return;
        }
        Double unrealizedPl = (ctx.activeTrade.isLong() ? HtrUtil.round5((dataBar.getbClose() - ctx.activeTrade.getOpenPrice()) * ctx.strategy.getTradingQuantity()) : HtrUtil.round5((ctx.activeTrade.getOpenPrice() - dataBar.getbClose()) * ctx.strategy.getTradingQuantity()));
        if (HtrEnums.SecType.FUT.equals(ctx.strategy.getTradeInstrument().getSecType())) {
            unrealizedPl *= HtrEnums.FutureMultiplier.getMultiplierBySymbol(ctx.strategy.getTradeInstrument().getSymbol());
        }
        if (HtrEnums.SecType.OPT.equals(ctx.strategy.getTradeInstrument().getSecType())) {
            unrealizedPl *= (HtrEnums.MiniOption.isMiniOption(ctx.strategy.getTradeInstrument().getSymbol()) ? 10 : 100);
        }
        ctx.activeTrade.setUnrealizedPl(unrealizedPl);
    }
}
