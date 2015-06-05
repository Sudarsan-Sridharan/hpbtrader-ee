package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.persistence.DatabaseDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rkolar on 4/23/14.
 */
@Named
@ApplicationScoped
public class OrderStateHandler {
    @Inject private DatabaseDao databaseDao;
    @Inject private EventBroker eventBroker;

    public void simulateFill(Order order, Quote quote) {
        Calendar t1 = LinUtil.getCalendar();
        order.addEvent(LinEnums.OrderStatus.SUBMIT_REQ, t1);
        databaseDao.updateOrder(order);
        Long dbId = order.getId();

        // get a fresh copy from db
        order = databaseDao.findOrder(dbId);
        Calendar t2 = LinUtil.getCalendar();
        if (t2.getTimeInMillis() <= t1.getTimeInMillis()) {
            t2.setTimeInMillis(t1.getTimeInMillis() + 1);
        }
        orderSubmitted(order, t2);

        // get a fresh copy from db
        order = databaseDao.findOrder(dbId);
        Calendar t3 = LinUtil.getCalendar();
        if (t3.getTimeInMillis() <= t2.getTimeInMillis()) {
            t3.setTimeInMillis(t2.getTimeInMillis() + 1);
        }
        order.setFillPrice(quote.getqClose());
        orderFilled(order, t3);
    }

    public void orderSubmitted(Order order, Calendar cal) {
        order.addEvent(LinEnums.OrderStatus.SUBMITTED, cal);
        databaseDao.updateOrder(order);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
    }

    public void orderFilled(Order order, Calendar cal) {
        order.addEvent(LinEnums.OrderStatus.FILLED, cal);
        databaseDao.updateOrder(order);

        Strategy strategy = order.getStrategy();

        List<Trade> trades = databaseDao.getTradesByOrder(order);
        Trade trade1 = trades.get(0);
        Trade trade2 = (order.isReversalOrder() ? trades.get(1) : null);

        if (order.isOpeningOrder()) {
            trade1.open(order.getFillPrice());
        } else {
            trade1.close(order.getEventDate(LinEnums.OrderStatus.FILLED), order.getFillPrice());
            strategy.recalculateStats(trade1);
        }
        databaseDao.updateTrade(trade1, order.getFillPrice());

        if (trade2 != null) {
            trade2.open(order.getFillPrice());
            databaseDao.updateTrade(trade2, order.getFillPrice());
        }

        strategy.setNumFilledOrders(strategy.getNumFilledOrders() + 1);
        strategy.setCurrentPosition(order.isBuyOrder() ? strategy.getCurrentPosition() + order.getQuantity() : strategy.getCurrentPosition() - order.getQuantity());
        databaseDao.updateStrategy(strategy);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
    }

    public void orderCanceled(Order order, Calendar cal) {
        order.addEvent(LinEnums.OrderStatus.CANCELED, cal);
        databaseDao.updateOrder(order);

        List<Trade> trades = databaseDao.getTradesByOrder(order);
        for (Trade t : trades) {
            t.cncClosed();
            databaseDao.updateTrade(t, null);
        }
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
    }
}