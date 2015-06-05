package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.quote.model.RealtimeData;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogic;
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
    private Map<Integer, Integer> backfillStatusMap = new HashMap<>(); // seriesId --> backfillStatus
    private Map<Integer, List<Quote>> quotesReceivedMap = new HashMap<>(); // seriesId --> quoteList
    private Map<Integer, StrategyLogic> strategyLogicMap = new HashMap<>(); // seriesId --> strategyLogic
    private Map<Long, Integer> openOrderHeartbeatMap = new ConcurrentHashMap<>(); // order dbId --> number of failed heartbeats left before declaring order UNKNOWN
    private Map<Integer, String> realtimeDataRequestMap = new HashMap<>(); // ib request id --> contract string
    private Map<String, RealtimeData> realtimeDataMap = new LinkedHashMap<>(); // contract string --> realtimeData

    public Map<Integer, Integer> getBackfillStatusMap() {
        return backfillStatusMap;
    }

    public Map<Integer, List<Quote>> getQuotesReceivedMap() {
        return quotesReceivedMap;
    }

    public Map<Integer, StrategyLogic> getStrategyLogicMap() {
        return strategyLogicMap;
    }

    public Map<Long, Integer> getOpenOrderHeartbeatMap() {
        return openOrderHeartbeatMap;
    }

    public Map<Integer, String> getRealtimeDataRequestMap() {
        return realtimeDataRequestMap;
    }

    public Map<String, RealtimeData> getRealtimeDataMap() {
        return realtimeDataMap;
    }
}
