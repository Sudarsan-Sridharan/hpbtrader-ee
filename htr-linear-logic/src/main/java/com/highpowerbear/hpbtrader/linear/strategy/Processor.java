package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.common.EmailSender;
import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.*;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.strategy.model.StrategyLogicContext;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
public class Processor implements Serializable {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private DatabaseDao databaseDao;
    @Inject private IbController ibController;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private EmailSender emailSender;
    @Inject private EventBroker eventBroker;

    private static final int INDICATORS_LIST_SIZE = 10;

    void process(Strategy strategy, StrategyLogic strategyLogic) {
        Series series = strategy.getSeries();
        String logMessage = "processing " + series.getSymbol() + ", " + series.getCurrency() + ", " + series.getInterval().getDisplayName() + ", " +  strategy.getStrategyType() + " --> " + strategyLogic.getName();
        
        l.info("START " + logMessage);
        StrategyLogicContext ctx = new StrategyLogicContext();
        ctx.strategy = strategy;
        ctx.activeTrade = databaseDao.getActiveTrade(strategy);
        ctx.bars = databaseDao.getBars(series.getId(), LinSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE);
        if (ctx.bars.size() < LinSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END " + logMessage + ", not enough  bars available");
            return;
        }
        Bar bar = ctx.bars.get(ctx.bars.size() - 1);
        
        long intervalMillis = series.getInterval().getMillis();
        long nowMillis = LinUtil.getCalendar().getTimeInMillis();
        boolean isCurrentBar = ((bar.getTimeInMillisBarClose() + intervalMillis) > nowMillis);
        if (!isCurrentBar) {
            l.info("END " + logMessage + ", not current bar");
            return;
        }
        
        if (ctx.activeTrade != null && ctx.activeTrade.isInit()) {
            l.info("END " + logMessage + ", active trade in state " + ctx.activeTrade.getTradeStatus());
            return;
        }
        
        strategyLogic.updateContext(ctx);
        Order order = strategyLogic.processSignals();
        
        if (order == null) {
            if (ctx.activeTrade != null) {
                databaseDao.updateTrade(ctx.activeTrade, bar.getqClose());
            }
            l.info("END " + logMessage + ", no new order");
            return;
        }
        
        databaseDao.addOrder(order);
        ctx.activeTrade.addTradeOrder(order);
        databaseDao.updateTrade(ctx.activeTrade, bar.getqClose());
        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        ctx.activeTrade = databaseDao.getActiveTrade(strategy);
        
        if (order.isClosingOrder()) {
            ctx.activeTrade.initClose();
            databaseDao.updateTrade(ctx.activeTrade, bar.getqClose());
        }
        if (order.isReversalOrder()) {
            ctx.activeTrade = new Trade().initOpen(order);
            strategyLogic.setInitialStopAndTarget();
            ctx.activeTrade.addTradeOrder(order);
            databaseDao.updateTrade(ctx.activeTrade, bar.getqClose());
        }
        
        ctx.strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        databaseDao.updateStrategy(strategy);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);

        // send email, notifying about new order
        String subject = order.getDescription();
        String content = order.getTriggerDesc() + "\n" + bar.printValues();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + order.getTriggerDesc());
        
        if (LinEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            ibController.submitIbOrder(order);
        } else {
            orderStateHandler.simulateFill(order, bar);
        }
    }

    void processManual(Order manualOrder, Trade activeTrade, Bar bar) {
        Strategy strategy = manualOrder.getStrategy();
        Series series = strategy.getSeries();
        String logMessage = "processing " + series.getSymbol() + ", " + series.getCurrency() + ", " + series.getInterval().getDisplayName() + ", " +  strategy.getStrategyType() + " --> manual order";

        l.info("START " + logMessage);
        databaseDao.addOrder(manualOrder);
        activeTrade.addTradeOrder(manualOrder);
        databaseDao.updateTrade(activeTrade, bar.getqClose());
        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        activeTrade = databaseDao.getActiveTrade(strategy);

        if (manualOrder.isClosingOrder()) {
            activeTrade.initClose();
            databaseDao.updateTrade(activeTrade, bar.getqClose());
        }
        strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        databaseDao.updateStrategy(strategy);
        eventBroker.trigger(LinEnums.DataChangeEvent.STRATEGY_UPDATE);

        // send email, notifying about new order
        String subject = manualOrder.getDescription();
        String content = manualOrder.getTriggerDesc() + "\n" + bar.printValues();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + manualOrder.getTriggerDesc());

        if (LinEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            ibController.submitIbOrder(manualOrder);
        } else {
            orderStateHandler.simulateFill(manualOrder, bar);
        }
    }
}