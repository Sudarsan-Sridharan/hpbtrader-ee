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
public class DatabaseDaoImpl implements Serializable, DatabaseDao {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void addQuotes(List<Quote> quotes) {
        if (quotes == null || quotes.isEmpty()) {
            return;
        }
        String symbol = quotes.iterator().next().getSeries().getSymbol();
        l.fine("START addQuotes, symbol=" + symbol);
        int added = 0;
        int modified = 0;
        for (Quote q : quotes) {
            TypedQuery<Quote> query = em.createQuery("SELECT q FROM Quote q WHERE q.series = :series AND q.qDateBarClose = :qDateBarClose", Quote.class);
            query.setParameter("series", q.getSeries());
            query.setParameter("qDateBarClose", q.getqDateBarClose());
            List<Quote> ql = query.getResultList();
            Quote dbQuote = (ql != null && !ql.isEmpty() ? ql.get(0) : null);
            if (dbQuote == null) {
                // insert
                l.fine("Adding " + q.printValues());
                added++;
                em.persist(q);
            } else {
                // update
                if (!dbQuote.valuesEqual(q)) {
                    l.fine(dbQuote.printValues() + " --> " + q.printValues());
                    dbQuote.copyValuesFrom(q);
                    modified++;
                    em.merge(dbQuote);
                }
            }
        }
        em.flush();
        em.clear();
        l.fine("END addQuotes, symbol=" + symbol + ", added=" + added + ", modified=" + modified);
    }

    @Override
    public List<Quote> getQuotes(Integer seriesId, Integer numQuotes) {
        TypedQuery<Quote> query = em.createQuery("SELECT q FROM Quote q WHERE q.series.id = :seriesId ORDER BY q.qDateBarClose DESC", Quote.class);
        query.setParameter("seriesId", seriesId);
        if (numQuotes != null && numQuotes > 0) {
            query.setMaxResults(numQuotes);
        }
        List<Quote> quotes = query.getResultList();
        em.flush();
        em.clear();
        if (quotes == null) {
            quotes = new ArrayList<>();
        }
        Collections.reverse(quotes);
        return quotes;
    }

    @Override
    public Quote getLastQuote(Series series) {
        TypedQuery<Quote> query = em.createQuery("SELECT q FROM Quote q WHERE q.series = :series ORDER BY q.qDateBarClose DESC", Quote.class);
        query.setParameter("series", series);
        query.setMaxResults(1);
        List<Quote> quotes = query.getResultList();
        em.flush();
        em.clear();
        return (quotes == null || quotes.isEmpty() ? null : quotes.get(0));
    }

    @Override
    public Long getNumQuotes(Series s) {
        Query query = em.createQuery("SELECT COUNT(q) FROM Quote q WHERE q.series.id = :seriesId");
        query.setParameter("seriesId", s.getId());
        Long nq = (Long) query.getSingleResult();
        em.flush();
        em.clear();
        return nq;
    }

    @Override
    public boolean addSeries(Series series) {
        boolean sucess = true;
        try {
            em.persist(series);
            em.flush();
            em.clear();
        } catch (PersistenceException pe) {
            sucess = false;
        }
        return sucess;
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
        List<Series> sl = q.getResultList();
        em.flush();
        em.clear();
        return sl;
    }

    @Override
    public List<Series> getSeriesByInterval(LinEnums.Interval interval) {
        TypedQuery<Series> query = em.createQuery("SELECT s FROM Series s WHERE s.interval = :interval", Series.class);
        query.setParameter("interval", interval);
        List<Series> series = query.getResultList();
        em.flush();
        em.clear();
        return series;
    }

    @Override
    public List<Series> getSeries(String symbol, LinEnums.Interval interval) {
        TypedQuery<Series> query = em.createQuery("SELECT s FROM Series s WHERE s.symbol = :symbol AND s.interval = :interval", Series.class);
        query.setParameter("symbol", symbol);
        query.setParameter("interval", interval);
        List<Series> series = query.getResultList();
        em.flush();
        em.clear();
        return series;
    }

    @Override
    public Series findSeries(Integer id) {
        Series s = em.find(Series.class, id);
        em.flush();
        em.clear();
        return s;
    }

