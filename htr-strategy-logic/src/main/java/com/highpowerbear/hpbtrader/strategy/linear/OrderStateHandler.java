package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Trade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rkolar on 4/23/14.
 */
@Named
@ApplicationScoped
public class OrderStateHandler {

    public void simulateFill(ProcessContext ctx, IbOrder ibOrder, Double price) {
        Calendar t1 = HtrUtil.getCalendar();
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, t1, null);
        ctx.updateIbOrder(ibOrder);
        ibOrder = ctx.findIbOrder(ibOrder); // get a fresh copy from db
        Calendar t2 = HtrUtil.getCalendar();
        if (t2.getTimeInMillis() == t1.getTimeInMillis()) {
            t2.setTimeInMillis(t1.getTimeInMillis() + 1);
        }
        orderSubmitted(ctx, ibOrder, t2);
        ibOrder = ctx.findIbOrder(ibOrder); // get a fresh copy from db
        Calendar t3 = HtrUtil.getCalendar();
        if (t3.getTimeInMillis() == t2.getTimeInMillis()) {
            t3.setTimeInMillis(t2.getTimeInMillis() + 1);
        }
        orderFilled(ctx, ibOrder, t3, price);
    }

    public void orderSubmitted(ProcessContext ctx, IbOrder ibOrder, Calendar cal) {
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMITTED, cal, null);
        ctx.updateIbOrder(ibOrder);
    }

    public void orderFilled(ProcessContext ctx, IbOrder ibOrder, Calendar cal, Double fillPrice) {
        ibOrder.addEvent(ibOrder.getStatus(), cal, fillPrice);
        ctx.updateIbOrder(ibOrder);

        List<Trade> trades = ctx.getTradesByOrder(ibOrder);
        Trade trade1 = trades.get(0);
        Trade trade2 = (ibOrder.isReversalOrder() ? trades.get(1) : null);

        if (ibOrder.isOpeningOrder()) {
            trade1.open(ibOrder.getFillPrice());
        } else {
            trade1.close(ibOrder.getEventDate(HtrEnums.IbOrderStatus.FILLED), ibOrder.getFillPrice());
            ctx.getStrategy().recalculateStats(trade1);
        }
        ctx.updateOrCreateTrade(trade1, ibOrder.getFillPrice());

        if (trade2 != null) {
            trade2.open(ibOrder.getFillPrice());
            ctx.updateOrCreateTrade(trade2, ibOrder.getFillPrice());
        }

        ctx.getStrategy().setNumFilledOrders(ctx.getStrategy().getNumFilledOrders() + 1);
        ctx.getStrategy().setCurrentPosition(ibOrder.isBuyOrder() ? ctx.getStrategy().getCurrentPosition() + ibOrder.getQuantity() : ctx.getStrategy().getCurrentPosition() - ibOrder.getQuantity());
        ctx.updateStrategy();
    }

    public void orderCanceled(ProcessContext ctx, IbOrder ibOrder, Calendar cal) {
        ibOrder.addEvent(HtrEnums.IbOrderStatus.CANCELLED, cal, null);
        ctx.updateIbOrder(ibOrder);

        List<Trade> trades = ctx.getTradesByOrder(ibOrder);
        trades.forEach(t -> {
            t.cncClosed();
            ctx.updateOrCreateTrade(t, null);
        });
    }
}