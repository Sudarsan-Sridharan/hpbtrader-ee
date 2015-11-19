package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.StrategyLog;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
public class StrategyDaoImpl implements StrategyDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createStrategy(Strategy strategy) {

    }

    @Override
    public Strategy findStrategy(Integer id) {
        return null;
    }

    @Override
    public Strategy getActiveStrategy(Series series) {
        return null;
    }

    @Override
    public void updateStrategy(Strategy strategy) {

    }

    @Override
    public void deleteStrategy(Strategy strategy) {

    }

    @Override
    public List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending) {
        return null;
    }
}
