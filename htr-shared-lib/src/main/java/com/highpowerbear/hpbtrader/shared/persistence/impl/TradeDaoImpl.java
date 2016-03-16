package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.entity.TradeLog;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
@Stateless
public class TradeDaoImpl implements TradeDao {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;

    public void createTrade(Trade trade, Double price) {
        em.persist(trade); // trade orders get persisted too
        TradeLog tradeLog = new TradeLog();
        tradeLog.setTrade(trade);
        tradeLog.setLogDate(HtrUtil.getCalendar());
        trade.copyValues(tradeLog);
        tradeLog.setPrice(price);
        em.persist(tradeLog);
    }

    @Override
    public void updateOrCreateTrade(Trade trade, Double price) {
        if (trade.isNew()) {
            createTrade(trade, price);
            return;
        }
        Trade dbTrade = em.find(Trade.class, trade.getId());
        em.detach(dbTrade);
        em.merge(trade); // trade orders get persisted too
        if (!dbTrade.valuesEqual(trade)) {
            TradeLog tradeLog = new TradeLog();
            tradeLog.setTrade(trade);
            tradeLog.setLogDate(HtrUtil.getCalendar());
            trade.copyValues(tradeLog);
            tradeLog.setPrice(price);
            em.persist(tradeLog);
        }
    }

    @Override
    public List<Trade> getTradesByStrategy(Strategy strategy, boolean ascending) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.dateInitOpen " + (ascending ? "ASC" : "DESC"), Trade.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }

    @Override
    public List<Trade> getTradesByOrder(IbOrder ibOrder) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t, TradeOrder to WHERE to.ibOrder = :ibOrder AND to.trade = t ORDER BY t.dateInitOpen ASC", Trade.class);
        query.setParameter("ibOrder", ibOrder);
        return query.getResultList();
    }

    @Override
    public Long getNumTrades(Strategy strategy) {
        Query query = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.strategy = :strategy");
        query.setParameter("strategy", strategy);
        return (Long) query.getSingleResult();
    }

    @Override
    public Trade getActiveTrade(Strategy strategy) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy AND t.tradeStatus IN :statuses ORDER BY t.dateInitOpen DESC", Trade.class);
        query.setParameter("strategy", strategy);
        Set<HtrEnums.TradeStatus> statuses = new HashSet<>();
        statuses.add(HtrEnums.TradeStatus.INIT_OPEN);
        statuses.add(HtrEnums.TradeStatus.OPEN);
        statuses.add(HtrEnums.TradeStatus.INIT_CLOSE);
        query.setParameter("statuses", statuses);
        List<Trade> trades = query.getResultList();
        return (trades != null && !trades.isEmpty() ? trades.get(0) : null);
    }

    @Override
    public Trade getLastTrade(Strategy strategy) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.dateInitOpen DESC", Trade.class);
        query.setParameter("strategy", strategy);
        List<Trade> trades = query.getResultList();
        return (trades != null && !trades.isEmpty() ? trades.get(0) : null);
    }

    @Override
    public List<TradeLog> getTradeLogs(Trade trade, boolean ascending) {
        TypedQuery<TradeLog> query = em.createQuery("SELECT l FROM TradeLog l WHERE l.trade = :trade ORDER BY l.logDate " + (ascending ? "ASC" : "DESC"), TradeLog.class);
        query.setParameter("trade", trade);
        return query.getResultList();
    }
}
