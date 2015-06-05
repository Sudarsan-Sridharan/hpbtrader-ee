package com.highpowerbear.hpbtrader.linear.cdibean.tab;

import com.highpowerbear.hpbtrader.linear.entity.*;
import com.highpowerbear.hpbtrader.linear.strategy.model.BacktestResult;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@SessionScoped
public class TabBean implements Serializable {
    private Boolean isBacktest = false;
    private BacktestResult backtestResult;
    private Series series;
    private Strategy strategy;
    private List<Strategy> strategyList = new ArrayList<>(); // one item list, just to accomodate showing in the table
    private ChartParams chartParams;

    // strategy
    private List<StrategyLog> strategyLogs;

    // order
    private List<Order> orders;
    private Order selectedOrder;

    // trade
    private List<Trade> trades;
    private Trade selectedTrade;
    private List<TradeOrder> selectedTradeOrders = new ArrayList<>();
    private List<TradeLog> selectedTradeLogs = new ArrayList<>();

    // quote
    private List<Quote> quotes;

    public Boolean getIsBacktest() {
        return isBacktest;
    }

    public void setIsBacktest(Boolean isBacktest) {
        this.isBacktest = isBacktest;
    }

    public BacktestResult getBacktestResult() {
        return backtestResult;
    }

    public void setBacktestResult(BacktestResult backtestResult) {
        this.backtestResult = backtestResult;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Strategy> getStrategyList() {
        return strategyList;
    }

    public void setStrategyList(List<Strategy> strategyList) {
        this.strategyList = strategyList;
    }

    public ChartParams getChartParams() {
        return chartParams;
    }

    public void setChartParams(ChartParams chartParams) {
        this.chartParams = chartParams;
    }

    public List<StrategyLog> getStrategyLogs() {
        return strategyLogs;
    }

    public void setStrategyLogs(List<StrategyLog> strategyLogs) {
        this.strategyLogs = strategyLogs;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public Trade getSelectedTrade() {
        return selectedTrade;
    }

    public void setSelectedTrade(Trade selectedTrade) {
        this.selectedTrade = selectedTrade;
    }

    public List<TradeOrder> getSelectedTradeOrders() {
        return selectedTradeOrders;
    }

    public void setSelectedTradeOrders(List<TradeOrder> selectedTradeOrders) {
        this.selectedTradeOrders = selectedTradeOrders;
    }

    public List<TradeLog> getSelectedTradeLogs() {
        return selectedTradeLogs;
    }

    public void setSelectedTradeLogs(List<TradeLog> selectedTradeLogs) {
        this.selectedTradeLogs = selectedTradeLogs;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}
