package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.IbOrder;
import com.highpowerbear.hpbtrader.linear.entity.Bar;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.persistence.LinDao;
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
    @Inject private LinDao linDao;
    @Inject private EventBroker eventBroker;

    public void simulateFill(IbOrder ibOrder, Bar bar) {
        Calendar t1 = LinUtil.getCalendar();
        ibOrder.addEvent(LinEnums.IbOrderStatus.SUBMIT_REQ, t1, null);
        linDao.updateIbOrder(ibOrder);
        Long dbId = ibOrder.getId();

        // get a fresh copy from db
        ibOrder = linDao.findIbOrder(dbId);
        Calendar t2 = LinUtil.getCalendar();
        if (t2.getTimeInMillis() <= t1.getTimeInMillis()) {
            t2.setTimeInMillis(t1.getTimeInMillis() + 1);
        }
        orderSubmitted(ibOrder, t2);

        // get a fresh copy from db
        ibOrder = linDao.findIbOrder(dbId);
        Calendar t3 = LinUtil.getCalendar();
        if (t3.getTimeInMillis() <= t2.getTimeInMillis()) {
            t3.setTimeInMillis(t2.getTimeInMillis() + 1);
        }
        orderFilled(ibOrder, t3, bar.getqClose());
    }

    public void orderSubmitted(IbOrder ibOrder, Calendar cal) {
        ibOrder.addEvent(LinEnums.IbOrderStatus.SUBMITTED, cal, null);
        linDao.updateIbOrder(ibOrder);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
    }

    public void orderFilled(IbOrder ibOrder, Calendar cal, Double fillPrice) {
        ibOrder.addEvent(ibOrder.getStatus(), cal, fillPrice);
        linDao.updateIbOrder(ibOrder);
        Strategy strategy = ibOrder.getStrategy();

        List<Trade> trades = linDao.getTradesByOrder(ibOrder);
        Trade trade1 = trades.get(0);
        Trade trade2 = (ibOrder.isReversalOrder() ? trades.get(1) : null);

        if (ibOrder.isOpeningOrder()) {
            trade1.open(ibOrder.getFillPrice());
        } else {
            trade1.close(ibOrder.getEventDate(LinEnums.IbOrderStatus.FILLED), ibOrder.getFillPrice());
            strategy.recalculateStats(trade1);
        }
        linDao.updateTrade(trade1, ibOrder.getFillPrice());

        if (trade2 != null) {
            trade2.open(ibOrder.getFillPrice());
            linDao.updateTrade(trade2, ibOrder.getFillPrice());
        }

        strategy.setNumFilledOrders(strategy.getNumFilledOrders() + 1);
        strategy.setCurrentPosition(ibOrder.isBuyOrder() ? strategy.getCurrentPosition() + ibOrder.getQuantity() : strategy.getCurrentPosition() - ibOrder.getQuantity());
        linDao.updateStrategy(strategy);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
    }

    public void orderCanceled(IbOrder ibOrder, Calendar cal) {
        ibOrder.addEvent(LinEnums.IbOrderStatus.CANCELED, cal, null);
        linDao.updateIbOrder(ibOrder);

        List<Trade> trades = linDao.getTradesByOrder(ibOrder);
        for (Trade t : trades) {
            t.cncClosed();
            linDao.updateTrade(t, null);
        }
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
    }
}