package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.StrategyPerformance;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface StrategyDao {
    List<Strategy> getStrategies();
    List<Strategy> getStrategiesByInputSeriesAlias(String inputSeriesAlias);
    Strategy findStrategy(Integer id);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyPerformance> getPagedStrategyPerformances(Strategy strategy, int start, int limit);
    List<StrategyPerformance> getLatestStrategyPerformances(Strategy strategy, int limit);
    Long getNumStrategyLogs(Strategy strategy);
}
