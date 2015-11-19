package com.highpowerbear.hpbtrader.linear.persistence;

import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.*;
import javax.ejb.Singleton;
import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Named
@Singleton
public class LinDaoImpl implements Serializable, LinDao {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public IbAccount findIbAccount(String accountId) {
        return em.find(IbAccount.class, accountId);
    }

    @Override
    public List<IbAccount> getIbAccounts() {
        TypedQuery<IbAccount> q = em.createQuery("SELECT ia FROM IbAccount ia ORDER BY ia.port", IbAccount.class);
        return q.getResultList();
    }

    @Override
    public IbAccount updateIbAccount(IbAccount ibAccount) {
        return em.merge(ibAccount);
    }

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
    public Long getNumBars(Series s) {
        Query query = em.createQuery("SELECT COUNT(q) FROM Bar q WHERE q.series.id = :seriesId");
        query.setParameter("seriesId", s.getId());
        return (Long) query.getSingleResult();
    }

    @Override
    public void addSeries(Series series) {
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
    public List<Series> getSeriesByInterval(LinEnums.Interval interval) {
        TypedQuery<Series> query = em.createQuery("SELECT s FROM Series s WHERE s.interval = :interval", Series.class);
        query.setParameter("interval", interval);
        return query.getResultList();
    }

    @Override
    public List<Series> getSeries(String symbol, LinEnums.Interval interval) {
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
        for (Strategy strategy : series.getStrategies()) {
            this.deleteStrategy(strategy);
        }
        Query q = em.createQuery("DELETE FROM Bar q WHERE q.series = :series");
        q.setParameter("series", series);
        q.executeUpdate();
        em.remove(series);
        l.info("END deleteSeries " + series.getSymbol());
    }

    @Override
    public void createIbOrder(IbOrder ibOrder) {
        em.persist(ibOrder); // strategy order events get persisted too
    }

    @Override
    public void updateIbOrder(IbOrder ibOrder) {
        em.merge(ibOrder); // strategy order events get persisted too
    }

    @Override
    public IbOrder findIbOrder(Long id) {
        return em.find(IbOrder.class, id);
    }

    @Override
    public IbOrder getIbOrderByIbPermId(IbAccount ibAccount, Integer ibPermId) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.ibAccount = :ibAccount AND o.ibPermId = :ibPermId", IbOrder.class);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("ibPermId", ibPermId);
        List<IbOrder> ibOrders = query.getResultList();
        return (!ibOrders.isEmpty() ? ibOrders.get(0) : null);
    }

    @Override
    public IbOrder getIbOrderByIbOrderId(IbAccount ibAccount, Integer ibOrderId) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.ibAccount = :ibAccount AND o.ibOrderId = :ibOrderId ORDER BY o.dateCreated DESC", IbOrder.class);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("ibOrderId", ibOrderId);
        List<IbOrder> ibOrders = query.getResultList();
        return (!ibOrders.isEmpty() ? ibOrders.get(0) : null);
    }

    @Override
    public List<IbOrder> getIbOrdersByStrategy(Strategy strategy) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.strategy = :strategy ORDER BY o.dateCreated DESC", IbOrder.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }

    @Override
    public List<IbOrder> getNewRetryIbOrders(IbAccount ibAccount) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o, OrderEvent e WHERE o.ibAccount = :ibAccount AND o = e.ibOrder AND o.orderStatus = e.orderStatus AND o.orderStatus IN :statuses ORDER BY e.eventDate ASC", IbOrder.class);
        Set<LinEnums.IbOrderStatus> statuses = new HashSet<>();
        statuses.add(LinEnums.IbOrderStatus.NEW_RETRY);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }

    @Override
    public List<IbOrder> getOpenIbOrders(IbAccount ibAccount) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.ibAccount = :ibAccount AND o.orderStatus IN :statuses AND o.strategyMode = :strategyMode", IbOrder.class);
        Set<LinEnums.IbOrderStatus> statuses = new HashSet<>();
        statuses.add(LinEnums.IbOrderStatus.NEW);
        statuses.add(LinEnums.IbOrderStatus.NEW_RETRY);
        statuses.add(LinEnums.IbOrderStatus.SUBMIT_REQ);
        statuses.add(LinEnums.IbOrderStatus.SUBMITTED);
        statuses.add(LinEnums.IbOrderStatus.CANCEL_REQ);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("statuses", statuses);
        query.setParameter("strategyMode", LinEnums.StrategyMode.IB);
        return query.getResultList();
    }

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
            strategyLog.setLogDate(LinUtil.getCalendar());
            strategy.copyValues(strategyLog);
            em.persist(strategyLog);
        }
    }

    @Override
    public void deleteStrategy(Strategy strategy) {
        l.info("START deleteStrategy " + strategy.getSeries().getSymbol() + ", " + strategy.getSeries().getInterval().getDisplayName() + ", " + strategy.getStrategyType().getDisplayName());
        strategy = em.find(Strategy.class, strategy.getId());
        Query q;
        for (Trade trade : this.getTradesByStrategy(strategy, true)) {
            trade.getTradeOrders().forEach(em::remove);
            q = em.createQuery("DELETE FROM TradeLog tl WHERE tl.trade = :trade");
            q.setParameter("trade", trade);
            q.executeUpdate();
            em.remove(trade);
        }
        q = em.createQuery("DELETE FROM StrategyLog sl WHERE sl.strategy = :strategy");
        q.setParameter("strategy", strategy);
        q.executeUpdate();
        for (IbOrder ibOrder : this.getIbOrdersByStrategy(strategy)) {
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
        Set<LinEnums.TradeStatus> statuses = new HashSet<>();
        statuses.add(LinEnums.TradeStatus.INIT_OPEN);
        statuses.add(LinEnums.TradeStatus.OPEN);
        statuses.add(LinEnums.TradeStatus.INIT_CLOSE);
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

    private void addTrade(Trade trade, Double price) {
        em.persist(trade); // trade orders get persisted too
        TradeLog tradeLog = new TradeLog();
        tradeLog.setTrade(trade);
        tradeLog.setLogDate(LinUtil.getCalendar());
        trade.copyValues(tradeLog);
        tradeLog.setPrice(price);
        em.persist(tradeLog);
    }

    @Override
    public void updateTrade(Trade trade, Double price) {
        if (trade.isNew()) {
           addTrade(trade, price);
           return;
        }
        Trade dbTrade = em.find(Trade.class, trade.getId());
        em.detach(dbTrade);
        em.merge(trade); // trade orders get persisted too
        if (!dbTrade.valuesEqual(trade)) {
            TradeLog tradeLog = new TradeLog();
            tradeLog.setTrade(trade);
            tradeLog.setLogDate(LinUtil.getCalendar());
            trade.copyValues(tradeLog);
            tradeLog.setPrice(price);
            em.persist(tradeLog);
        }
    }

    @Override
    public List<TradeLog> getTradeLogs(Trade trade, boolean ascending) {
        TypedQuery<TradeLog> query = em.createQuery("SELECT l FROM TradeLog l WHERE l.trade = :trade ORDER BY l.logDate " + (ascending ? "ASC" : "DESC"), TradeLog.class);
        query.setParameter("trade", trade);
        return query.getResultList();
    }
}
