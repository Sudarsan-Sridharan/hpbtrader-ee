package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface IbOrderDao {
    void createIbOrder(IbOrder ibOrder);
    void updateIbOrder(IbOrder ibOrder);
    IbOrder findIbOrder(Long id);
    IbOrder getIbOrderByIbPermId(IbAccount ibAccount, Integer ibPermId);
    IbOrder getIbOrderByIbOrderId(IbAccount ibAccount, Integer ibOrderId);
    List<IbOrder> getIbOrders(Strategy strategy);
    List<IbOrder> getNewRetryIbOrders(IbAccount ibAccount);
    List<IbOrder> getOpenIbOrders(IbAccount ibAccount);
    List<IbOrder> getPagedIbOrders(Strategy strategy, int start, int limit);
    Long getNumIbOrders(Strategy strategy);
}
