package com.highpowerbear.hpbtrader.strategy.process.context;

import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.strategy.process.ProcessContext;
import com.highpowerbear.hpbtrader.strategy.websocket.WebsocketController;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 5/13/2016.
 */
@Dependent
public class DatabaseCtx implements ProcessContext {

    @Inject private TradeDao tradeDao;
    @Inject private StrategyDao strategyDao;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private WebsocketController websocketController;

    private Strategy strategy;

    @Override
    public ProcessContext configure(Strategy strategy) {
        this.strategy = strategy;
        return this;
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
        websocketController.notifyStrategyUpdated(strategy);
    }

    @Override
    public Trade getActiveTrade() {
        return tradeDao.getActiveTrade(this.strategy);
    }

    @Override
    public void updateOrCreateTrade(Trade trade, Double currentPrice) {
        tradeDao.updateOrCreateTrade(trade, this.getCurrentDate(), currentPrice);
        websocketController.notifyTradeUpdatedOrCreated(trade);
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
        websocketController.notifyIbOrderUpdatedOrCreated(ibOrder);
    }

    @Override
    public void updateIbOrder(IbOrder ibOrder) {
        ibOrderDao.updateIbOrder(ibOrder);
    }

    @Override
    public List<StrategyPerformance> getStrategyPerformances(int start, int limit) {
        return strategyDao.getPagedStrategyLogs(this.strategy, start, limit);
    }

    @Override
    public List<IbOrder> getIbOrders(int start, int limit) {
        return ibOrderDao.getPagedIbOrders(this.strategy, start, limit);
    }

    @Override
    public List<Trade> getTrades(int start, int limit) {
        return tradeDao.getPagedTrades(this.strategy, start, limit);
    }

    @Override
    public List<TradeLog> getTradeLogs(Trade trade, int start, int limit) {
        return tradeDao.getPagedTradeLogs(trade, start, limit);
    }

    @Override
    public Long getNumStrategyPerformances() {
        return strategyDao.getNumStrategyLogs(this.strategy);
    }

    @Override
    public Long getNumIbOrders() {
        return ibOrderDao.getNumIbOrders(this.strategy);
    }

    @Override
    public Long getNumTrades() {
        return tradeDao.getNumTrades(this.strategy);
    }

    @Override
    public Long getNumTradeLogs(Trade trade) {
        return tradeDao.getNumTradeLogs(trade);
    }
}