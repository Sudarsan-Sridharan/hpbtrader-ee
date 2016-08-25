package com.highpowerbear.hpbtrader.exec.ibclient;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by rkolar on 5/9/14.
 */
@ApplicationScoped
public class HeartbeatControl {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private IbOrderDao ibOrderDao;
    @Inject private IbAccountDao ibAccountDao;

    private Map<IbAccount, Map<IbOrder, Integer>> openOrderHeartbeatMap = new HashMap<>(); // ibAccount --> (ibOrder --> number of failed heartbeats left before UNKNOWN)

    public Map<IbAccount, Map<IbOrder, Integer>> getOpenOrderHeartbeatMap() {
        return openOrderHeartbeatMap;
    }

    private void init(@Observes @Initialized(ApplicationScoped.class) Object evt) { // mechanism for cdi eager initialization without using singleton ejb
        l.info("BEGIN HeartbeatControl.init");
        ibAccountDao.getIbAccounts().forEach(ibAccount -> openOrderHeartbeatMap.put(ibAccount, new ConcurrentHashMap<>()));
        ibAccountDao.getIbAccounts().stream()
                .flatMap(ibAccount -> ibOrderDao.getOpenIbOrders(ibAccount).stream())
                .forEach(this::initHeartbeat);
        l.info("END HeartbeatControl.init");
    }

    public void updateHeartbeats(IbAccount ibAccount) {
        Map<IbOrder, Integer> hm = openOrderHeartbeatMap.get(ibAccount);
        new HashSet<>(hm.keySet()).forEach(ibOrder -> {
            Integer failedHeartbeatsLeft = hm.get(ibOrder);
            if (failedHeartbeatsLeft <= 0) {
                if (!HtrEnums.IbOrderStatus.UNKNOWN.equals(ibOrder.getStatus())) {
                    ibOrder.addEvent(HtrEnums.IbOrderStatus.UNKNOWN, HtrUtil.getCalendar());
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
