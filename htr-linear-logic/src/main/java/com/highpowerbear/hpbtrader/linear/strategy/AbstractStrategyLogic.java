package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.common.SingletonRepo;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.quote.TiCalculator;
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
    protected Quote quote;
    protected Quote prevQuote;
    protected NumberFormat doubleValueFormat = NumberFormat.getInstance(Locale.US);
    
    public AbstractStrategyLogic() {
        tiCalculator = SingletonRepo.getInstance().getTiCalculator();
        doubleValueFormat.setMinimumFractionDigits(6);
        doubleValueFormat.setMaximumFractionDigits(6);
    }
    
    @Override
    public void updateContext(StrategyLogicContext ctx) {
        this.ctx = ctx;
        quote = ctx.quotes.get(ctx.quotes.size() - 1);
        prevQuote = ctx.quotes.get(ctx.quotes.size() - 2);
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
        order.addEvent(LinEnums.OrderStatus.NEW, (ctx.isBacktest ? quote.getqDateBarClose() : LinUtil.getCalendar()));
    }
    
    protected Double getPrice() {
        return quote.getqClose();
    }
    
    protected boolean targetMet() {
        if (ctx.activeTrade == null || ctx.activeTrade.getProfitTarget() == null) {
            return false;
        }
        return (ctx.activeTrade.isLong() ? (quote.getqClose() >= ctx.activeTrade.getProfitTarget()) : (quote.getqClose() <= ctx.activeTrade.getProfitTarget()));
    }
    
    protected boolean stopTriggered() {
        if (ctx.activeTrade == null || ctx.activeTrade.getStopLoss() == null) {
            return false;
        }
        return (ctx.activeTrade.isLong() ? (quote.getqClose() <= ctx.activeTrade.getStopLoss()) : (quote.getqClose() >= ctx.activeTrade.getStopLoss()));
    }
    
    protected void setInitialStop(Double stopPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        Double stop = (ctx.activeTrade.isLong() ? LinUtil.round5(quote.getqClose() - (stopPct / 100.0) * quote.getqClose()) : LinUtil.round5(quote.getqClose() + (stopPct / 100.0) * quote.getqClose()));
        ctx.activeTrade.setStopLoss(stop);
        ctx.activeTrade.setInitialStop(stop);
    }
    
    protected void setTrailStop(Double stopPct) {
        if (ctx.activeTrade == null) {
            return;
        }
        if (ctx.activeTrade.isLong()) {
            if (quote.getqClose() > prevQuote.getqClose()) {
                Double newStop = LinUtil.round5(quote.getqClose() - (stopPct / 100.0) * quote.getqClose());
                if (newStop > ctx.activeTrade.getStopLoss()) {
                    ctx.activeTrade.setStopLoss(newStop);
                }
            }
        } else {
            if (quote.getqClose() < prevQuote.getqClose()) {
                Double newStop = LinUtil.round5(quote.getqClose() + (stopPct / 100.0) * quote.getqClose());
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
        ctx.activeTrade.setProfitTarget(ctx.activeTrade.isLong() ? LinUtil.round5(quote.getqClose() + (targetPct / 100.0) * quote.getqClose()) : LinUtil.round5(quote.getqClose() - (targetPct / 100.0) * quote.getqClose()));
    }
    
    protected void setPl() {
        if (ctx.activeTrade == null || ctx.activeTrade.getOpenPrice() == null) {
            return;
        }
        Double unrealizedPl = (ctx.activeTrade.isLong() ? LinUtil.round5((quote.getqClose() - ctx.activeTrade.getOpenPrice()) * ctx.strategy.getTradingQuantity()) : LinUtil.round5((ctx.activeTrade.getOpenPrice() - quote.getqClose()) * ctx.strategy.getTradingQuantity()));
        if (LinEnums.SecType.FUT.equals(ctx.strategy.getSeries().getSecType())) {
            unrealizedPl *= LinEnums.FutureMultiplier.getMultiplierBySymbol(ctx.strategy.getSeries().getSymbol());
        }
        if (LinEnums.SecType.OPT.equals(ctx.strategy.getSeries().getSecType())) {
            unrealizedPl *= (LinEnums.MiniOption.isMiniOption(ctx.strategy.getSeries().getSymbol()) ? 10 : 100);
        }
        ctx.activeTrade.setUnrealizedPl(unrealizedPl);
    }
}
