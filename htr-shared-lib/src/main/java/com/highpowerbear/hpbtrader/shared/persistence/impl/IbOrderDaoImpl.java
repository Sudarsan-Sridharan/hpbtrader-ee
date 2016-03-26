package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by robertk on 19.11.2015.
 */
@Stateless
public class IbOrderDaoImpl implements IbOrderDao {

    @PersistenceContext
    private EntityManager em;

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
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.strategy.ibAccount = :ibAccount AND o.ibPermId = :ibPermId", IbOrder.class);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("ibPermId", ibPermId);
        List<IbOrder> ibOrders = query.getResultList();
        return (!ibOrders.isEmpty() ? ibOrders.get(0) : null);
    }

    @Override
    public IbOrder getIbOrderByIbOrderId(IbAccount ibAccount, Integer ibOrderId) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.strategy.ibAccount = :ibAccount AND o.ibOrderId = :ibOrderId ORDER BY o.createdDate DESC", IbOrder.class);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("ibOrderId", ibOrderId);
        List<IbOrder> ibOrders = query.getResultList();
        return (!ibOrders.isEmpty() ? ibOrders.get(0) : null);
    }

    @Override
    public List<IbOrder> getIbOrdersByStrategy(Strategy strategy) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.strategy = :strategy ORDER BY o.createdDate DESC", IbOrder.class);
        query.setParameter("strategy", strategy);
        return query.getResultList();
    }

    @Override
    public List<IbOrder> getNewRetryIbOrders(IbAccount ibAccount) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o, OrderEvent e WHERE o.strategy.ibAccount = :ibAccount AND o = e.ibOrder AND o.status = e.status AND o.status IN :statuses ORDER BY e.eventDate ASC", IbOrder.class);
        Set<HtrEnums.IbOrderStatus> statuses = new HashSet<>();
        statuses.add(HtrEnums.IbOrderStatus.NEW_RETRY);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }

    @Override
    public List<IbOrder> getOpenIbOrders(IbAccount ibAccount) {
        TypedQuery<IbOrder> query = em.createQuery("SELECT o FROM IbOrder o WHERE o.strategy.ibAccount = :ibAccount AND o.status IN :statuses AND o.strategyMode = :strategyMode", IbOrder.class);
        Set<HtrEnums.IbOrderStatus> statuses = new HashSet<>();
        statuses.add(HtrEnums.IbOrderStatus.NEW);
        statuses.add(HtrEnums.IbOrderStatus.NEW_RETRY);
        statuses.add(HtrEnums.IbOrderStatus.SUBMIT_REQ);
        statuses.add(HtrEnums.IbOrderStatus.SUBMITTED);
        statuses.add(HtrEnums.IbOrderStatus.CANCEL_REQ);
        query.setParameter("ibAccount", ibAccount);
        query.setParameter("statuses", statuses);
        query.setParameter("strategyMode", HtrEnums.StrategyMode.IB);
        return query.getResultList();
    }
}
