package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
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
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;
    @Inject TradeDao tradeDao;
    @Inject private IbOrderDao ibOrderDao;

    @Override
    public void createStrategy(Strategy strategy) {
        em.persist(strategy);
    }

    @Override
    public List<Strategy> getAllStrategies(boolean inactiveToo) {
        TypedQuery<Strategy> q;
        if (inactiveToo) {
            q = em.createQuery("SELECT s from Strategy s ORDER BY s.displayOrder ASC", Strategy.class);
        } else {
            q = em.createQuery("SELECT s from Strategy s WHERE s.active = :active ORDER BY s.displayOrder ASC", Strategy.class);
            q.setParameter("active", Boolean.TRUE);
        }
        return q.getResultList();
    }

    @Override
    public Strategy findStrategy(Integer id) {
        return em.find(Strategy.class, id);
    }

    @Override
    public Strategy getActiveStrategy(Instrument tradeInstrument, IbAccount ibAccount) {
        TypedQuery<Strategy> query = em.createQuery("SELECT str FROM Strategy str WHERE str.tradeInstrument = :tradeInstrument AND str.ibAccount = :ibAccount AND str.active = :active", Strategy.class);
        query.setParameter("tradeInstrument", tradeInstrument);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("active", Boolean.TRUE);
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
        l.info("START deleteStrategy " + strategy.getTradeInstrument().getSymbol() + ", " + strategy.getStrategyType().name().toLowerCase());
        strategy = em.find(Strategy.class, strategy.getId());
        Query q;
        for (Trade trade : tradeDao.getTradesByStrategy(strategy, true)) {
            trade.getTradeIbOrders().forEach(em::remove);
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
        l.info("END deleteStrategy " + strategy.getTradeInstrument().getSymbol() + ", " + strategy.getStrategyType().name().toLowerCase());
    }

    @Override
    public List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending) {
        TypedQuery<StrategyLog> query = em.createQuery("SELECT sl FROM StrategyLog sl WHERE sl.strategy = :strategy ORDER BY sl.logDate " + (ascending ? "ASC" : "DESC"), StrategyLog.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }
}
