package com.highpowerbear.hpbtrader.linear.strategy.model;

import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import java.util.List;

/**
 *
 * @author robertk
 */
public class StrategyLogicContext {
    public Strategy strategy = null;
    public Trade activeTrade = null;
    public List<Quote> quotes;
    public boolean isBacktest = false;
}