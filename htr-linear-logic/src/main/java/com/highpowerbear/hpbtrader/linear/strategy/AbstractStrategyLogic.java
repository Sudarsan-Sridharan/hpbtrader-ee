package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.common.SingletonRepo;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.Bar;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.mktdata.TiCalculator;
import com.highpowerbear.hpbtrader.linear.strategy.model.StrategyLogicContext;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author rkolar
 */
public abstract class AbstractStrategyLogic implements StrategyLogic {
    protected TiCalculator tiCalculator;
    protected StrategyLogicContext ctx;
    protected Order order;
    protected Bar bar;
    protected Bar prevBar;
    protected NumberFormat doubleValueFormat = NumberFormat.getInstance(Locale.US);
    
    public AbstractStrategyLogic() {
        tiCalculator = SingletonRepo.getInstance().getTiCalculator();
        doubleValueFormat.setMinimumFractionDigits(6);
        doubleValueFormat.setMaximumFractionDigits(6);
    }
    
    @Override
    public void updateContext(StrategyLogicContext ctx) {
        this.ctx = ctx;
        bar = ctx.bars.get(ctx.bars.size() - 1);
        prevBar = ctx.bars.get(ctx.bars.size() - 2);
        reloadParameters();
        calculateIndicators();
    }
    
    @Override
    public abstract Order processSignals();
    
    @Override
    public abstract void setInitialStopAndTarget();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
    
    protected abstract void calculateIndicators();
    protected abstract void reloadParameters();
    
    protected void createOrder() {
        order = new Order();
        order.setStrategy(ctx.strategy);
        order.setStrategyMode(ctx.strategy.getStrategyMode());
        order.setSubmitType(LinEnums.SubmitType.AUTO);
        order.setQuantity(ctx.strategy.getTradingQuantity());
        order.setOrderType(LinEnums.OrderType.MKT);
        order.setLimitPrice(null); // N/A for market order
        order.setStopPrice(null); // N/A for market order
        order.addEvent(LinEnums.OrderStatus.NEW, (ctx.isBacktest ? bar.getqDateBarClose() : LinUtil.getCalendar()));
    }
    
    protected Double getPrice() {
        return bar.getqClose();
    }
    
    protected boolean targetMet() {
        if (ctx.activeTrade == null || ctx.activeTrade.getProfitTarget() == null) {
            return false;
        }
        return (ctx.activeTrade.isLong() ? (bar.getqClose() >= ctx.activeTrade.getProfitTarget()) : (bar.getqClose() <= ctx.activeTrade.getProfitTarget()));
    }
    
    protected boolean stopTriggered() {
        if (ctx.activeTrade == null || ctx.activeTrade.getStopLoss() == null) {
            return false;
        }
        return (ctx.activeTrade.isLong() ? (bar.getqClose() <= ctx.activeTrade.getStopLoss()) : (bar.getqClose() >= ctx.activeTrade.getStopLoss()));
    }
    
    protected void setInitialStop(Double stopPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        Double stop = (ctx.activeTrade.isLong() ? LinUtil.round5(bar.getqClose() - (stopPct / 100.0) * bar.getqClose()) : LinUtil.round5(bar.getqClose() + (stopPct / 100.0) * bar.getqClose()));
        ctx.activeTrade.setStopLoss(stop);
        ctx.activeTrade.setInitialStop(stop);
    }
    
    protected void setTrailStop(Double stopPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        if (ctx.activeTrade.isLong()) {
            if (bar.getqClose() > prevBar.getqClose()) {
                Double newStop = LinUtil.round5(bar.getqClose() - (stopPct / 100.0) * bar.getqClose());
                if (newStop > ctx.activeTrade.getStopLoss()) {
                    ctx.activeTrade.setStopLoss(newStop);
                }
            }
        } else {
            if (bar.getqClose() < prevBar.getqClose()) {
                Double newStop = LinUtil.round5(bar.getqClose() + (stopPct / 100.0) * bar.getqClose());
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
        ctx.activeTrade.setProfitTarget(ctx.activeTrade.isLong() ? LinUtil.round5(bar.getqClose() + (targetPct / 100.0) * bar.getqClose()) : LinUtil.round5(bar.getqClose() - (targetPct / 100.0) * bar.getqClose()));
    }
    
    protected void setPl() {
        if (ctx.activeTrade == null || ctx.activeTrade.getOpenPrice() == null) {
            return;
        }
        Double unrealizedPl = (ctx.activeTrade.isLong() ? LinUtil.round5((bar.getqClose() - ctx.activeTrade.getOpenPrice()) * ctx.strategy.getTradingQuantity()) : LinUtil.round5((ctx.activeTrade.getOpenPrice() - bar.getqClose()) * ctx.strategy.getTradingQuantity()));
        if (LinEnums.SecType.FUT.equals(ctx.strategy.getSeries().getSecType())) {
            unrealizedPl *= LinEnums.FutureMultiplier.getMultiplierBySymbol(ctx.strategy.getSeries().getSymbol());
        }
        if (LinEnums.SecType.OPT.equals(ctx.strategy.getSeries().getSecType())) {
            unrealizedPl *= (LinEnums.MiniOption.isMiniOption(ctx.strategy.getSeries().getSymbol()) ? 10 : 100);
        }
        ctx.activeTrade.setUnrealizedPl(unrealizedPl);
    }
}
