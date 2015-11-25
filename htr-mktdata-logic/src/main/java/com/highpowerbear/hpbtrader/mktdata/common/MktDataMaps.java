package com.highpowerbear.hpbtrader.mktdata.common;

import com.highpowerbear.hpbtrader.mktdata.model.RealtimeData;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.ibclient.IbConnection;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class MktDataMaps {
    @Inject private IbAccountDao ibAccountDao;

    private Map<IbAccount, IbConnection> ibConnectionMap = new HashMap<>(); // ibAccount --> ibConnection
    private Map<Series, Map<Long, Bar>> barsReceivedMap = new HashMap<>(); // series --> (timeInMillisBarClose --> bar
    private Map<Series, Integer> backfillStatusMap = new HashMap<>(); // series --> backfillStatus
    private Map<Integer, RealtimeData> realtimeDataMap = new LinkedHashMap<>(); // ib request id --> realtimeData

    @PostConstruct
    public void init() {
        for (IbAccount ibAccount : ibAccountDao.getIbAccounts()) {
            ibConnectionMap.put(ibAccount, new IbConnection());
        }
    }

    public Map<IbAccount, IbConnection> getIbConnectionMap() {
        return ibConnectionMap;
    }

    public Map<Series, Map<Long, Bar>> getBarsReceivedMap() {
        return barsReceivedMap;
    }

    public Map<Series, Integer> getBackfillStatusMap() {
        return backfillStatusMap;
    }

    public IbAccountDao getIbAccountDao() {
        return ibAccountDao;
    }

    public Map<Integer, RealtimeData> getRealtimeDataMap() {
        return realtimeDataMap;
    }
}
