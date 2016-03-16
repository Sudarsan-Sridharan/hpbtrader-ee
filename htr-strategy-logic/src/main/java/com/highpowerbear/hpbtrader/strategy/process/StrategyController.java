package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.strategy.common.LinData;
import com.highpowerbear.hpbtrader.strategy.common.LinSettings;
import com.highpowerbear.hpbtrader.strategy.linear.BacktestResult;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.LuxorStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.MacdCrossStrategyLogic;
import com.highpowerbear.hpbtrader.strategy.linear.logic.TestStrategyLogic;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;

import javax.annotation.PostConstruct;
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

    @Inject private StrategyDao strategyDao;
    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private LinData linData;
    @Inject private Processor processor;
    @Inject private Backtester backtester;

    @PostConstruct
    public void init() {
        dataSeriesDao.getAllSeries(false).forEach(this::swapStrategyLogic);
    }

    public void swapStrategyLogic(DataSeries dataSeries) {
        if (dataSeries.getEnabled()) {
            Strategy activeStrategy = strategyDao.getActiveStrategy(dataSeries);
            linData.getStrategyLogicMap().put(dataSeries.getId(), createStrategyLogic(activeStrategy));
        } else {
            linData.getStrategyLogicMap().remove(dataSeries.getId());
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

    public void process(DataSeries dataSeries) {
        if (!dataSeries.getEnabled()) {
            l.info("Series not enabled, bars won't be processed, seriesId=" + dataSeries.getId() + ", symbol=" + dataSeries.getSymbol());
            return;
        }
        Strategy activeStrategy = strategyDao.getActiveStrategy(dataSeries);
        StrategyLogic strategyLogic = linData.getStrategyLogicMap().get(dataSeries.getId());
        if (dataSeriesDao.getNumBars(dataSeries) >= HtrDefinitions.BARS_REQUIRED) {
            processor.process(activeStrategy, strategyLogic);
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
