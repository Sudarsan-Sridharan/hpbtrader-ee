package com.highpowerbear.hpbtrader.linear.cdibean.series;

/**
 * Created by robertk on 6/2/14.
 */

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import org.primefaces.event.SelectEvent;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Named
@SessionScoped
public class EditStrategiesBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private StrategyController strategyController;
    @Inject private SeriesBean seriesBean;
    @Inject private DatabaseDao databaseDao;

    private Series series;
    private List<Strategy> strategies;
    private Strategy activeStrategy;
    private Strategy selectedStrategy;
    private Strategy newStrategy;

    public EditStrategiesBean() {
        selectedStrategy = new Strategy();
        newStrategy = new Strategy();
    }

    public void init()  {
        series = seriesBean.getSelectedSeriesRecord().getSeries();
        strategies = series.getStrategies();
        activeStrategy = series.getActiveStrategy();
        selectedStrategy = series.getActiveStrategy();
        newStrategy = new Strategy();
        newStrategy.setSeries(series);
        newStrategy.setTradingQuantity(series.getSecType().getDefaultTradingQuantity());
    }

    private void refreshFromDb() {
        series = databaseDao.findSeries(series.getId());
        strategies = series.getStrategies();
        activeStrategy = series.getActiveStrategy();
    }

    public void activateStrategy() {
        if (selectedStrategy.equals(activeStrategy)) {
            return;
        }
        activeStrategy.setIsActive(Boolean.FALSE);
        selectedStrategy.setIsActive(Boolean.TRUE);
        databaseDao.updateStrategy(activeStrategy);
        databaseDao.updateStrategy(selectedStrategy);
        strategyController.swapStrategyLogic(series);
        activeStrategy = selectedStrategy;
    }

    public void updateStrategy() {
        databaseDao.updateStrategy(selectedStrategy);
        refreshFromDb();

    }

    public void addStrategy() {
        newStrategy.setId(null);
        databaseDao.addStrategy(newStrategy);
        refreshFromDb();
        selectedStrategy = newStrategy;
    }

    public void deleteStrategy() {
        if (strategies.size() < 2 || selectedStrategy.getIsActive()) {
            return;
        }
        databaseDao.deleteStrategy(selectedStrategy);
        refreshFromDb();
        selectedStrategy = strategies.get(0);
    }

    public void onStrategySelect(SelectEvent event) {
    }

    public void newStrategyTypeChanged() {
        newStrategy.setParams(newStrategy.getStrategyType().getDefaultParams());
    }

    public Series getSeries() {
        return series;
    }

    public List<Strategy> getStrategies() {
        return strategies;
    }

    public Strategy getSelectedStrategy() {
        return selectedStrategy;
    }

    public void setSelectedStrategy(Strategy selectedStrategy) {
        this.selectedStrategy = selectedStrategy;
    }

    public Strategy getNewStrategy() {
        return newStrategy;
    }

    public List<LinEnums.StrategyMode> getStrategyModes() {
        return LinEnums.selectableStrategyModes;
    }

    public List<Integer> getTradingQuantities() {
        return LinEnums.tradingQuantities;
    }

    public List<LinEnums.StrategyType> getStrategyTypes() {
        return Arrays.asList(LinEnums.StrategyType.values());
    }

    public Boolean getActivateDisabled() {
        return selectedStrategy == null || selectedStrategy.getIsActive();
    }

    public Boolean getDeleteDisabled() {
        return strategies == null || strategies.size() < 2 || selectedStrategy == null || selectedStrategy.getIsActive();
    }
}

