package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogic;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class LinData {
    private Map<IbAccount, Integer> validOrderMap = new HashMap<>();
    private Map<Integer, StrategyLogic> strategyLogicMap = new HashMap<>(); // seriesId --> strategyLogic
    private Map<IbAccount, Map<IbOrder, Integer>> openOrderHeartbeatMap = new ConcurrentHashMap<>(); // ibAccount --> (ibOrder --> number of failed heartbeats left before UNKNOWN)

    public Map<Integer, StrategyLogic> getStrategyLogicMap() {
        return strategyLogicMap;
    }

    public Map<IbAccount, Map<IbOrder, Integer>> getOpenOrderHeartbeatMap() {
        return openOrderHeartbeatMap;
    }
}
