package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
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
    public void createBars(List<Bar> bars) {
        if (bars == null || bars.isEmpty()) {
            return;
        }
        String symbol = bars.iterator().next().getSeries().getSymbol();
        l.fine("START createBars, symbol=" + symbol);
        int added = 0;
        int modified = 0;
        for (Bar q : bars) {
            TypedQuery<Bar> query = em.createQuery("SELECT q FROM Bar q WHERE q.series = :series AND q.qDateBarClose = :qDateBarClose", Bar.class);
            query.setParameter("series", q.getSeries());
            query.setParameter("qDateBarClose", q.getqDateBarClose());
            List<Bar> ql = query.getResultList();
            Bar dbBar = (ql != null && !ql.isEmpty() ? ql.get(0) : null);
            if (dbBar == null) {
                // insert
                l.fine("Adding " + q.printValues());
                added++;
                em.persist(q);
            } else {
                // update
                if (!dbBar.valuesEqual(q)) {
                    l.fine(dbBar.printValues() + " --> " + q.printValues());
                    dbBar.copyValuesFrom(q);
                    modified++;
                    em.merge(dbBar);
                }
            }
        }
        l.fine("END createBars, symbol=" + symbol + ", added=" + added + ", modified=" + modified);
    }

    @Override
    public List<Bar> getBars(Integer seriesId, Integer numBars) {
        TypedQuery<Bar> query = em.createQuery("SELECT q FROM Bar q WHERE q.series.id = :seriesId ORDER BY q.qDateBarClose DESC", Bar.class);
        query.setParameter("seriesId", seriesId);
        if (numBars != null && numBars > 0) {
            query.setMaxResults(numBars);
        }
        List<Bar> bars = query.getResultList();
        if (bars == null) {
            bars = new ArrayList<>();
        }
        Collections.reverse(bars);
        return bars;
    }

    @Override
    public Bar getLastBar(Series series) {
        TypedQuery<Bar> query = em.createQuery("SELECT q FROM Bar q WHERE q.series = :series ORDER BY q.qDateBarClose DESC", Bar.class);
        query.setParameter("series", series);
        query.setMaxResults(1);
        List<Bar> bars = query.getResultList();
        return (bars == null || bars.isEmpty() ? null : bars.get(0));
    }

    @Override
    public Long getNumBars(Series series) {
        Query query = em.createQuery("SELECT COUNT(q) FROM Bar q WHERE q.series.id = :seriesId");
        query.setParameter("seriesId", series.getId());
        return (Long) query.getSingleResult();
    }
}
