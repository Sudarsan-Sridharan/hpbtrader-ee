package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.strategy.common.EventBroker;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogicContext;
import com.highpowerbear.hpbtrader.shared.common.EmailSender;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
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
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);
    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private StrategyDao strategyDao;
    @Inject private OrderStateHandler orderStateHandler;
    @Inject private EmailSender emailSender;
    @Inject private EventBroker eventBroker;

    private static final int INDICATORS_LIST_SIZE = 10;

    void process(Strategy strategy, StrategyLogic strategyLogic) {
        DataSeries dataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        String logMessage = "processing " + dataSeries.getInstrument().getSymbol() + ", " + dataSeries.getInstrument().getCurrency() + ", " + dataSeries.getInterval().name() + ", " +  strategy.getStrategyType() + " --> " + strategyLogic.getName();
        
        l.info("START " + logMessage);
        StrategyLogicContext ctx = new StrategyLogicContext();
        ctx.strategy = strategy;
        ctx.activeTrade = tradeDao.getActiveTrade(strategy);
        ctx.dataBars = dataSeriesDao.getBars(dataSeries, HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE);
        if (ctx.dataBars.size() < HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END " + logMessage + ", not enough  bars available");
            return;
        }
        DataBar dataBar = ctx.dataBars.get(ctx.dataBars.size() - 1);
        
        long intervalMillis = dataSeries.getInterval().getMillis();
        long nowMillis = HtrUtil.getCalendar().getTimeInMillis();
        boolean isCurrentBar = ((dataBar.getbCloseDateMillis() + intervalMillis) > nowMillis);
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
                tradeDao.updateOrCreateTrade(ctx.activeTrade, dataBar.getbClose());
            }
            l.info("END " + logMessage + ", no new order");
            return;
        }
        
        ibOrderDao.createIbOrder(ibOrder);
        ctx.activeTrade.addTradeOrder(ibOrder);
        tradeDao.updateOrCreateTrade(ctx.activeTrade, dataBar.getbClose());

        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        ctx.activeTrade = tradeDao.getActiveTrade(strategy);
        
        if (ibOrder.isClosingOrder()) {
            ctx.activeTrade.initClose();
            tradeDao.updateOrCreateTrade(ctx.activeTrade, dataBar.getbClose());
        }
        if (ibOrder.isReversalOrder()) {
            ctx.activeTrade = new Trade().initOpen(ibOrder);
            strategyLogic.setInitialStopAndTarget();
            ctx.activeTrade.addTradeOrder(ibOrder);
            tradeDao.updateOrCreateTrade(ctx.activeTrade, dataBar.getbClose());
        }
        
        ctx.strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        strategyDao.updateStrategy(strategy);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);

        // send email, notifying about new order
        String subject = ibOrder.getDescription();
        String content = ibOrder.getTriggerDesc() + "\n" + dataBar.print();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + ibOrder.getTriggerDesc());
        
        if (HtrEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            //ibController.submitIbOrder(ibOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(ibOrder, dataBar);
        }
    }

    void processManual(IbOrder manualIbOrder, Trade activeTrade, DataBar dataBar) {
        Strategy strategy = manualIbOrder.getStrategy();
        DataSeries dataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        String logMessage = "processing " + dataSeries.getInstrument().getSymbol() + ", " + dataSeries.getInstrument().getCurrency() + ", " + dataSeries.getInterval().name() + ", " +  strategy.getStrategyType() + " --> manual order";

        l.info("START " + logMessage);
        ibOrderDao.createIbOrder(manualIbOrder);
        activeTrade.addTradeOrder(manualIbOrder);
        tradeDao.updateOrCreateTrade(activeTrade, dataBar.getbClose());
        // needed to get fresh copy of trade and trade orders with set ids to prevent trade order duplication in the next update
        activeTrade = tradeDao.getActiveTrade(strategy);

        if (manualIbOrder.isClosingOrder()) {
            activeTrade.initClose();
            tradeDao.updateOrCreateTrade(activeTrade, dataBar.getbClose());
        }
        strategy.setNumAllOrders(strategy.getNumAllOrders() + 1);
        strategyDao.updateStrategy(strategy);
        eventBroker.trigger(HtrEnums.DataChangeEvent.STRATEGY_UPDATE);

        // send email, notifying about new order
        String subject = manualIbOrder.getDescription();
        String content = manualIbOrder.getTriggerDesc() + "\n" + dataBar.print();
        emailSender.sendEmail(subject, content);

        l.info("END " + logMessage + ", new order, trigger=" + manualIbOrder.getTriggerDesc());

        if (HtrEnums.StrategyMode.IB.equals(strategy.getStrategyMode())) {
            //ibController.submitIbOrder(manualIbOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(manualIbOrder, dataBar);
        }
    }
}