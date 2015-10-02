package com.highpowerbear.hpbtrader.options.persistence;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.*;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.options.model.Position;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author rkolar
 */
@Stateless
public class OptDaoImpl implements OptDao {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;

    private final String B = "BEGIN " + this.getClass().getSimpleName() + ".";
    private final String E = "END " + this.getClass().getSimpleName() + ".";

    @Override
    public void addOptionContract(OptionContract optionContract) {
        em.merge(optionContract);
    }
    
    @Override
    public List<OptionContract> getOptionContracts(Set<String> underlyings) {
        l.info(B + "getOptionContracts");
        Calendar yesterday = OptUtil.getYesterdayCalendar();
        TypedQuery<OptionContract> query = em.createQuery("SELECT oc FROM OptionContract oc WHERE oc.expiry > :yesterday AND oc.underlying IN :underlyings ORDER BY oc.expiry, oc.underlying, oc.optionType, oc.strike", OptionContract.class);
        query.setParameter("underlyings", underlyings);
        query.setParameter("yesterday", yesterday);
        List<OptionContract> optionContracts = query.getResultList();
        l.info(E + "getOptionContracts, count=" + optionContracts.size());
        return optionContracts;
    }
    
    @Override
    public String getCallSymbol(String underlying, Calendar minExpiry, Double maxStrike) {
        l.info(B + "getCallSymbol, underlying=" + underlying + ", minExpiry=" + OptUtil.toExpiryStringFull(minExpiry) + ", maxStrike" + maxStrike);
        TypedQuery<String> query = em.createQuery("SELECT oc.optionSymbol FROM OptionContract oc WHERE oc.optionType = :optionType AND oc.underlying = :underlying AND oc.expiry >= :minExpiry AND oc.strike <= :maxStrike ORDER BY oc.expiry ASC, oc.strike DESC", String.class);
        query.setParameter("optionType", IbApiEnums.OptionType.CALL);
        query.setParameter("underlying",underlying);
        query.setParameter("minExpiry",minExpiry);
        query.setParameter("maxStrike",maxStrike);
        List<String> list = query.getResultList();
        String callSymbol = (list.size() > 0 ? list.get(0) : null);
        l.info(E + "getCallSymbol, underlying=" + underlying + ", minExpiry=" + OptUtil.toExpiryStringFull(minExpiry) + ", maxStrike" + maxStrike + ", symbol=" + callSymbol);
        return callSymbol;
    }
    
    @Override
    public String getPutSymbol(String underlying, Calendar minExpiry, Double minStrike) {
        l.info(B + "getPutSymbol, underlying=" + underlying + ", minExpiry=" + OptUtil.toExpiryStringFull(minExpiry) + ", maxStrike" + minStrike);
        TypedQuery<String> query = em.createQuery("SELECT oc.optionSymbol FROM OptionContract oc WHERE oc.optionType = :optionType AND oc.underlying = :underlying AND oc.expiry >= :minExpiry AND oc.strike >= :minStrike ORDER BY oc.expiry ASC, oc.strike ASC", String.class);
        query.setParameter("optionType", IbApiEnums.OptionType.PUT);
        query.setParameter("underlying",underlying);
        query.setParameter("minExpiry",minExpiry);
        query.setParameter("minStrike",minStrike);
        List<String> list = query.getResultList();
        String putSymbol = (list.size() > 0 ? list.get(0) : null);
        l.info(B + "getPutSymbol, underlying=" + underlying + ", minExpiry=" + OptUtil.toExpiryStringFull(minExpiry) + ", maxStrike" + minStrike + ", symbol=" + putSymbol);
        return putSymbol;
    }

    @Override
    public void addSignal(InputSentiment inputSentiment) {
        em.persist(inputSentiment);
    }

    @Override
    public void updateSignal(InputSentiment inputSentiment) {
        em.merge(inputSentiment);
    }
    
    @Override
    public boolean existsSignal(Long signalId) {
        return (em.find(InputSentiment.class, signalId) != null);
    }
    
    @Override
    public List<InputSentiment> getSignals(String underlying) {
        l.info(B + "getSignals, underlying=" + underlying);
        TypedQuery<InputSentiment> query;
        if (underlying != null) {
            query = em.createQuery("SELECT s FROM InputSentiment s WHERE s.underlying = :underlying ORDER BY s.sentimentDate DESC", InputSentiment.class);
            query.setParameter("underlying", underlying);
        } else {
           query = em.createQuery("SELECT s FROM InputSentiment s ORDER BY s.sentimentDate DESC", InputSentiment.class);
        }
        query.setMaxResults(OptDefinitions.JPA_MAX_RESULTS);
        List<InputSentiment> inputSentiments = query.getResultList();
        l.info(E + "getSignals, underlying=" + underlying);
        return inputSentiments;
    }

    @Override
    public void addOrder(Order order) {
        l.info(B + "addOrder, order=" + order.print());
        em.persist(order);
        l.info(E + "addOrder, order=" + order.print());
    }
    
    @Override
    public void updateOrder(Order order) {
        em.merge(order);
    }

