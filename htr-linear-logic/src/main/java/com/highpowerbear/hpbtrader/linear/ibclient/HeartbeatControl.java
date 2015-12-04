package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by rkolar on 5/9/14.
 */
@Named
@ApplicationScoped
public class HeartbeatControl {
    @Inject private LinData linData;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private IbAccountDao ibAccountDao;

    @PostConstruct
    public void init() {
        ibAccountDao.getIbAccounts().stream()
                .flatMap(ibAccount -> ibOrderDao.getOpenIbOrders(ibAccount).stream())
                .forEach(this::addHeartbeat);
    }

    public void updateHeartbeats(IbAccount ibAccount) {
        Map<IbOrder, Integer> hm = linData.getOpenOrderHeartbeatMap().get(ibAccount);
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

    public void heartbeatReceived(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = linData.getOpenOrderHeartbeatMap().get(ibOrder.getStrategy().getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.put(ibOrder, (failedHeartbeatsLeft < HtrSettings.MAX_ORDER_HEARTBEAT_FAILS ? failedHeartbeatsLeft + 1 : failedHeartbeatsLeft));
        }
    }

    public void addHeartbeat(IbOrder ibOrder) {
        linData.getOpenOrderHeartbeatMap().get(ibOrder.getStrategy().getIbAccount()).put(ibOrder, HtrSettings.MAX_ORDER_HEARTBEAT_FAILS);
    }

    public void removeHeartbeat(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = linData.getOpenOrderHeartbeatMap().get(ibOrder.getStrategy().getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.remove(ibOrder);
        }
    }
}
