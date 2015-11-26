package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
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
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;
    @Inject TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;

    @Override
    public void createStrategy(Strategy strategy) {
        em.persist(strategy);
    }

    @Override
    public Strategy findStrategy(Integer id) {
        return em.find(Strategy.class, id);
    }

    @Override
    public Strategy getActiveStrategy(Series series) {
        TypedQuery<Strategy> query = em.createQuery("SELECT str FROM Strategy str WHERE str.series = :series AND str.isActive = :isActive", Strategy.class);
        query.setParameter("series", series);
        query.setParameter("isActive", Boolean.TRUE);
        List<Strategy> strategyList = query.getResultList();
        return strategyList.get(0);
    }

    @Override
    public void updateStrategy(Strategy strategy) {
        Strategy dbStrategy = em.find(Strategy.class, strategy.getId());
        em.detach(dbStrategy);
        em.merge(strategy);
        if (!dbStrategy.valuesEqual(strategy)) {
            StrategyLog strategyLog = new StrategyLog();
            strategyLog.setStrategy(strategy);
            strategyLog.setLogDate(HtrUtil.getCalendar());
            strategy.copyValues(strategyLog);
            em.persist(strategyLog);
        }
    }

    @Override
    public void deleteStrategy(Strategy strategy) {
        l.info("START deleteStrategy " + strategy.getSeries().getSymbol() + ", " + strategy.getSeries().getInterval().getDisplayName() + ", " + strategy.getStrategyType().getDisplayName());
        strategy = em.find(Strategy.class, strategy.getId());
        Query q;
        for (Trade trade : tradeDao.getTradesByStrategy(strategy, true)) {
            trade.getTradeOrders().forEach(em::remove);
            q = em.createQuery("DELETE FROM TradeLog tl WHERE tl.trade = :trade");
            q.setParameter("trade", trade);
            q.executeUpdate();
            em.remove(trade);
        }
        q = em.createQuery("DELETE FROM StrategyLog sl WHERE sl.strategy = :strategy");
        q.setParameter("strategy", strategy);
        q.executeUpdate();
        for (IbOrder ibOrder : ibOrderDao.getIbOrdersByStrategy(strategy)) {
            ibOrder.getEvents().forEach(em::remove);
            em.remove(ibOrder);
        }
        em.remove(strategy);
        l.info("END deleteStrategy " + strategy.getSeries().getSymbol() + ", " + strategy.getSeries().getInterval().getDisplayName() + ", " + strategy.getStrategyType().getDisplayName());
    }

    @Override
    public List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending) {
        TypedQuery<StrategyLog> query = em.createQuery("SELECT sl FROM StrategyLog sl WHERE sl.strategy = :strategy ORDER BY sl.logDate " + (ascending ? "ASC" : "DESC"), StrategyLog.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }
}
