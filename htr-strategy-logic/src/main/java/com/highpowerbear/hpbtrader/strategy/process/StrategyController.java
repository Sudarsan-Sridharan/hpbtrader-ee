package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.strategy.linear.BacktestResult;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;
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
    @Inject private Processor processor;
    @Inject private Backtester backtester;

    private Map<Strategy, StrategyLogic> strategyLogicMap = new HashMap<>();

    @PostConstruct
    public void init() {
        strategyDao.getAllStrategies(false).forEach(this::swapStrategyLogic);
    }

    public void swapStrategyLogic(Strategy strategy) {
        if (strategy.getActive()) {
            strategyLogicMap.put(strategy, createStrategyLogic(strategy));
        } else {
            strategyLogicMap.remove(strategy);
        }
    }

    private StrategyLogic createStrategyLogic(Strategy strategy) {
        StrategyLogic strategyLogic = null;
        if (HtrEnums.StrategyType.MACD_CROSS.equals(strategy.getStrategyType())) {
            strategyLogic = new MacdCrossStrategyLogic();
        } else if (HtrEnums.StrategyType.TEST.equals(strategy.getStrategyType())) {
            strategyLogic = new TestStrategyLogic();
        } else if (HtrEnums.StrategyType.LUXOR.equals(strategy.getStrategyType())) {
            strategyLogic = new LuxorStrategyLogic();
        }
        return strategyLogic;
    }

    public void process(Strategy strategy) {
        StrategyLogic strategyLogic = strategyLogicMap.get(strategy);
        DataSeries dataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        if (dataSeriesDao.getNumBars(dataSeries) >= HtrDefinitions.BARS_REQUIRED) {
            processor.process(strategy, strategyLogic);
        }
    }

    public void processManual(IbOrder manualIbOrder, Trade activeTrade, DataBar dataBar) {
        processor.processManual(manualIbOrder, activeTrade, dataBar);
    }

    public BacktestResult backtest(Strategy strategy, Calendar startDate, Calendar endDate) {
        Strategy backtestStrategy = new Strategy(); // need to create new instance for backtest, copy required parameters
        strategy.deepCopy(backtestStrategy);
        backtestStrategy.resetStatistics();
        StrategyLogic backtestStrategyLogic = createStrategyLogic(backtestStrategy); // need to create new instance for backtest
        return backtester.backtest(backtestStrategy, backtestStrategyLogic, startDate, endDate);
    }
}
