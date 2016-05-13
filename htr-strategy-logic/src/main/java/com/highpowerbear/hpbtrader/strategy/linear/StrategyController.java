package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.common.EmailSender;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.OperResult;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.strategy.linear.context.InMemoryCtx;
import com.highpowerbear.hpbtrader.strategy.linear.context.DatabaseCtx;
import com.highpowerbear.hpbtrader.strategy.linear.logic.LuxorStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.MacdCrossStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.TestStrategyLogic;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@ApplicationScoped
public class StrategyController implements Serializable {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private StrategyDao strategyDao;
    @Inject private DataSeriesDao dataSeriesDao;

    @Inject private OrderStateHandler orderStateHandler;
    @Inject private EmailSender emailSender;

    private Map<Strategy, StrategyLogic> strategyLogicMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            if (strategy.isActive()) {
                strategyLogicMap.put(strategy, createStrategyLogic(new DatabaseCtx(strategy)));
            } else {
                strategyLogicMap.remove(strategy);
            }
        }
    }

    public Map<Strategy, StrategyLogic> getStrategyLogicMap() {
        return strategyLogicMap;
    }

    private StrategyLogic createStrategyLogic(ProcessContext ctx) {
        StrategyLogic strategyLogic = null;
        if (HtrEnums.StrategyType.MACD_CROSS.equals(ctx.getStrategy().getStrategyType())) {
            strategyLogic = new MacdCrossStrategyLogic(ctx);

        } else if (HtrEnums.StrategyType.TEST.equals(ctx.getStrategy().getStrategyType())) {
            strategyLogic = new TestStrategyLogic(ctx);

        } else if (HtrEnums.StrategyType.LUXOR.equals(ctx.getStrategy().getStrategyType())) {
            strategyLogic = new LuxorStrategyLogic(ctx);
        }
        return strategyLogic;
    }

    public void processStrategy(ProcessContext ctx) {
        StrategyLogic sl = strategyLogicMap.get(ctx.getStrategy());
        String logMessage = " strategy, id=" + ctx.getStrategy().getId() + ", " + ctx.getStrategy().getDefaultInputSeriesAlias() + ", " + ctx.getStrategy().getStrategyType() + " --> " + sl.getClass().getSimpleName();

        l.info("BEGIN prepare " + logMessage);
        OperResult<Boolean, String> result = sl.prepare(0);
        l.info("END prepare " + logMessage + ", " + result.getContent());
        if (!result.getStatus()) {
            return;
        }

        l.info("BEGIN process " + logMessage);
        sl.process();
        l.info("END process " + logMessage + (sl.getIbOrder() == null ? ", no new order" : ", new order, trigger=" + sl.getIbOrder().getTriggerDesc()));

        if (sl.getIbOrder() == null) {
            if (sl.getActiveTrade() != null) {
                ctx.updateOrCreateTrade(sl.getActiveTrade(), sl.getLastDataBar().getbBarClose());
            }
            return;
        }

        l.info("BEGIN postProcess " + logMessage);
        ctx.createIbOrder(sl.getIbOrder());
        sl.getActiveTrade().addTradeOrder(sl.getIbOrder());
        ctx.updateOrCreateTrade(sl.getActiveTrade(), sl.getLastDataBar().getbBarClose());
        Trade activeTrade = ctx.getActiveTrade(); // fresh copy
        if (sl.getIbOrder().isClosingOrder()) {
            activeTrade.initClose();
            ctx.updateOrCreateTrade(activeTrade, sl.getLastDataBar().getbBarClose());
        }
        if (sl.getIbOrder().isReversalOrder()) {
            activeTrade = new Trade().initOpen(sl.getIbOrder(), activeTrade.getInitialStop(), activeTrade.getProfitTarget());
            activeTrade.addTradeOrder(sl.getIbOrder());
            ctx.updateOrCreateTrade(activeTrade, sl.getLastDataBar().getbBarClose());
        }
        ctx.getStrategy().setNumAllOrders(ctx.getStrategy().getNumAllOrders() + 1);
        ctx.updateStrategy();
        emailSender.sendEmail(sl.getIbOrder().getDescription(), sl.getIbOrder().getTriggerDesc() + "\n" + sl.getLastDataBar().print());

        if (HtrEnums.StrategyMode.IB.equals(ctx.getStrategy().getStrategyMode())) {
            //ibController.submitIbOrder(ibOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(ctx, sl.getIbOrder(), sl.getLastDataBar().getbBarClose());
        }
        l.info("END postProcess " + logMessage);
    }

    public ProcessContext backtestStrategy(Strategy strategy, Calendar startDate, Calendar endDate) {
        Strategy btStrategy = strategy.deepCopyTo(new Strategy()).resetStatistics();
        btStrategy.setStrategyMode(HtrEnums.StrategyMode.BTEST);
        ProcessContext ctx = new InMemoryCtx(btStrategy);
        StrategyLogic sl = createStrategyLogic(ctx);

        String logMessage = " strategy, id=" + strategy.getId() + ", " + strategy.getDefaultInputSeriesAlias() + ", " + strategy.getStrategyType() + " --> " + sl.getClass().getSimpleName();
        l.info("BEGIN backtestStrategy " + logMessage);

        DataSeries inputDataSeries = dataSeriesDao.getDataSeriesByAlias(ctx.getStrategy().getDefaultInputSeriesAlias());
        int numIterations = (int) ((endDate.getTimeInMillis() - startDate.getTimeInMillis()) / inputDataSeries.getInterval().getMillis());
        Calendar iterDate = Calendar.getInstance();
        iterDate.setTimeInMillis(startDate.getTimeInMillis());
        int i = 0;
        while (iterDate.before(endDate)) {
            ctx.setCurrentDate(iterDate);
            l.info("id=" + strategy.getId() + ", iter=" + i + "/" + numIterations + ", date=" + HtrDefinitions.DF.format(iterDate.getTime()));
            this.processStrategy(ctx);
        }
        l.info("END backtestStrategy " + logMessage);
        return ctx;
    }

    public void manualOrder(ProcessContext ctx, IbOrder ibOrder) {
        DataSeries inputDataSeries = dataSeriesDao.getDataSeriesByAlias(ctx.getStrategy().getDefaultInputSeriesAlias());
        DataBar dataBar = dataSeriesDao.getLastDataBars(inputDataSeries, 1, 0).get(0);
        String logMessage = " strategy, id=" + ctx.getStrategy().getId() + ", " + ctx.getStrategy().getDefaultInputSeriesAlias() + ", " + ctx.getStrategy().getStrategyType() + " --> " + "manual order";

        l.info("BEGIN manualOrder " + logMessage);
        ctx.createIbOrder(ibOrder);
        ctx.getActiveTrade().addTradeOrder(ibOrder);
        ctx.updateOrCreateTrade(ctx.getActiveTrade(), dataBar.getbBarClose());
        Trade activeTrade = ctx.getActiveTrade(); // fresh copy

        if (ibOrder.isClosingOrder()) {
            activeTrade.initClose();
            ctx.updateOrCreateTrade(activeTrade, dataBar.getbBarClose());
        }
        ctx.getStrategy().setNumAllOrders(ctx.getStrategy().getNumAllOrders() + 1);
        ctx.updateStrategy();
        emailSender.sendEmail(ibOrder.getDescription(), ibOrder.getTriggerDesc() + "\n" + dataBar.print());

        if (HtrEnums.StrategyMode.IB.equals(ctx.getStrategy().getStrategyMode())) {
            //ibController.submitIbOrder(manualIbOrder);
            // TODO
        } else {
            orderStateHandler.simulateFill(ctx, ibOrder, dataBar.getbBarClose());
        }
        l.info("END manualOrder " + logMessage + ", new order, trigger=" + ibOrder.getTriggerDesc());
    }
}
