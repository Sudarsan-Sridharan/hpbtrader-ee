package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.StrategyLog;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface StrategyDao {
    void createStrategy(Strategy strategy);
    Strategy findStrategy(Integer id);
    Strategy getActiveStrategy(DataSeries dataSeries);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending);
}