    @Override
    public Order getOrder(Long id) {
        return em.find(Order.class, id);
    }
    
    
    @Override
    public Order getOrderByIbPermId(Integer ibPermId) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.ibPermId = :ibPermId", Order.class);
        query.setParameter("ibPermId", ibPermId);
        List<Order> list = query.getResultList();
        return (list.size() > 0 ? list.get(0) : null);
    }
    
    @Override
    public Order getOrderByIbOrderId(Integer ibOrderId) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.ibOrderId = :ibOrderId ORDER BY o.dateCreated DESC", Order.class);
        query.setParameter("ibOrderId", ibOrderId);
        List<Order> list = query.getResultList();
        return (list.size() > 0 ? list.get(0) : null);
    }

    @Override
    public List<Order> getOrders(String underlying) {
        TypedQuery<Order> query;
        if (underlying != null) {
            query = em.createQuery("SELECT o FROM Order o WHERE o.inputSignal.underlying = :underlying ORDER BY o.dateCreated DESC", Order.class);
            query.setParameter("underlying", underlying);
        } else {
            query = em.createQuery("SELECT o FROM Order o ORDER BY o.dateCreated DESC", Order.class);
        }
        query.setMaxResults(OptDefinitions.JPA_MAX_RESULTS);
        return query.getResultList();
    }

    @Override
    public List<Order> getOrdersBySignalId(Long signalId) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.inputSignal.id = :signalId ORDER BY o.dateCreated, o.id", Order.class);
        query.setParameter("signalId", signalId);
        return query.getResultList();
    }
    
    @Override
    public List<Order> getNewRetryOrders() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o, OrderEvent evt WHERE o = evt.ibOrder AND o.orderStatus = evt.orderStatus AND o.orderStatus IN :statuses ORDER BY evt.eventDate ASC", Order.class);
        Set<OptEnums.OrderStatus> statuses = new HashSet<>();
        statuses.add(OptEnums.OrderStatus.NEW_RETRY);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }

    @Override
    public List<Order> getOpenOrders() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.orderStatus IN :statuses ORDER BY o.dateCreated DESC", Order.class);
        Set<OptEnums.OrderStatus> statuses = new HashSet<>();
        statuses.add(OptEnums.OrderStatus.NEW);
        statuses.add(OptEnums.OrderStatus.NEW_RETRY);
        statuses.add(OptEnums.OrderStatus.SUBMIT_REQ);
        statuses.add(OptEnums.OrderStatus.SUBMITTED);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }
    
    @Override
    public void addTrade(Trade trade) {
        em.persist(trade);
    }
    
    @Override
    public void updateTrade(Trade trade) {
        em.merge(trade);
    }

    @Override
    public List<Trade> getTrades(String underlying) {
        TypedQuery<Trade> query;
        if (underlying != null) {
            query = em.createQuery("SELECT t FROM Trade t WHERE t.underlying = :underlying ORDER BY t.dateInitOpen DESC", Trade.class);
            query.setParameter("underlying", underlying);
        } else {
            query = em.createQuery("SELECT t FROM Trade t ORDER BY t.dateInitOpen DESC", Trade.class);
        }
        query.setMaxResults(OptDefinitions.JPA_MAX_RESULTS);
        return query.getResultList();
    }
    
    @Override
    public List<Trade> getActiveTrades(String underlying) {
        TypedQuery<Trade> query;
        if (underlying == null) {
            query = em.createQuery("SELECT t FROM Trade t WHERE t.tradeStatus IN :statuses ORDER BY t.dateInitOpen DESC, t.id DESC", Trade.class);
        } else {
            query = em.createQuery("SELECT t FROM Trade t WHERE t.underlying = :underlying AND t.tradeStatus IN :statuses ORDER BY t.dateInitOpen DESC, t.id DESC", Trade.class);
            query.setParameter("underlying", underlying);
        }
        Set<OptEnums.TradeStatus> statuses = new HashSet<>();
        statuses.add(OptEnums.TradeStatus.INIT_OPEN);
        statuses.add(OptEnums.TradeStatus.OPEN);
        statuses.add(OptEnums.TradeStatus.INIT_CLOSE);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }
    
    @Override
    public Trade getActiveTrade(String underlying, IbApiEnums.OptionType optionType) {
        List<Trade> activeTrades = getActiveTrades(underlying);
        Trade activeTrade = null;
        for (Trade t : activeTrades) {
            if (t.getOptionType().equals(optionType)) {
                activeTrade = t;
            }
        }
        return activeTrade;
    }
    
    @Override
    public List<Position> getPosition(String underlying) {
        TypedQuery<Trade> query = em.createQuery("SELECT t FROM Trade t WHERE t.underlying = :underlying AND t.tradeStatus IN :statuses ORDER BY t.dateInitOpen ASC", Trade.class);
        query.setParameter("underlying", underlying);
        Set<OptEnums.TradeStatus> statuses = new HashSet<>();
        statuses.add(OptEnums.TradeStatus.OPEN);
        statuses.add(OptEnums.TradeStatus.INIT_CLOSE);
        query.setParameter("statuses", statuses);
        return query.getResultList().stream().map(t -> new Position(t.getOptionSymbol(), t.getCurrentPosition())).collect(Collectors.toList());
    }
}
