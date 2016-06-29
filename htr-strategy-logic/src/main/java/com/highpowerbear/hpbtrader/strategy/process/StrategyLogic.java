package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.OperResult;

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