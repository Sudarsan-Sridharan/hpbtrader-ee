package com.highpowerbear.hpbtrader.linear.cdibean;

import com.highpowerbear.hpbtrader.linear.cdibean.tab.ChartParams;
import com.highpowerbear.hpbtrader.linear.cdibean.tab.TabBean;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.TradeLog;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import org.primefaces.event.SelectEvent;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Named
@SessionScoped
public class TabController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private StrategyController strategyController;
    @Inject private DatabaseDao databaseDao;
    @Inject private TabBean tabBean;

    public String show(Strategy strategy) {
        tabBean.setIsBacktest(false);
        tabBean.setSeries(strategy.getSeries());
        tabBean.setStrategy(strategy);
        tabBean.getStrategyList().clear();
        tabBean.getStrategyList().add(tabBean.getStrategy());
        tabBean.setStrategyLogs(databaseDao.getStrategyLogs(tabBean.getStrategy(), false));
        tabBean.setOrders(databaseDao.getOrdersByStrategy(tabBean.getStrategy()));
        tabBean.setTrades(databaseDao.getTradesByStrategy(tabBean.getStrategy(), false));
        tabBean.setSelectedTrade(null);
        tabBean.getSelectedTradeOrders().clear();
        tabBean.getSelectedTradeLogs().clear();
        tabBean.setChartParams(new ChartParams(tabBean.getSeries()));
        tabBean.getChartParams().setEma1Period(20);
        tabBean.getChartParams().setEma2Period(50);
        tabBean.getChartParams().setNumBars(500);
        List<Quote> quotes = databaseDao.getQuotes(tabBean.getSeries().getId(), tabBean.getChartParams().getNumBars());
        Collections.reverse(quotes);
        tabBean.setQuotes(quotes);
        return "tab";
    }
    
    public String backtest(Strategy strategy, Calendar startDate, Calendar endDate) {
        tabBean.setIsBacktest(true);
        tabBean.setSeries(strategy.getSeries());
        tabBean.setBacktestResult(strategyController.backtest(strategy, startDate, endDate));
        tabBean.setStrategy(tabBean.getBacktestResult().getStrategy());
        tabBean.getStrategyList().clear();
        tabBean.getStrategyList().add(tabBean.getStrategy());
        tabBean.setStrategyLogs(tabBean.getBacktestResult().getStrategyLogs());
        tabBean.setOrders(tabBean.getBacktestResult().getOrders());
        tabBean.setTrades(tabBean.getBacktestResult().getTrades());
        tabBean.setSelectedTrade(null);
        tabBean.getSelectedTradeOrders().clear();
        tabBean.getSelectedTradeLogs().clear();
        tabBean.setChartParams(new ChartParams(tabBean.getSeries()));
        tabBean.getChartParams().setEma1Period(20);
        tabBean.getChartParams().setEma2Period(50);
        tabBean.getChartParams().setNumBars(500);
        tabBean.setQuotes(databaseDao.getQuotes(tabBean.getSeries().getId(), tabBean.getChartParams().getNumBars()));
        return "tab";
    }
    
    public void onTradeSelect(SelectEvent event) {
        if (tabBean.getSelectedTrade() != null) {
            tabBean.setSelectedTradeOrders(tabBean.getSelectedTrade().getTradeOrders());
            if (tabBean.getIsBacktest()) {
                tabBean.getSelectedTradeLogs().clear();
                for (TradeLog stl : tabBean.getBacktestResult().getTradeLogs()) {
                    if (stl.getTrade().equals(tabBean.getSelectedTrade())) {
                        tabBean.getSelectedTradeLogs().add(stl);
                    }
                }
            } else {
                tabBean.setSelectedTradeLogs(databaseDao.getTradeLogs(tabBean.getSelectedTrade(), false));
            }
        }
    }
    
    public void refreshStrategy() {
        if (!tabBean.getIsBacktest()) {
            tabBean.setStrategyLogs(databaseDao.getStrategyLogs(tabBean.getStrategy(), false));
        }
    }
    
    public void refreshOrders() {
        if (!tabBean.getIsBacktest()) {
            tabBean.setOrders(databaseDao.getOrdersByStrategy(tabBean.getStrategy()));
        }
    }
    
    public void refreshTrades() {
        if (!tabBean.getIsBacktest()) {
            tabBean.setTrades(databaseDao.getTradesByStrategy(tabBean.getStrategy(), false));
            if (tabBean.getSelectedTrade() != null) {
                tabBean.setSelectedTradeOrders(tabBean.getSelectedTrade().getTradeOrders());
                tabBean.setSelectedTradeLogs(databaseDao.getTradeLogs(tabBean.getSelectedTrade(), false));
            }
        }
    }
    
    public void refreshQuotes() {
        tabBean.setQuotes(databaseDao.getQuotes(tabBean.getSeries().getId(), tabBean.getChartParams().getNumBars()));
        if (!tabBean.getIsBacktest()) {
             Collections.reverse(tabBean.getQuotes());
        }
    }
}