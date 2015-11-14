package com.highpowerbear.hpbtrader.linear.persistence;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.*;
import java.util.List;

/**
 * Created by rkolar on 4/14/14.
 */
public interface DatabaseDao {
    void addBars(List<Bar> bars);
    List<Bar> getBars(Integer seriesId, Integer numBars);
    Bar getLastBar(Series series);
    Long getNumBars(Series series);
    boolean addSeries(Series series);
    List<Series> getAllSeries(boolean disabledToo);
    List<Series> getSeriesByInterval(LinEnums.Interval interval);
    List<Series> getSeries(String symbol, LinEnums.Interval interval);
    Series findSeries(Integer id);
    void updateSeries(Series series);
    Integer getHighestDisplayOrder();
    void deleteSeries(Series series);
    void addOrder(Order order);
    void updateOrder(Order order);
    Order findOrder(Long id);
    Order getOrderByIbPermId(Integer ibPermId);
    Order getOrderByIbOrderId(Integer ibOrderId);
    List<Order> getOrdersByStrategy(Strategy strategy);
    List<Order> getRecentOrders();
    List<Order> getNewRetryOrders();
    List<Order> getIbOpenOrders();
    void addStrategy(Strategy strategy);
    Strategy findStrategy(Integer id);
    Strategy getActiveStrategy(Series series);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending);
    List<Trade> getTradesByStrategy(Strategy strategy, boolean ascending);
    List<Trade> getTradesByOrder(Order order);
    Long getNumTrades(Strategy strategy);
    Trade getActiveTrade(Strategy strategy);
    Trade getLastTrade(Strategy strategy);
    void updateTrade(Trade trade, Double price);
    List<TradeLog> getTradeLogs(Trade trade, boolean ascending);
}
