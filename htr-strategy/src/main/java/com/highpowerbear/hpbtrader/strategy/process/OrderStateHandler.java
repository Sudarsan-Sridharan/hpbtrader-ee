package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;

import javax.enterprise.context.ApplicationScoped;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rkolar on 4/23/14.
 */
@ApplicationScoped
public class OrderStateHandler {

    public void simulateFill(ProcessContext ctx, IbOrder ibOrder, Double fillPrice) {
        Calendar t1 = HtrUtil.getCalendar();
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMIT_REQ, t1);
        Calendar t2 = HtrUtil.getCalendar();
        if (t2.getTimeInMillis() == t1.getTimeInMillis()) {
            t2.setTimeInMillis(t1.getTimeInMillis() + 1);
        }
        ibOrder.addEvent(HtrEnums.IbOrderStatus.SUBMITTED, t2);
        Calendar t3 = HtrUtil.getCalendar();
        if (t3.getTimeInMillis() == t2.getTimeInMillis()) {
            t3.setTimeInMillis(t2.getTimeInMillis() + 1);
        }
        ibOrder.setFillPrice(fillPrice);
        ibOrder.addEvent(HtrEnums.IbOrderStatus.FILLED, t3);
        ctx.updateIbOrder(ibOrder);
        orderFilled(ctx, ibOrder);
    }

    public void orderStateChanged(ProcessContext ctx, IbOrder ibOrder) {
        HtrEnums.IbOrderStatus status = ibOrder.getStatus();
        if (HtrEnums.IbOrderStatus.FILLED.equals(status)) {
            orderFilled(ctx, ibOrder);
        } else if (HtrEnums.IbOrderStatus.CANCELLED.equals(status)) {
            orderCanceled(ctx, ibOrder);
        } else if (HtrEnums.IbOrderStatus.UNKNOWN.equals(status)) {
            orderUnknown(ctx, ibOrder);
        }
    }

    private void orderFilled(ProcessContext ctx, IbOrder ibOrder) {
        Strategy str = ctx.getStrategy();
        List<Trade> trades = ctx.getTradesByOrder(ibOrder);
        Trade trade1 = trades.get(0);
        Trade trade2 = (ibOrder.isReversalOrder() ? trades.get(1) : null);

        if (ibOrder.isOpeningOrder()) {
            trade1.open(ibOrder.getFillPrice());
        } else {
            trade1.close(ibOrder.getEventDate(HtrEnums.IbOrderStatus.FILLED), ibOrder.getFillPrice());
            str.recalculateStats(trade1);
        }
        ctx.updateOrCreateTrade(trade1, ibOrder.getFillPrice());

        if (trade2 != null) {
            trade2.open(ibOrder.getFillPrice());
            ctx.updateOrCreateTrade(trade2, ibOrder.getFillPrice());
        }

        str.setNumFilledOrders(str.getNumFilledOrders() + 1);
        str.setCurrentPosition(ibOrder.isBuyOrder() ? str.getCurrentPosition() + ibOrder.getQuantity() : str.getCurrentPosition() - ibOrder.getQuantity());
        ctx.updateStrategy();
    }

    private void orderCanceled(ProcessContext ctx, IbOrder ibOrder) {
        List<Trade> trades = ctx.getTradesByOrder(ibOrder);
        trades.forEach(t -> {
            t.cncClose();
            ctx.updateOrCreateTrade(t, null);
        });
    }

    private void orderUnknown(ProcessContext ctx, IbOrder ibOrder) {
        List<Trade> trades = ctx.getTradesByOrder(ibOrder);
        trades.forEach(t -> {
            t.errClose();
            ctx.updateOrCreateTrade(t, null);
        });
    }
}