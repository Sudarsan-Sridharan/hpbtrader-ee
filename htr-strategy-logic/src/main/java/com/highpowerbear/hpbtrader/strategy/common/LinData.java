package com.highpowerbear.hpbtrader.strategy.common;

import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class LinData {
    private Map<IbAccount, Integer> validOrderMap = new HashMap<>();
    private Map<Integer, StrategyLogic> strategyLogicMap = new HashMap<>(); // seriesId --> strategyLogic

    public Map<Integer, StrategyLogic> getStrategyLogicMap() {
        return strategyLogicMap;
    }
}
