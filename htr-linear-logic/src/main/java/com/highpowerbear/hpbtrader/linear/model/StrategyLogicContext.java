package com.highpowerbear.hpbtrader.linear.model;

import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;

import java.util.List;

/**
 *
 * @author robertk
 */
public class StrategyLogicContext {
    public Strategy strategy = null;
    public Trade activeTrade = null;
    public List<Bar> bars;
    public boolean isBacktest = false;
}