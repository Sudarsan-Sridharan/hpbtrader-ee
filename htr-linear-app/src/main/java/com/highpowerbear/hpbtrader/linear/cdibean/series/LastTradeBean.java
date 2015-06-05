package com.highpowerbear.hpbtrader.linear.cdibean.series;

import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.entity.TradeLog;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Named
@SessionScoped
public class LastTradeBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);

    @Inject private SeriesBean seriesBean;
    @Inject private DatabaseDao databaseDao;
    
    private Trade trade;
    private List<Trade> tradeList = new ArrayList<>(); // one item list, just to accomodate showing in the table
    private List<TradeLog> tradeLogs = new ArrayList<>();
    private Strategy strategy;

    public void init() {
        l.fine("LastTradeBean init");
        strategy = seriesBean.getSelectedSeriesRecord().getSeries().getActiveStrategy();
        trade = databaseDao.getLastTrade(strategy);
        tradeList.clear();
        if (trade != null) {
            tradeList.add(trade);
        }
        tradeLogs.clear();
        if (trade != null) {
            tradeLogs = databaseDao.getTradeLogs(trade, false);
        }
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Trade getTrade() {
        return trade;
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public List<TradeLog> getTradeLogs() {
        return tradeLogs;
    }
}
