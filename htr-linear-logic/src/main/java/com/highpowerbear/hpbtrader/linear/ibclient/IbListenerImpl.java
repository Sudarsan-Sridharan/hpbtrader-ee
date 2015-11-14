package com.highpowerbear.hpbtrader.linear.ibclient;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Bar;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.ibclient.classifier.DefaultListener;
import com.highpowerbear.hpbtrader.linear.mktdata.MktDataController;
import com.highpowerbear.hpbtrader.linear.strategy.OrderStateHandler;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import com.ib.client.Contract;
import com.ib.client.OrderState;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
@DefaultListener
public class IbListenerImpl extends AbstractIbListener {
    @Inject private LinData linData;
    @Inject private DatabaseDao databaseDao;
    @Inject private IbController ibController;
    @Inject private MktDataController mktDataController;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private HeartbeatControl heartbeatControl;
    @Inject private EventBroker eventBroker;

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        
        if (!(  IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) ||
                IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status)))
        {
            return;
        }
        com.highpowerbear.hpbtrader.linear.entity.Order order = databaseDao.getOrderByIbPermId(permId);
        if (order == null) {
            return;
        }

        if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && LinEnums.OrderStatus.SUBMITTED.equals(order.getOrderStatus())) {
            heartbeatControl.heartbeatReceived(order);
            eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
        } else if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && !LinEnums.OrderStatus.SUBMITTED.equals(order.getOrderStatus())) {
            heartbeatControl.heartbeatReceived(order);
            orderStateHandler.orderSubmitted(order, LinUtil.getCalendar());
        } else if (IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) && !LinEnums.OrderStatus.CANCELED.equals(order.getOrderStatus())) {
            heartbeatControl.removeHeartbeat(order);
            orderStateHandler.orderCanceled(order, LinUtil.getCalendar());
        } else if (IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status) && remaining == 0 && !LinEnums.OrderStatus.FILLED.equals(order.getOrderStatus())) {
            heartbeatControl.removeHeartbeat(order);
            order.setFillPrice(avgFillPrice);
            orderStateHandler.orderFilled(order, LinUtil.getCalendar());
        }
    }

    @Override
    public void openOrder(int orderId, Contract contract, com.ib.client.Order order, OrderState orderState) {
        super.openOrder(orderId, contract, order, orderState);
        
        Order dbOrder = databaseDao.getOrderByIbOrderId(orderId);
        if (dbOrder != null && dbOrder.getIbPermId() == null) {
            dbOrder.setIbPermId(order.m_permId);
            databaseDao.updateOrder(dbOrder);
            eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);
        }
    }

    @Override
    public void nextValidId(int orderId) {
        super.nextValidId(orderId);
        ibController.setNextValidOrderId(orderId);
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        //super.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
        
        Series s = databaseDao.findSeries(reqId / LinSettings.IB_REQUEST_MULT);
        if (date.startsWith("finish")) {
            // remove last bar if it is not finished yet
            List<Bar> barsReceived = linData.getBarsReceivedMap().get(s.getId());
            int numBars = barsReceived.size();
            Bar lastBar = barsReceived.get(numBars - 1);
            if (lastBar.getTimeInMillisBarClose() > LinUtil.getCalendar().getTimeInMillis()) {
                barsReceived.remove(numBars - 1);
            }
            mktDataController.processBars(s);
            return;
        }
        Bar q = new Bar();
        Calendar c = LinUtil.getCalendar();
        c.setTimeInMillis(Long.valueOf(date) * 1000 + s.getInterval().getMillis()); // date-time stamp of the end of the bar
        if (LinEnums.Interval.INT_60_MIN.equals(s.getInterval())) {
            c.set(Calendar.MINUTE, 0); // needed in case of bars started at 9:30 (END 10:00 not 10:30) or 17:15 (END 18:00 not 18:15)
        }
        q.setqDateBarClose(c);
        q.setqOpen(open);
        q.setHigh(high);
        q.setLow(low);
        q.setqClose(close);
        q.setVolume(volume == -1 ? 0 : volume);
        q.setCount(count);
        q.setWap(WAP);
        q.setHasGaps(hasGaps);
        q.setSeries(s);
        linData.getBarsReceivedMap().get(s.getId()).add(q);
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        mktDataController.updateRealtimeData(tickerId, field, price);
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        mktDataController.updateRealtimeData(tickerId, field, size);
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        mktDataController.updateRealtimeData(tickerId, tickType, value);
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
    }
}
