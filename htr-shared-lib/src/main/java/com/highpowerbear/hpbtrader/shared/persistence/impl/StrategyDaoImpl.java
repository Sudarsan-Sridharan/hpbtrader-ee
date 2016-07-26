package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.StrategyPerformance;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;

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
public class StrategyDaoImpl implements StrategyDao {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;
    @Inject TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;

    @Override
    public List<Strategy> getStrategies() {
        TypedQuery<Strategy> q = em.createQuery("SELECT s from Strategy s ORDER BY s.displayOrder", Strategy.class);
        return q.getResultList();
    }

    @Override
    public List<Strategy> getStrategiesByInputSeriesAlias(String inputSeriesAlias) {
        TypedQuery<Strategy> q = em.createQuery("SELECT s from Strategy s WHERE s.inputSeriesAliases LIKE :inputSeriesAlias ORDER BY s.displayOrder", Strategy.class);
        q.setParameter("inputSeriesAlias", "%" + inputSeriesAlias + "%");
        return q.getResultList();
    }

    @Override
    public Strategy findStrategy(Integer id) {
        return em.find(Strategy.class, id);
    }

    @Override
    public void updateStrategy(Strategy strategy) {
        em.merge(strategy);
        em.persist(strategy.createPerformance());
    }

    @Override
    public void deleteStrategy(Strategy strategy) {
        l.info("START deleteStrategy " + strategy.getTradeInstrument().getSymbol() + ", " + strategy.getStrategyType().name().toLowerCase());
        strategy = em.find(Strategy.class, strategy.getId());
        Query q;
        for (Trade trade : tradeDao.getTrades(strategy)) {
            trade.getTradeIbOrders().forEach(em::remove);
            q = em.createQuery("DELETE FROM TradeLog tl WHERE tl.trade = :trade");
            q.setParameter("trade", trade);
            q.executeUpdate();
            em.remove(trade);
        }
        q = em.createQuery("DELETE FROM StrategyPerformance sl WHERE sl.strategy = :strategy");
        q.setParameter("strategy", strategy);
        q.executeUpdate();
        for (IbOrder ibOrder : ibOrderDao.getIbOrders(strategy)) {
            ibOrder.getIbOrderEvents().forEach(em::remove);
            em.remove(ibOrder);
        }
        em.remove(strategy);
        l.info("END deleteStrategy " + strategy.getTradeInstrument().getSymbol() + ", " + strategy.getStrategyType().name().toLowerCase());
    }

    @Override
    public List<StrategyPerformance> getPagedStrategyLogs(Strategy strategy, int start, int limit) {
        TypedQuery<StrategyPerformance> q = em.createQuery("SELECT sl FROM StrategyPerformance sl WHERE sl.strategy = :strategy ORDER BY sl.performanceDate DESC", StrategyPerformance.class);
        q.setParameter("strategy", strategy);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Long getNumStrategyLogs(Strategy strategy) {
        Query q = em.createQuery("SELECT COUNT(sl) FROM StrategyPerformance sl WHERE sl.strategy = :strategy");
        q.setParameter("strategy", strategy);
        return (Long) q.getSingleResult();
    }
}
