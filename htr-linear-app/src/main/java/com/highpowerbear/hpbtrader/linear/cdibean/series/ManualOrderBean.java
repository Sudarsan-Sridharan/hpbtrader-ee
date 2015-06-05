package com.highpowerbear.hpbtrader.linear.cdibean.series;

import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@SessionScoped
public class ManualOrderBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private SeriesBean seriesBean;
    @Inject private StrategyController strategyController;
    @Inject private DatabaseDao databaseDao;
    
    private List<LinEnums.OrderAction> orderActions;
    private LinEnums.OrderAction orderAction;
    private Double initialStopPct;
    private Double targetPct;
    private Order order;
    private Trade activeTrade;
    private Quote quote;

    public void init() {
        l.fine("ManualOrderBean init");
        orderActions = new ArrayList<>();
        initialStopPct = 1.3d;
        targetPct = 2.2d;
        createOrder();
        activeTrade = databaseDao.getActiveTrade(seriesBean.getSelectedSeriesRecord().getSeries().getActiveStrategy());
        if (activeTrade == null) {
            orderActions.add(LinEnums.OrderAction.BTO);
            orderActions.add(LinEnums.OrderAction.STO);
        } else if (activeTrade.isLong()) {
            orderActions.add(LinEnums.OrderAction.STC);
        } else {
             orderActions.add(LinEnums.OrderAction.BTC);
        }
        orderAction = orderActions.get(0);
        quote = databaseDao.getLastQuote(seriesBean.getSelectedSeriesRecord().getSeries());
    }
    
    private void createOrder() {
        order = new Order();
        Strategy strategy = seriesBean.getSelectedSeriesRecord().getSeries().getActiveStrategy();
        order.setStrategy(strategy);
        order.setStrategyMode(strategy.getStrategyMode());
        order.setSubmitType(LinEnums.SubmitType.MANUAL);
        order.setQuantity(strategy.getTradingQuantity());
        order.setOrderType(LinEnums.OrderType.MKT);
        order.setLimitPrice(null); // N/A for market order
        order.setStopPrice(null); // N/A for market order
        order.addEvent(LinEnums.OrderStatus.NEW, LinUtil.getCalendar());
    }

    public Order getOrder() {
        return order;
    }
    
    public List<LinEnums.OrderAction> getOrderActions() {
        return orderActions;
    }

    public LinEnums.OrderAction getOrderAction() {
        return orderAction;
    }

    public void setOrderAction(LinEnums.OrderAction orderAction) {
        this.orderAction = orderAction;
    }

    public Double getInitialStopPct() {
        return initialStopPct;
    }

    public void setInitialStopPct(Double initialStopPct) {
        this.initialStopPct = initialStopPct;
    }

    public Double getTargetPct() {
        return targetPct;
    }

    public void setTargetPct(Double targetPct) {
        this.targetPct = targetPct;
    }
    
    public boolean getStopTargetRendered() {
        return (LinEnums.OrderAction.BTO.equals(orderAction) || LinEnums.OrderAction.STO.equals(orderAction));
    }
    
    public void submitOrder() {
        order.setOrderAction(this.orderAction);
        order.setTriggerDesc("man: Manual Order");
        if (activeTrade == null) {
            activeTrade = new Trade().initOpen(order);
            Double initialStop = (activeTrade.isLong() ? LinUtil.round5(quote.getqClose() - (initialStopPct / 100.0) * quote.getqClose()) : LinUtil.round5(quote.getqClose() + (initialStopPct / 100.0) * quote.getqClose()));
            activeTrade.setStopLoss(initialStop);
            activeTrade.setInitialStop(initialStop);
            activeTrade.setProfitTarget(activeTrade.isLong() ? LinUtil.round5(quote.getqClose() + (targetPct / 100.0) * quote.getqClose()) : LinUtil.round5(quote.getqClose() - (targetPct / 100.0) * quote.getqClose()));
        }
        strategyController.processManual(order, activeTrade, quote);
    }
}