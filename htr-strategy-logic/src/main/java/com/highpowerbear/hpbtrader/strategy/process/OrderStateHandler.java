package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.strategy.common.EventBroker;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;

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
    @Inject private IbOrderDao ibOrderDao;
    @Inject private TradeDao tradeDao;
    @Inject private StrategyDao strategyDao;
    @Inject private EventBroker eventBroker;

    public void simulateFill(IbOrder ibOrder, DataBar dataBar) {
        Calendar t1 = HtrUtil.getCalendar();
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, t1, null);
        ibOrderDao.updateIbOrder(ibOrder);
        Long dbId = ibOrder.getId();

        // get a fresh copy from db
        ibOrder = ibOrderDao.findIbOrder(dbId);
        Calendar t2 = HtrUtil.getCalendar();
        if (t2.getTimeInMillis() <= t1.getTimeInMillis()) {
            t2.setTimeInMillis(t1.getTimeInMillis() + 1);
        }
        orderSubmitted(ibOrder, t2);

        // get a fresh copy from db
        ibOrder = ibOrderDao.findIbOrder(dbId);
        Calendar t3 = HtrUtil.getCalendar();
        if (t3.getTimeInMillis() <= t2.getTimeInMillis()) {
            t3.setTimeInMillis(t2.getTimeInMillis() + 1);
        }
        orderFilled(ibOrder, t3, dataBar.getbBarClose());
    }

    public void orderSubmitted(IbOrder ibOrder, Calendar cal) {
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMITTED, cal, null);
        ibOrderDao.updateIbOrder(ibOrder);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
    }

    public void orderFilled(IbOrder ibOrder, Calendar cal, Double fillPrice) {
        ibOrder.addEvent(ibOrder.getStatus(), cal, fillPrice);
        ibOrderDao.updateIbOrder(ibOrder);
        Strategy strategy = ibOrder.getStrategy();

        List<Trade> trades = tradeDao.getTradesByOrder(ibOrder);
        Trade trade1 = trades.get(0);
        Trade trade2 = (ibOrder.isReversalOrder() ? trades.get(1) : null);

        if (ibOrder.isOpeningOrder()) {
            trade1.open(ibOrder.getFillPrice());
        } else {
            trade1.close(ibOrder.getEventDate(HtrEnums.IbOrderStatus.FILLED), ibOrder.getFillPrice());
            strategy.recalculateStats(trade1);
        }
        tradeDao.updateOrCreateTrade(trade1, ibOrder.getFillPrice());

        if (trade2 != null) {
            trade2.open(ibOrder.getFillPrice());
            tradeDao.updateOrCreateTrade(trade2, ibOrder.getFillPrice());
        }

        strategy.setNumFilledOrders(strategy.getNumFilledOrders() + 1);
        strategy.setCurrentPosition(ibOrder.isBuyOrder() ? strategy.getCurrentPosition() + ibOrder.getQuantity() : strategy.getCurrentPosition() - ibOrder.getQuantity());
        strategyDao.updateStrategy(strategy);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
    }

    public void orderCanceled(IbOrder ibOrder, Calendar cal) {
        ibOrder.addEvent(HtrEnums.IbOrderStatus.CANCELLED, cal, null);
        ibOrderDao.updateIbOrder(ibOrder);

        List<Trade> trades = tradeDao.getTradesByOrder(ibOrder);
        trades.forEach(t -> {
            t.cncClosed();
            tradeDao.updateOrCreateTrade(t, null);
        });
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);
    }
}