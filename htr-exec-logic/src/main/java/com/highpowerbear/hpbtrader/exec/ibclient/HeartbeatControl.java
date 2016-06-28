package com.highpowerbear.hpbtrader.exec.ibclient;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rkolar on 5/9/14.
 */
@ApplicationScoped
public class HeartbeatControl {
    @Inject private IbOrderDao ibOrderDao;
    @Inject private IbAccountDao ibAccountDao;

    private Map<IbAccount, Map<IbOrder, Integer>> openOrderHeartbeatMap = new HashMap<>(); // ibAccount --> (ibOrder --> number of failed heartbeats left before UNKNOWN)

    @PostConstruct
    public void init() {
        ibAccountDao.getIbAccounts().forEach(ibAccount -> openOrderHeartbeatMap.put(ibAccount, new ConcurrentHashMap<>()));
        ibAccountDao.getIbAccounts().stream()
                .flatMap(ibAccount -> ibOrderDao.getOpenIbOrders(ibAccount).stream())
                .forEach(this::initHeartbeat);
    }

    public void updateHeartbeats(IbAccount ibAccount) {
        Map<IbOrder, Integer> hm = openOrderHeartbeatMap.get(ibAccount);
        new HashSet<>(hm.keySet()).forEach(ibOrder -> {
            Integer failedHeartbeatsLeft = hm.get(ibOrder);
            if (failedHeartbeatsLeft <= 0) {
                if (!HtrEnums.IbOrderStatus.UNKNOWN.equals(ibOrder.getStatus())) {
                    ibOrder.addEvent(HtrEnums.IbOrderStatus.UNKNOWN, null, null);
                    ibOrderDao.updateIbOrder(ibOrder);
                }
                hm.remove(ibOrder);
            } else {
                hm.put(ibOrder, failedHeartbeatsLeft - 1);
            }
        });
    }

    public void initHeartbeat(IbOrder ibOrder) {
        openOrderHeartbeatMap.get(ibOrder.getStrategy().getIbAccount()).put(ibOrder, HtrDefinitions.MAX_ORDER_HEARTBEAT_FAILS);
    }

    public void removeHeartbeat(IbOrder ibOrder) {
        openOrderHeartbeatMap.get(ibOrder.getStrategy().getIbAccount()).remove(ibOrder);
    }
}
