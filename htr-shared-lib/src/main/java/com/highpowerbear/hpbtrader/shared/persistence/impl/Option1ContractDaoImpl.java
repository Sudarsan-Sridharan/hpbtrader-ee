package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.OptionContract;
import com.highpowerbear.hpbtrader.shared.persistence.OptionContractDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by robertk on 24.12.2015.
 */
public class Option1ContractDaoImpl implements OptionContractDao {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createOptionContract(OptionContract optionContract) {
        em.merge(optionContract);
    }

    @Override
    public List<OptionContract> getOptionContracts(Set<String> underlyings) {
        Calendar yesterday = HtrUtil.getYesterdayCalendar();
        TypedQuery<OptionContract> query = em.createQuery("SELECT oc FROM OptionContract oc WHERE oc.expiry > :yesterday AND oc.underlying IN :underlyings ORDER BY oc.expiry, oc.underlying, oc.optionType, oc.strike", OptionContract.class);
        query.setParameter("underlyings", underlyings);
        query.setParameter("yesterday", yesterday);
        return query.getResultList();
    }

    @Override
    public String getCallSymbol(String underlying, Calendar minExpiry, Double maxStrike) {
        TypedQuery<String> query = em.createQuery("SELECT oc.optionSymbol FROM OptionContract oc WHERE oc.optionType = :optionType AND oc.underlying = :underlying AND oc.expiry >= :minExpiry AND oc.strike <= :maxStrike ORDER BY oc.expiry ASC, oc.strike DESC", String.class);
        query.setParameter("optionType", HtrEnums.OptionType.CALL);
        query.setParameter("underlying",underlying);
        query.setParameter("minExpiry",minExpiry);
        query.setParameter("maxStrike",maxStrike);
        List<String> list = query.getResultList();
        return (list.size() > 0 ? list.get(0) : null);
    }

    @Override
    public String getPutSymbol(String underlying, Calendar minExpiry, Double minStrike) {
        TypedQuery<String> query = em.createQuery("SELECT oc.optionSymbol FROM OptionContract oc WHERE oc.optionType = :optionType AND oc.underlying = :underlying AND oc.expiry >= :minExpiry AND oc.strike >= :minStrike ORDER BY oc.expiry ASC, oc.strike ASC", String.class);
        query.setParameter("optionType", HtrEnums.OptionType.PUT);
        query.setParameter("underlying",underlying);
        query.setParameter("minExpiry",minExpiry);
        query.setParameter("minStrike",minStrike);
        List<String> list = query.getResultList();
        return (list.size() > 0 ? list.get(0) : null);
    }
}
