package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.entity.DataBar;
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
    public List<DataBar> dataBars;
    public boolean isBacktest = false;
}