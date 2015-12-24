package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;

import javax.ejb.Stateless;
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
public class BarDaoImpl implements BarDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createBars(Series series, List<Bar> bars) {
        if (bars == null || bars.isEmpty()) {
            return;
        }
        l.fine("START createBars, symbol=" + series.getSymbol());
        int created = 0;
        int updated = 0;
        for (Bar bar : bars) {
            if (!series.equals(bar.getSeries())) {
                continue;
            }
            TypedQuery<Bar> query = em.createQuery("SELECT b FROM Bar b WHERE b.series = :series AND b.qDateBarClose = :qDateBarClose", Bar.class);
            query.setParameter("series", series);
            query.setParameter("qDateBarClose", bar.getqDateBarClose());
            List<Bar> bl = query.getResultList();
            Bar dbBar = (bl != null && !bl.isEmpty() ? bl.get(0) : null);
            if (dbBar == null) {
                // insert
                l.fine("Adding " + bar.print());
                created++;
                em.persist(bar);
            } else {
                // update
                l.fine(dbBar.print() + " --> " + bar.print());
                updated++;
                dbBar.mergeFrom(bar);
                em.merge(dbBar);
            }
        }
        l.fine("END createBars, symbol=" + series.getSymbol() + ", added=" + created + ", updated=" + updated);
    }

    @Override
    public List<Bar> getBars(Series series, Integer numBars) {
        TypedQuery<Bar> query = em.createQuery("SELECT b FROM Bar b WHERE b.series = :series ORDER BY b.qDateBarClose ASC", Bar.class);
        query.setParameter("series", series);
        if (numBars != null && numBars > 0) {
            query.setMaxResults(numBars);
        }
        List<Bar> bars = query.getResultList();
        if (bars == null) {
            bars = new ArrayList<>();
        }
        return bars;
    }

    @Override
    public List<Bar> getPagedBars(Series series, Integer start, Integer limit) {
        return null;
    }

    @Override
    public Bar getLastBar(Series series) {
        TypedQuery<Bar> query = em.createQuery("SELECT b FROM Bar b WHERE b.series = :series ORDER BY b.qDateBarClose DESC", Bar.class);
        query.setParameter("series", series);
        query.setMaxResults(1);
        List<Bar> bars = query.getResultList();
        return (bars == null || bars.isEmpty() ? null : bars.get(0));
    }

    @Override
    public Long getNumBars(Series series) {
        Query query = em.createQuery("SELECT COUNT(b) FROM Bar b WHERE b.series = :series");
        query.setParameter("series", series);
        return (Long) query.getSingleResult();
    }
}
