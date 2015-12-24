package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;
import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
@Stateless
public class SeriesDaoImpl implements SeriesDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;
    @Inject private StrategyDao strategyDao;

    @Override
    public void createSeries(Series series) {
        em.persist(series);
    }

    @Override
    public List<Series> getAllSeries(boolean disabledToo) {
        TypedQuery<Series> q;
        if (disabledToo) {
            q = em.createQuery("SELECT s from Series s ORDER BY s.displayOrder ASC", Series.class);
        } else {
            q = em.createQuery("SELECT s from Series s WHERE s.isEnabled = :isEnabled ORDER BY s.displayOrder ASC", Series.class);
            q.setParameter("isEnabled", Boolean.TRUE);
        }
        return q.getResultList();
    }

    @Override
    public List<Series> getSeriesByInterval(HtrEnums.Interval interval) {
        TypedQuery<Series> query = em.createQuery("SELECT s FROM Series s WHERE s.interval = :interval", Series.class);
        query.setParameter("interval", interval);
        return query.getResultList();
    }

    @Override
    public List<Series> getSeries(String symbol, HtrEnums.Interval interval) {
        TypedQuery<Series> query = em.createQuery("SELECT s FROM Series s WHERE s.symbol = :symbol AND s.interval = :interval", Series.class);
        query.setParameter("symbol", symbol);
        query.setParameter("interval", interval);
        return query.getResultList();
    }

    @Override
    public Series findSeries(Integer id) {
        return em.find(Series.class, id);
    }

    @Override
    public void updateSeries(Series series) {
        em.merge(series);
    }

    @Override
    public Integer getHighestDisplayOrder() {
        Query query = em.createQuery("SELECT MAX(s.displayOrder) from Series s");
        return (Integer) query.getSingleResult();
    }

    @Override
    public void deleteSeries(Series series) {
        l.info("START deleteSeries " + series.getSymbol());
        series = em.find(Series.class, series.getId()); // make sure it is managed by entitymanager
        series.getStrategies().forEach(strategyDao::deleteStrategy);
        Query q = em.createQuery("DELETE FROM Bar q WHERE q.series = :series");
        q.setParameter("series", series);
        q.executeUpdate();
        em.remove(series);
        l.info("END deleteSeries " + series.getSymbol());
    }
}
