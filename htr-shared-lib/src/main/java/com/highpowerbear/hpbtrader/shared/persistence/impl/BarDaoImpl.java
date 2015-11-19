package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
public class BarDaoImpl implements BarDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createBars(List<Bar> bars) {

    }

    @Override
    public List<Bar> getBars(Integer seriesId, Integer numBars) {
        return null;
    }

    @Override
    public Bar getLastBar(Series series) {
        return null;
    }

    @Override
    public Long getNumBars(Series series) {
        return null;
    }
}
