package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by rkolar on 5/9/14.
 */
@Named
@ApplicationScoped
public class HeartbeatControl {
    @Inject private LinData linData;
    @Inject private DatabaseDao databaseDao;
    @Inject private EventBroker eventBroker;

    public void init() {
        List<Order> openOrders = databaseDao.getIbOpenOrders();
        for (Order o : openOrders) {
            addHeartbeat(o);
        }
    }

    public void updateHeartbeats() {
        for (Long dbId : linData.getOpenOrderHeartbeatMap().keySet()) {
            Integer failedHeartbeatsLeft = linData.getOpenOrderHeartbeatMap().get(dbId);
            if (failedHeartbeatsLeft <= 0) {
                Order order = databaseDao.findOrder(dbId);
                if (!LinEnums.OrderStatus.UNKNOWN.equals(order.getOrderStatus())) {
                    order.addEvent(LinEnums.OrderStatus.UNKNOWN, LinUtil.getCalendar());
                    List<Trade> trades = databaseDao.getTradesByOrder(order);
                    for (Trade t : trades) {
                        t.errClose();
                        databaseDao.updateTrade(t, null);
                    }
                    databaseDao.updateOrder(order);
                }
                linData.getOpenOrderHeartbeatMap().remove(dbId);
            } else {
                linData.getOpenOrderHeartbeatMap().put(dbId, failedHeartbeatsLeft - 1);
            }
            eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
        }
    }

    public void heartbeatReceived(Order o) {
        Integer failedHeartbeatsLeft = linData.getOpenOrderHeartbeatMap().get(o.getId());
        if (failedHeartbeatsLeft != null) {
            linData.getOpenOrderHeartbeatMap().put(o.getId(), (failedHeartbeatsLeft < LinSettings.MAX_ORDER_HEARTBEAT_FAILS ? failedHeartbeatsLeft + 1 : failedHeartbeatsLeft));
        }
    }

    public void addHeartbeat(Order o) {
        linData.getOpenOrderHeartbeatMap().put(o.getId(), LinSettings.MAX_ORDER_HEARTBEAT_FAILS);
    }

    public void removeHeartbeat(Order o) {
        Integer failedHeartbeatsLeft = linData.getOpenOrderHeartbeatMap().get(o.getId());
        if (failedHeartbeatsLeft != null) {
            linData.getOpenOrderHeartbeatMap().remove(o.getId());
        }
    }
}
