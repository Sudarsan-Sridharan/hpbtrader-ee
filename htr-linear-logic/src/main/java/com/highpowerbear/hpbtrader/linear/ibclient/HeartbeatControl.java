package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.IbAccount;
import com.highpowerbear.hpbtrader.linear.entity.IbOrder;
import com.highpowerbear.hpbtrader.linear.persistence.LinDao;
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
    @Inject private LinDao linDao;

    public void init(IbAccount ibAccount) {
        linDao.getOpenIbOrders(ibAccount).forEach(this::addHeartbeat);
    }

    public void updateHeartbeats(IbAccount ibAccount) {
        Map<IbOrder, Integer> hm = linData.getOpenOrderHeartbeatMap().get(ibAccount);
        Set<IbOrder> keyset = new HashSet<>(hm.keySet());
        for (IbOrder ibOrder : keyset) {
            Integer failedHeartbeatsLeft = hm.get(ibOrder);
            if (failedHeartbeatsLeft <= 0) {
                if (!LinEnums.IbOrderStatus.UNKNOWN.equals(ibOrder.getStatus())) {
                    ibOrder.addEvent(LinEnums.IbOrderStatus.UNKNOWN, null, null);
                    linDao.updateIbOrder(ibOrder);
                }
                hm.remove(ibOrder);
            } else {
                hm.put(ibOrder, failedHeartbeatsLeft - 1);
            }
        }
    }

    public void heartbeatReceived(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = linData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.put(ibOrder, (failedHeartbeatsLeft < LinSettings.MAX_ORDER_HEARTBEAT_FAILS ? failedHeartbeatsLeft + 1 : failedHeartbeatsLeft));
        }
    }

    public void addHeartbeat(IbOrder ibOrder) {
        linData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount()).put(ibOrder, LinSettings.MAX_ORDER_HEARTBEAT_FAILS);
    }

    public void removeHeartbeat(IbOrder ibOrder) {
        Map<IbOrder, Integer> hm = linData.getOpenOrderHeartbeatMap().get(ibOrder.getIbAccount());
        Integer failedHeartbeatsLeft = hm.get(ibOrder);
        if (failedHeartbeatsLeft != null) {
            hm.remove(ibOrder);
        }
    }
}
