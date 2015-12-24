package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
@Stateless
public class IbAccountDaoImpl implements IbAccountDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

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
}