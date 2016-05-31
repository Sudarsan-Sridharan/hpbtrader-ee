package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.*;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface StrategyDao {
    List<Strategy> getStrategies();
    List<Strategy> getStrategies(String inputSeriesAlias);
    Strategy findStrategy(Integer id);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyLog> getPagedStrategyLogs(Strategy strategy, int start, int limit);
    Long getNumStrategyLogs(Strategy strategy);
}
