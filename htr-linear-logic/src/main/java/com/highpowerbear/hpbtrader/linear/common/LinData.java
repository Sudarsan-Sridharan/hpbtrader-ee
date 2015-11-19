package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.model.RealtimeData;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogic;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.model.IbConnection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class LinData {
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>();
    private Map<IbAccount, Integer> validOrderMap = new HashMap<>();
    private Map<Integer, Integer> backfillStatusMap = new HashMap<>(); // seriesId --> backfillStatus
    private Map<Integer, List<Bar>> barsReceivedMap = new HashMap<>(); // seriesId --> barList
    private Map<Integer, StrategyLogic> strategyLogicMap = new HashMap<>(); // seriesId --> strategyLogic
    private Map<IbAccount, Map<IbOrder, Integer>> openOrderHeartbeatMap = new ConcurrentHashMap<>(); // ibAccount --> (ibOrder --> number of failed heartbeats left before UNKNOWN)
    private Map<Integer, String> realtimeDataRequestMap = new HashMap<>(); // ib request id --> contract string
    private Map<String, RealtimeData> realtimeDataMap = new LinkedHashMap<>(); // contract string --> realtimeData

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    public Map<Integer, Integer> getBackfillStatusMap() {
        return backfillStatusMap;
    }

    public Map<Integer, List<Bar>> getBarsReceivedMap() {
        return barsReceivedMap;
    }

    public Map<Integer, StrategyLogic> getStrategyLogicMap() {
        return strategyLogicMap;
    }

    public Map<IbAccount, Map<IbOrder, Integer>> getOpenOrderHeartbeatMap() {
        return openOrderHeartbeatMap;
    }

    public Map<Integer, String> getRealtimeDataRequestMap() {
        return realtimeDataRequestMap;
    }

    public Map<String, RealtimeData> getRealtimeDataMap() {
        return realtimeDataMap;
    }
}