    @Override
    public void updateSeries(Series series) {
        em.merge(series);
        em.flush();
        em.clear();
    }

    @Override
    public Integer getHighestDisplayOrder() {
        Query query = em.createQuery("SELECT MAX(s.displayOrder) from Series s");
        Integer highestDisplayOrder = (Integer) query.getSingleResult();
        em.flush();
        em.clear();
        return highestDisplayOrder;
    }

    @Override
    public void deleteSeries(Series series) {
        l.info("START deleteSeries " + series.getSymbol());
        series = em.find(Series.class, series.getId()); // make sure it is managed by entitymanager
        for (Strategy strategy : series.getStrategies()) {
            this.deleteStrategy(strategy);
        }
        Query q = em.createQuery("DELETE FROM Quote q WHERE q.series = :series");
        q.setParameter("series", series);
        q.executeUpdate();
        em.remove(series);
        l.info("END deleteSeries " + series.getSymbol());
        em.flush();
        em.clear();
    }

    @Override
    public void addOrder(Order order) {
        em.persist(order); // strategy order events get persisted too
        em.flush();
        em.clear();
    }

    @Override
    public void updateOrder(Order order) {
        em.merge(order); // strategy order events get persisted too
        em.flush();
        em.clear();
    }

    @Override
    public Order findOrder(Long id) {
        Order order = em.find(Order.class, id);
        em.flush();
        em.clear();
        return order;
    }

