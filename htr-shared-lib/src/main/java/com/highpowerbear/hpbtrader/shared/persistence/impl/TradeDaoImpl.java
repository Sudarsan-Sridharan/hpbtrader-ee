package com.highpowerbear.hpbtrader.shared.persistence.impl;

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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by robertk on 19.11.2015.
 */
@Stateless
public class TradeDaoImpl implements TradeDao {

    @PersistenceContext
    private EntityManager em;

    private void createTrade(Trade trade, Double price) {
        em.persist(trade); // trade orders get persisted too
        TradeLog tradeLog = new TradeLog();
        tradeLog.setTrade(trade);
        tradeLog.setLogDate(HtrUtil.getCalendar());
        trade.copyValues(tradeLog);
        tradeLog.setPrice(price);
        em.persist(tradeLog);
    }

    @Override
    public void updateOrCreateTrade(Trade trade, Calendar date, Double price) {
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
            tradeLog.setLogDate(date);
            trade.copyValues(tradeLog);
            tradeLog.setPrice(price);
            em.persist(tradeLog);
        }
    }

    @Override
    public List<Trade> getTrades(Strategy strategy) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.initOpenDate", Trade.class);
        q.setParameter("strategy", strategy);
        return q.getResultList();
    }

    @Override
    public List<Trade> getTradesByOrder(IbOrder ibOrder) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t, TradeIbOrder to WHERE to.ibOrder = :ibOrder AND to.trade = t ORDER BY t.initOpenDate", Trade.class);
        q.setParameter("ibOrder", ibOrder);
        return q.getResultList();
    }

    @Override
    public Trade findTrade(Long id) {
        return em.find(Trade.class, id);
    }

    @Override
    public Long getNumTrades(Strategy strategy) {
        Query q = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.strategy = :strategy");
        q.setParameter("strategy", strategy);
        return (Long) q.getSingleResult();
    }

    @Override
    public Trade getActiveTrade(Strategy strategy) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy AND t.tradeStatus IN :statuses ORDER BY t.initOpenDate DESC", Trade.class);
        q.setParameter("strategy", strategy);
        Set<HtrEnums.TradeStatus> statuses = new HashSet<>();
        statuses.add(HtrEnums.TradeStatus.INIT_OPEN);
        statuses.add(HtrEnums.TradeStatus.OPEN);
        statuses.add(HtrEnums.TradeStatus.INIT_CLOSE);
        q.setParameter("statuses", statuses);
        List<Trade> trades = q.getResultList();
        return (trades != null && !trades.isEmpty() ? trades.get(0) : null);
    }

    @Override
    public Trade getLastTrade(Strategy strategy) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.initOpenDate DESC", Trade.class);
        q.setParameter("strategy", strategy);
        List<Trade> trades = q.getResultList();
        return (trades != null && !trades.isEmpty() ? trades.get(0) : null);
    }

    @Override
    public List<Trade> getPagedTrades(Strategy strategy, int start, int limit) {
        TypedQuery<Trade> q = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.initOpenDate DESC", Trade.class);
        q.setParameter("strategy", strategy);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<TradeLog> getPagedTradeLogs(Trade trade, int start, int limit) {
        TypedQuery<TradeLog> q = em.createQuery("SELECT l FROM TradeLog l WHERE l.trade = :trade ORDER BY l.logDate DESC", TradeLog.class);
        q.setParameter("trade", trade);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }
}