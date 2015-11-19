package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
public class SeriesDaoImpl implements SeriesDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void addSeries(Series series) {

    }

    @Override
    public List<Series> getAllSeries(boolean disabledToo) {
        return null;
    }

    @Override
    public List<Series> getSeriesByInterval(HtrEnums.Interval interval) {
        return null;
    }

    @Override
    public List<Series> getSeries(String symbol, HtrEnums.Interval interval) {
        return null;
    }

    @Override
    public Series findSeries(Integer id) {
        return null;
    }

    @Override
    public void updateSeries(Series series) {

    }

    @Override
    public Integer getHighestDisplayOrder() {
        return null;
    }

    @Override
    public void deleteSeries(Series series) {

    }
}
