package com.highpowerbear.hpbtrader.linear.rest;

import com.highpowerbear.hpbtrader.linear.cdibean.series.LastTradeBean;
import com.highpowerbear.hpbtrader.linear.cdibean.series.SeriesBean;
import com.highpowerbear.hpbtrader.linear.cdibean.tab.ChartParams;
import com.highpowerbear.hpbtrader.linear.cdibean.tab.TabBean;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.StrategyLog;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.entity.TradeLog;
import com.highpowerbear.hpbtrader.linear.quote.TiCalculator;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Ema;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Macd;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Stochastics;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Singleton
@Path("linear")
public class LinService {
    @Inject private DatabaseDao databaseDao;
    @Inject private TiCalculator tiCalculator;
    @Inject private SeriesBean seriesBean;
    @Inject private TabBean tabBean;
    @Inject private LastTradeBean lastTradeBean;
    
    @GET
    @Path("quotes/{seriesId}")
    @Produces({"application/json"})
    public List<Quote> getQuotes(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        return databaseDao.getQuotes(seriesId, numBars);
    }
    
    @GET
    @Path("ema/{seriesId}/{emaPeriod}")
    @Produces({"application/json"})
    public List<Ema> getEma(@PathParam("seriesId") Integer seriesId, @PathParam("emaPeriod") Integer emaPeriod, @QueryParam("numBars") Integer numBars) {
        List<Quote> quotes = databaseDao.getQuotes(seriesId, LinSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateEma(quotes, emaPeriod);
    }
    
    @GET
    @Path("stoch/{seriesId}")
    @Produces({"application/json"})
    public List<Stochastics> getStoch(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        List<Quote> quotes = databaseDao.getQuotes(seriesId, LinSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateStoch(quotes);
    }
    
    @GET
    @Path("macd/{seriesId}")
    @Produces({"application/json"})
    public List<Macd> getMacd(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        List<Quote> quotes = databaseDao.getQuotes(seriesId, LinSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateMacd(quotes);
    }
    
    @GET
    @Path("chartparams")
    @Produces({"application/json"})
    public ChartParams getChartParams() {
        if (tabBean.getChartParams() == null) {
            return null;
        }
        return tabBean.getChartParams();
    }

    @GET
    @Path("chartparams_series")
    @Produces({"application/json"})
    public ChartParams getChartParamsSeries() {
        if (seriesBean.getChartParams() == null) {
            return null;
        }
        return seriesBean.getChartParams();
    }
    
    @GET
    @Path("strategylogs")
    @Produces({"application/json"})
    public List<StrategyLog> getStrategyLogs() {
        if (tabBean.getStrategy() == null) {
            return new ArrayList<>();
        }
        if (tabBean.getIsBacktest()) {
            return tabBean.getStrategyLogs();
        } else {
            return databaseDao.getStrategyLogs(tabBean.getStrategy(), true);
        }
    }
    
    @GET
    @Path("trades")
    @Produces({"application/json"})
    public List<Trade> getTrades() {
        if (tabBean.getStrategy() == null) {
            return new ArrayList<>();
        }
        if (tabBean.getIsBacktest()) {
            return tabBean.getTrades();
        } else {
            return databaseDao.getTradesByStrategy(tabBean.getStrategy(), true);
        }
    }
    
    @GET
    @Path("tradelogs")
    @Produces({"application/json"})
    public List<TradeLog> getTradeLogs() {
        if (tabBean.getSelectedTrade() == null) {
            return new ArrayList<>();
        }
        if (tabBean.getIsBacktest()) {
            return tabBean.getSelectedTradeLogs();
        } else {
            return databaseDao.getTradeLogs(tabBean.getSelectedTrade(), true);
        }
    }
    
    @GET
    @Path("lasttradelogs")
    @Produces({"application/json"})
    public List<TradeLog> getLastTradeLogs() {
        if (lastTradeBean.getTrade() == null) {
            return new ArrayList<>();
        }
        return databaseDao.getTradeLogs(lastTradeBean.getTrade(), false);
    }
}