    @Override
    public Order getOrderByIbPermId(Integer ibPermId) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.ibPermId = :ibPermId", Order.class);
        query.setParameter("ibPermId", ibPermId);
        List<Order> orders = query.getResultList();
        em.flush();
        em.clear();
        return (!orders.isEmpty() ? orders.get(0) : null);
    }

    @Override
    public Order getOrderByIbOrderId(Integer ibOrderId) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.ibOrderId = :ibOrderId ORDER BY o.dateCreated DESC", Order.class);
        query.setParameter("ibOrderId", ibOrderId);
        List<Order> orders = query.getResultList();
        em.flush();
        em.clear();
        return (!orders.isEmpty() ? orders.get(0) : null);
    }

    @Override
    public List<Order> getOrdersByStrategy(Strategy strategy) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.strategy = :strategy ORDER BY o.dateCreated DESC", Order.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }

    @Override
    public List<Order> getRecentOrders() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o, OrderEvent e WHERE o = e.order AND o.orderStatus = e.orderStatus AND (o.orderStatus IN :statuses OR e.eventDate > :recentDate) ORDER BY e.eventDate DESC", Order.class);
        Set<LinEnums.OrderStatus> statuses = new HashSet<>();
        statuses.add(LinEnums.OrderStatus.NEW);
        statuses.add(LinEnums.OrderStatus.NEW_RETRY);
        statuses.add(LinEnums.OrderStatus.SUBMIT_REQ);
        statuses.add(LinEnums.OrderStatus.SUBMITTED);
        statuses.add(LinEnums.OrderStatus.CANCEL_REQ);
        query.setParameter("statuses", statuses);
        Calendar recentDate = LinUtil.getCalendar();
        recentDate.add(Calendar.DAY_OF_MONTH, -LinSettings.RECENT_ORDER_DAYS);
        query.setParameter("recentDate", recentDate);
        List<Order> orders = query.getResultList();
        em.flush();
        em.clear();
        return orders;
    }

    @Override
    public List<Order> getNewRetryOrders() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o, OrderEvent e WHERE o = e.order AND o.orderStatus = e.orderStatus AND o.orderStatus IN :statuses ORDER BY e.eventDate ASC", Order.class);
        Set<LinEnums.OrderStatus> statuses = new HashSet<>();
        statuses.add(LinEnums.OrderStatus.NEW_RETRY);
        query.setParameter("statuses", statuses);
        List<Order> orders = query.getResultList();
        em.flush();
        em.clear();
        return orders;
    }

    @Override
    public List<Order> getIbOpenOrders() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.orderStatus IN :statuses AND o.strategyMode = :strategyMode", Order.class);
        Set<LinEnums.OrderStatus> statuses = new HashSet<>();
        statuses.add(LinEnums.OrderStatus.NEW);
        statuses.add(LinEnums.OrderStatus.NEW_RETRY);
        statuses.add(LinEnums.OrderStatus.SUBMIT_REQ);
        statuses.add(LinEnums.OrderStatus.SUBMITTED);
        statuses.add(LinEnums.OrderStatus.CANCEL_REQ);
        query.setParameter("statuses", statuses);
        query.setParameter("strategyMode", LinEnums.StrategyMode.IB);
        List<Order> orders = query.getResultList();
        em.flush();
        em.clear();
        return orders;
    }

    @Override
    public void addStrategy(Strategy strategy) {
        em.persist(strategy);
        em.flush();
        em.clear();
    }

    @Override
    public Strategy findStrategy(Integer id) {
        Strategy str = em.find(Strategy.class, id);
        em.flush();
        em.clear();
        return str;
    }

    @Override
    public Strategy getActiveStrategy(Series series) {
        TypedQuery<Strategy> query = em.createQuery("SELECT str FROM Strategy str WHERE str.series = :series AND str.isActive = :isActive", Strategy.class);
        query.setParameter("series", series);
        query.setParameter("isActive", Boolean.TRUE);
        List<Strategy> strategyList = query.getResultList();
        Strategy strategy = strategyList.get(0);
        em.flush();
        em.clear();
        return strategy;
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
        em.flush();
        em.clear();
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
        for (Order order : this.getOrdersByStrategy(strategy)) {
            order.getEvents().forEach(em::remove);
            em.remove(order);
        }
        em.remove(strategy);
        l.info("END deleteStrategy " + strategy.getSeries().getSymbol() + ", " + strategy.getSeries().getInterval().getDisplayName() + ", " + strategy.getStrategyType().getDisplayName());
    }

    @Override
    public List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending) {
        TypedQuery<StrategyLog> query = em.createQuery("SELECT sl FROM StrategyLog sl WHERE sl.strategy = :strategy ORDER BY sl.logDate " + (ascending ? "ASC" : "DESC"), StrategyLog.class);
        query.setParameter("strategy", strategy);
        List<StrategyLog> logs = query.getResultList();
        em.flush();
        em.clear();
        return logs;
    }

    @Override
    public List<Trade> getTradesByStrategy(Strategy strategy, boolean ascending) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.dateInitOpen " + (ascending ? "ASC" : "DESC"), Trade.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }

    @Override
    public List<Trade> getTradesByOrder(Order order) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t, TradeOrder to WHERE to.order = :order AND to.trade = t ORDER BY t.dateInitOpen ASC", Trade.class);
        query.setParameter("order", order);
        List<Trade> trades = query.getResultList();
        em.flush();
        em.clear();
        return trades;
    }

    @Override
    public Long getNumTrades(Strategy strategy) {
        Query query = em.createQuery("SELECT COUNT(t) FROM Trade t WHERE t.strategy = :strategy");
        query.setParameter("strategy", strategy);
        Long nt = (Long) query.getSingleResult();
        em.flush();
        em.clear();
        return nt;
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
        em.flush();
        em.clear();
        return (trades != null && !trades.isEmpty() ? trades.get(0) : null);
    }

    @Override
    public Trade getLastTrade(Strategy strategy) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t WHERE t.strategy = :strategy ORDER BY t.dateInitOpen DESC", Trade.class);
        query.setParameter("strategy", strategy);
        List<Trade> trades = query.getResultList();
        em.flush();
        em.clear();
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
        em.flush();
        em.clear();
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
        em.flush();
        em.clear();
        
    }

    @Override
    public List<TradeLog> getTradeLogs(Trade trade, boolean ascending) {
        TypedQuery<TradeLog> query = em.createQuery("SELECT l FROM TradeLog l WHERE l.trade = :trade ORDER BY l.logDate " + (ascending ? "ASC" : "DESC"), TradeLog.class);
        query.setParameter("trade", trade);
        List<TradeLog> logs = query.getResultList();
        em.flush();
        em.clear();
        return logs;
    }
}
