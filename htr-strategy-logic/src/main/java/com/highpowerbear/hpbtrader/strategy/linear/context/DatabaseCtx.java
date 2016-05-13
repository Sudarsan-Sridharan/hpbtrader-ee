package com.highpowerbear.hpbtrader.strategy.linear.context;

import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.strategy.common.SingletonRepo;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;

import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 5/13/2016.
 */
public class DatabaseCtx implements ProcessContext {

    private Strategy strategy;

    private TradeDao tradeDao = SingletonRepo.getInstance().getTradeDao();
    private StrategyDao strategyDao = SingletonRepo.getInstance().getStrategyDao();
    private IbOrderDao ibOrderDao = SingletonRepo.getInstance().getIbOrderDao();

    public DatabaseCtx(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void setCurrentDate(Calendar currentDate) {
        // ignore
    }

    @Override
    public Calendar getCurrentDate() {
        return HtrUtil.getCalendar();
    }

    @Override
    public Strategy getStrategy() {
        return this.strategy;
    }

    @Override
    public void updateStrategy() {
        strategyDao.updateStrategy(this.strategy);
    }

    @Override
    public Trade getActiveTrade() {
        return tradeDao.getActiveTrade(this.strategy);
    }

    @Override
    public void updateOrCreateTrade(Trade trade, Double currentPrice) {
        tradeDao.updateOrCreateTrade(trade, this.getCurrentDate(), currentPrice);
    }

    @Override
    public Trade findTrade(Long id) {
        return tradeDao.findTrade(id);
    }

    @Override
    public List<Trade> getTradesByOrder(IbOrder ibOrder) {
        return tradeDao.getTradesByOrder(ibOrder);
    }

    @Override
    public void createIbOrder(IbOrder ibOrder) {
        ibOrderDao.createIbOrder(ibOrder);
    }

    @Override
    public void updateIbOrder(IbOrder ibOrder) {
        ibOrderDao.updateIbOrder(ibOrder);
    }

    @Override
    public IbOrder findIbOrder(IbOrder ibOrder) {
        return ibOrderDao.findIbOrder(ibOrder.getId()); // refresh from db
    }

    @Override
    public List<StrategyLog> getPagedStrategyLogs() {
        // TODO
        return null;
    }

    @Override
    public List<IbOrder> getPagedIbOrders() {
        // TODO
        return null;
    }

    @Override
    public List<Trade> getPagedTrades() {
        // TODO
        return null;
    }

    @Override
    public List<TradeLog> getPagedTradeLogs() {
        // TODO
        return null;
    }
}