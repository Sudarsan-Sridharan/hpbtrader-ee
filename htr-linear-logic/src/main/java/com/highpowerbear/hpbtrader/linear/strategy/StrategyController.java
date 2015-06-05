package com.highpowerbear.hpbtrader.linear.strategy;

import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.*;
import com.highpowerbear.hpbtrader.linear.strategy.logic.LuxorStrategyLogic;
import com.highpowerbear.hpbtrader.linear.strategy.logic.MacdCrossStrategyLogic;
import com.highpowerbear.hpbtrader.linear.strategy.logic.TestStrategyLogic;
import com.highpowerbear.hpbtrader.linear.strategy.model.BacktestResult;
import com.highpowerbear.hpbtrader.persistence.DatabaseDao;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@ApplicationScoped
public class StrategyController implements Serializable {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private DatabaseDao databaseDao;
    @Inject private LinData linData;
    @Inject private Processor processor;
    @Inject private Backtester backtester;

    public void init() {
        for (Series s : databaseDao.getAllSeries(false)) {
            swapStrategyLogic(s);
        }
    }

    public void swapStrategyLogic(Series series) {
        if (series.getIsEnabled()) {
            Strategy activeStrategy = databaseDao.getActiveStrategy(series);
            linData.getStrategyLogicMap().put(series.getId(), createStrategyLogic(activeStrategy));
        } else {
            linData.getStrategyLogicMap().remove(series.getId());
        }
    }

    private StrategyLogic createStrategyLogic(Strategy strategy) {
        StrategyLogic strategyLogic = null;
        if (LinEnums.StrategyType.MACD_CROSS.equals(strategy.getStrategyType())) {
            strategyLogic = new MacdCrossStrategyLogic();
        } else if (LinEnums.StrategyType.TEST.equals(strategy.getStrategyType())) {
            strategyLogic = new TestStrategyLogic();
        } else if (LinEnums.StrategyType.LUXOR.equals(strategy.getStrategyType())) {
            strategyLogic = new LuxorStrategyLogic();
        }
        return strategyLogic;
    }

    public void process(Strategy strategy, StrategyLogic strategyLogic) {
        processor.process(strategy, strategyLogic);
    }

    public void processManual(Order manualOrder, Trade activeTrade, Quote quote) {
        processor.processManual(manualOrder, activeTrade, quote);
    }

    public BacktestResult backtest(Strategy strategy, Calendar startDate, Calendar endDate) {
        Strategy backtestStrategy = new Strategy(); // need to create new instance for backtest, copy required parameters
        strategy.deepCopy(backtestStrategy);
        backtestStrategy.resetStatistics();
        StrategyLogic backtestStrategyLogic = createStrategyLogic(backtestStrategy); // need to create new instance for backtest
        return backtester.backtest(backtestStrategy, backtestStrategyLogic, startDate, endDate);
    }
}
