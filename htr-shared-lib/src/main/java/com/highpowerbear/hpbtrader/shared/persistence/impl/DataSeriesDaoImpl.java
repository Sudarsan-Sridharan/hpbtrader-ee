package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
@Stateless
public class DataSeriesDaoImpl implements DataSeriesDao {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;
    @Inject private StrategyDao strategyDao;

    @Override
    public void createSeries(DataSeries dataSeries) {
        em.persist(dataSeries);
    }

    @Override
    public List<DataSeries> getAllSeries(boolean disabledToo) {
        TypedQuery<DataSeries> q;
        if (disabledToo) {
            q = em.createQuery("SELECT s from DataSeries s ORDER BY s.displayOrder ASC", DataSeries.class);
        } else {
            q = em.createQuery("SELECT s from DataSeries s WHERE s.isEnabled = :isEnabled ORDER BY s.displayOrder ASC", DataSeries.class);
            q.setParameter("isEnabled", Boolean.TRUE);
        }
        return q.getResultList();
    }

    @Override
    public List<DataSeries> getSeriesByInterval(HtrEnums.Interval interval) {
        TypedQuery<DataSeries> query = em.createQuery("SELECT s FROM DataSeries s WHERE s.interval = :interval", DataSeries.class);
        query.setParameter("interval", interval);
        return query.getResultList();
    }

    @Override
    public List<DataSeries> getSeries(String symbol, HtrEnums.Interval interval) {
        TypedQuery<DataSeries> query = em.createQuery("SELECT s FROM DataSeries s WHERE s.symbol = :symbol AND s.interval = :interval", DataSeries.class);
        query.setParameter("symbol", symbol);
        query.setParameter("interval", interval);
        return query.getResultList();
    }

    @Override
    public DataSeries findSeries(Integer id) {
        return em.find(DataSeries.class, id);
    }

    @Override
    public void updateSeries(DataSeries dataSeries) {
        em.merge(dataSeries);
    }

    @Override
    public Integer getHighestDisplayOrder() {
        Query query = em.createQuery("SELECT MAX(s.displayOrder) from DataSeries s");
        return (Integer) query.getSingleResult();
    }

    @Override
    public void deleteSeries(DataSeries dataSeries) {
        l.info("START deleteSeries " + dataSeries.getSymbol());
        dataSeries = em.find(DataSeries.class, dataSeries.getId()); // make sure it is managed by entitymanager
        dataSeries.getStrategies().forEach(strategyDao::deleteStrategy);
        Query q = em.createQuery("DELETE FROM DataBar q WHERE q.series = :series");
        q.setParameter("series", dataSeries);
        q.executeUpdate();
        em.remove(dataSeries);
        l.info("END deleteSeries " + dataSeries.getSymbol());
    }

    @Override
    public void createBars(DataSeries dataSeries, List<DataBar> dataBars) {
        if (dataBars == null || dataBars.isEmpty()) {
            return;
        }
        l.fine("START createBars, symbol=" + dataSeries.getSymbol());
        int created = 0;
        int updated = 0;
        for (DataBar dataBar : dataBars) {
            if (!dataSeries.equals(dataBar.getDataSeries())) {
                continue;
            }
            TypedQuery<DataBar> query = em.createQuery("SELECT b FROM DataBar b WHERE b.series = :series AND b.qDateBarClose = :qDateBarClose", DataBar.class);
            query.setParameter("series", dataSeries);
            query.setParameter("qDateBarClose", dataBar.getqDateBarClose());
            List<DataBar> bl = query.getResultList();
            DataBar dbDataBar = (bl != null && !bl.isEmpty() ? bl.get(0) : null);
            if (dbDataBar == null) {
                // insert
                l.fine("Adding " + dataBar.print());
                created++;
                em.persist(dataBar);
            } else {
                // update
                l.fine(dbDataBar.print() + " --> " + dataBar.print());
                updated++;
                dbDataBar.mergeFrom(dataBar);
                em.merge(dbDataBar);
            }
        }
        l.fine("END createBars, symbol=" + dataSeries.getSymbol() + ", added=" + created + ", updated=" + updated);
    }

    @Override
    public List<DataBar> getBars(DataSeries dataSeries, Integer numBars) {
        TypedQuery<DataBar> query = em.createQuery("SELECT b FROM DataBar b WHERE b.series = :series ORDER BY b.qDateBarClose ASC", DataBar.class);
        query.setParameter("series", dataSeries);
        if (numBars != null && numBars > 0) {
            query.setMaxResults(numBars);
        }
        List<DataBar> dataBars = query.getResultList();
        if (dataBars == null) {
            dataBars = new ArrayList<>();
        }
        return dataBars;
    }

    @Override
    public List<DataBar> getPagedBars(DataSeries dataSeries, Integer start, Integer limit) {
        return null;
    }

    @Override
    public DataBar getLastBar(DataSeries dataSeries) {
        TypedQuery<DataBar> query = em.createQuery("SELECT b FROM DataBar b WHERE b.series = :series ORDER BY b.qDateBarClose DESC", DataBar.class);
        query.setParameter("series", dataSeries);
        query.setMaxResults(1);
        List<DataBar> dataBars = query.getResultList();
        return (dataBars == null || dataBars.isEmpty() ? null : dataBars.get(0));
    }

    @Override
    public Long getNumBars(DataSeries dataSeries) {
        Query query = em.createQuery("SELECT COUNT(b) FROM DataBar b WHERE b.series = :series");
        query.setParameter("series", dataSeries);
        return (Long) query.getSingleResult();
    }
}
