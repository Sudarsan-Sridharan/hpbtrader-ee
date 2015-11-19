package com.highpowerbear.hpbtrader.shared.persistence.impl;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 19.11.2015.
 */
public class IbOrderDaoImpl implements IbOrderDao {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void createIbOrder(IbOrder ibOrder) {

    }

    @Override
    public void updateIbOrder(IbOrder ibOrder) {

    }

    @Override
    public IbOrder findIbOrder(Long id) {
        return null;
    }

    @Override
    public IbOrder getIbOrderByIbPermId(IbAccount ibAccount, Integer ibPermId) {
        return null;
    }

    @Override
    public IbOrder getIbOrderByIbOrderId(IbAccount ibAccount, Integer ibOrderId) {
        return null;
    }

    @Override
    public List<IbOrder> getIbOrdersByStrategy(Strategy strategy) {
        return null;
    }

    @Override
    public List<IbOrder> getNewRetryIbOrders(IbAccount ibAccount) {
        return null;
    }

    @Override
    public List<IbOrder> getOpenIbOrders(IbAccount ibAccount) {
        return null;
    }
}
