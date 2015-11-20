package com.highpowerbear.hpbtrader.mktdata.common;

import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class MktDataMaps {
    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>();
    private Map<Integer, Integer> backfillStatusMap = new HashMap<>(); // seriesId --> backfillStatus
    private Map<Integer, List<Bar>> barsReceivedMap = new HashMap<>(); // seriesId --> barList
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

    public Map<Integer, String> getRealtimeDataRequestMap() {
        return realtimeDataRequestMap;
    }

    public Map<String, RealtimeData> getRealtimeDataMap() {
        return realtimeDataMap;
    }
}
