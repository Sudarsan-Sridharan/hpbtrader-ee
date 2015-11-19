package com.highpowerbear.hpbtrader.linear.persistence;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.*;
import java.util.List;

/**
 * Created by rkolar on 4/14/14.
 */
public interface LinDao {
    IbAccount findIbAccount(String accountId);
    List<IbAccount> getIbAccounts();
    IbAccount updateIbAccount(IbAccount ibAccount);

    void createBars(List<Bar> bars);
    List<Bar> getBars(Integer seriesId, Integer numBars);
    Bar getLastBar(Series series);
    Long getNumBars(Series series);
    void addSeries(Series series);

    List<Series> getAllSeries(boolean disabledToo);
    List<Series> getSeriesByInterval(LinEnums.Interval interval);
    List<Series> getSeries(String symbol, LinEnums.Interval interval);
    Series findSeries(Integer id);
    void updateSeries(Series series);
    Integer getHighestDisplayOrder();
    void deleteSeries(Series series);

    void createIbOrder(IbOrder ibOrder);
    void updateIbOrder(IbOrder ibOrder);
    IbOrder findIbOrder(Long id);
    IbOrder getIbOrderByIbPermId(IbAccount ibAccount, Integer ibPermId);
    IbOrder getIbOrderByIbOrderId(IbAccount ibAccount, Integer ibOrderId);
    List<IbOrder> getIbOrdersByStrategy(Strategy strategy);
    List<IbOrder> getNewRetryIbOrders(IbAccount ibAccount);
    List<IbOrder> getOpenIbOrders(IbAccount ibAccount);

    void createStrategy(Strategy strategy);
    Strategy findStrategy(Integer id);
    Strategy getActiveStrategy(Series series);
    void updateStrategy(Strategy strategy);
    void deleteStrategy(Strategy strategy);
    List<StrategyLog> getStrategyLogs(Strategy strategy, boolean ascending);

    List<Trade> getTradesByStrategy(Strategy strategy, boolean ascending);
    List<Trade> getTradesByOrder(IbOrder ibOrder);
    Long getNumTrades(Strategy strategy);
    Trade getActiveTrade(Strategy strategy);
    Trade getLastTrade(Strategy strategy);
    void updateTrade(Trade trade, Double price);
    List<TradeLog> getTradeLogs(Trade trade, boolean ascending);
}
