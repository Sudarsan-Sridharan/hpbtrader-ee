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
import java.util.Calendar;
import java.util.Collections;
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
    public List<DataSeries> getAllDataSeries() {
        TypedQuery<DataSeries> q = em.createQuery("SELECT s from DataSeries s ORDER BY s.displayOrder", DataSeries.class);
        return q.getResultList();
    }

    @Override
    public List<DataSeries> getDataSeriesByInterval(HtrEnums.Interval interval) {
        TypedQuery<DataSeries> query = em.createQuery("SELECT s FROM DataSeries s WHERE s.interval = :interval", DataSeries.class);
        query.setParameter("interval", interval);
        return query.getResultList();
    }

    @Override
    public DataSeries getDataSeriesByAlias(String alias) {
        TypedQuery<DataSeries> query = em.createQuery("SELECT s FROM DataSeries s WHERE s.alias = :alias", DataSeries.class);
        query.setParameter("alias", alias);
        return query.getResultList().get(0);
    }

    @Override
    public DataSeries findDataSeries(Integer id) {
        return em.find(DataSeries.class, id);
    }

    @Override
    public void createDataBars(DataSeries dataSeries, List<DataBar> dataBars) {
        if (dataBars == null || dataBars.isEmpty()) {
            return;
        }
        l.fine("START createDataBars, symbol=" + dataSeries.getInstrument().getSymbol());
        int created = 0;
        int updated = 0;
        for (DataBar dataBar : dataBars) {
            if (!dataSeries.equals(dataBar.getDataSeries())) {
                continue;
            }
            TypedQuery<DataBar> query = em.createQuery("SELECT b FROM DataBar b WHERE b.dataSeries = :dataSeries AND b.barCloseDate = :barCloseDate", DataBar.class);
            query.setParameter("dataSeries", dataSeries);
            query.setParameter("barCloseDate", dataBar.getBarCloseDate());
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
        l.fine("END createDataBars, symbol=" + dataSeries.getInstrument().getSymbol() + ", added=" + created + ", updated=" + updated);
    }

    @Override
    public List<DataBar> getLastDataBars(DataSeries dataSeries, int numBars, int offsetFromLast) {
        TypedQuery<DataBar> q = em.createQuery("SELECT b FROM DataBar b where b.dataSeries = :dataSeries ORDER BY b.barCloseDate DESC", DataBar.class);
        q.setParameter("dataSeries", dataSeries);
        q.setFirstResult(Math.abs(offsetFromLast));
        q.setMaxResults(numBars);
        List<DataBar> dataBars = q.getResultList();
        Collections.reverse(dataBars);
        return dataBars ;
    }

    @Override
    public List<DataBar> getDataBars(DataSeries dataSeries, int numBars, Calendar lastDate) {
        TypedQuery<DataBar> q = em.createQuery("SELECT b FROM DataBar b where b.dataSeries = :dataSeries AND b.barCloseDate <= :lastDate ORDER BY b.barCloseDate  DESC", DataBar.class);
        q.setParameter("dataSeries", dataSeries);
        q.setParameter("lastDate", lastDate);
        q.setMaxResults(numBars);
        List<DataBar> dataBars = q.getResultList();
        Collections.reverse(dataBars);
        return dataBars ;
    }

    @Override
    public List<DataBar> getPagedDataBars(DataSeries dataSeries, int start, int limit) {
        TypedQuery<DataBar> q = em.createQuery("SELECT b FROM DataBar b where b.dataSeries = :dataSeries ORDER BY b.barCloseDate  DESC", DataBar.class);
        q.setParameter("dataSeries", dataSeries);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumDataBars(DataSeries dataSeries) {
        Query query = em.createQuery("SELECT COUNT(b) FROM DataBar b WHERE b.dataSeries = :dataSeries");
        query.setParameter("dataSeries", dataSeries);
        return (Long) query.getSingleResult();
    }
}
