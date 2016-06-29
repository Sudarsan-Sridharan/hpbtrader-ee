package com.highpowerbear.hpbtrader.strategy.logic;

import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.model.OperResult;
import com.highpowerbear.hpbtrader.strategy.process.ProcessContext;

import java.util.Calendar;

/**
 *
 * @author robertk
 */
public interface StrategyLogic {
    Strategy getStrategy();
    ProcessContext getProcessContext();
    OperResult<Boolean, String> prepare();
    OperResult<Boolean, String> prepare(Calendar lastDate);
    void process();
    IbOrder getIbOrder();
    Trade getActiveTrade();
    DataBar getLastDataBar();
}