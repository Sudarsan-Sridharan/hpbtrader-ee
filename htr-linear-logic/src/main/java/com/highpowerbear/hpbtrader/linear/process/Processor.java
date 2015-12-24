package com.highpowerbear.hpbtrader.linear.process;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinSettings;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogic;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogicContext;
import com.highpowerbear.hpbtrader.shared.common.EmailSender;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;
import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;

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
    @Inject private BarDao barDao;
    @Inject private TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private StrategyDao strategyDao;
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
        ctx.activeTrade = tradeDao.getActiveTrade(strategy);
        ctx.bars = barDao.getBars(series, HtrSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE);
        if (ctx.bars.size() < HtrSettings.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END " + logMessage + ", not enough  bars available");
            return;
        }
        Bar bar = ctx.bars.get(ctx.bars.size() - 1);
        
        long intervalMillis = series.getInterval().getMillis();
        long nowMillis = HtrUtil.getCalendar().getTimeInMillis();
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
        IbOrder ibOrder = strategyLogic.processSignals();
        
        if (ibOrder == null) {
            if (ctx.activeTrade != null) {
                tradeDao.updateOrCreateTrade(ctx.activeTrade, bar.getqClose());
            }
            l.info("END " + logMessage + ", no new order");
            return;
        }
        
        ibOrderDao.createIbOrder(ibOrder);
        ctx.activeTrade.addTradeOrder(ibOrder);
        tradeDao.updateOrCreateTrade(ctx.activeTrade, bar.getqClose());

        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        ctx.activeTrade = tradeDao.getActiveTrade(strategy);
        
        if (ibOrder.isClosingOrder()) {
            ctx.activeTrade.initClose();
            tradeDao.updateOrCreateTrade(ctx.activeTrade, bar.getqClose());
        }
        if (ibOrder.isReversalOrder()) {
            ctx.activeTrade = new Trade().initOpen(ibOrder);
            strategyLogic.setInitialStopAndTarget();
            ctx.activeTrade.addTradeOrder(ibOrder);
            tradeDao.updateOrCreateTrade(ctx.activeTrade, bar.getqClose());
        }
        
        ctx.strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        strategyDao.updateStrategy(strategy);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);

        // send email, notifying about new order
        String subject = ibOrder.getDescription();
        String content = ibOrder.getTriggerDesc() + "\n" + bar.print();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + ibOrder.getTriggerDesc());
        
        if (HtrEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            //ibController.submitIbOrder(ibOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(ibOrder, bar);
        }
    }

    void processManual(IbOrder manualIbOrder, Trade activeTrade, Bar bar) {
        Strategy strategy = manualIbOrder.getStrategy();
        Series series = strategy.getSeries();
        String logMessage = "processing " + series.getSymbol() + ", " + series.getCurrency() + ", " + series.getInterval().getDisplayName() + ", " +  strategy.getStrategyType() + " --> manual order";

        l.info("START " + logMessage);
        ibOrderDao.createIbOrder(manualIbOrder);
        activeTrade.addTradeOrder(manualIbOrder);
        tradeDao.updateOrCreateTrade(activeTrade, bar.getqClose());
        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        activeTrade = tradeDao.getActiveTrade(strategy);

        if (manualIbOrder.isClosingOrder()) {
            activeTrade.initClose();
            tradeDao.updateOrCreateTrade(activeTrade, bar.getqClose());
        }
        strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        strategyDao.updateStrategy(strategy);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);

        // send email, notifying about new order
        String subject = manualIbOrder.getDescription();
        String content = manualIbOrder.getTriggerDesc() + "\n" + bar.print();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + manualIbOrder.getTriggerDesc());

        if (HtrEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            //ibController.submitIbOrder(manualIbOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(manualIbOrder, bar);
        }
    }
}