package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
public class IbAccountDaoImpl implements IbAccountDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public IbAccount findIbAccount(String accountId) {
        return null;
    }

    @Override
    public List<IbAccount> getIbAccounts() {
        return null;
    }

    @Override
    public IbAccount updateIbAccount(IbAccount ibAccount) {
        return null;
    }
}