package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.*;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface StrategyDao {
    void createStrategy(Strategy strategy);
    List<Strategy> getAllStrategies(boolean inactiveToo);
    Strategy findStrategy(Integer id);
    Strategy getActiveStrategy(Instrument tradeInstrument, IbAccount ibAccount);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending);
}
