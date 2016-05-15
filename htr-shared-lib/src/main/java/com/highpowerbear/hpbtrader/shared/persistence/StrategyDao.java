package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.*;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface StrategyDao {
    List<Strategy> getStrategies();
    Strategy findStrategy(Integer id);
    Strategy getActiveStrategy(Instrument tradeInstrument, IbAccount ibAccount);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyLog> getPagedStrategyLogs(Strategy strategy, int start, int limit);
    Long getNumStrategyLogs(Strategy strategy);
}
