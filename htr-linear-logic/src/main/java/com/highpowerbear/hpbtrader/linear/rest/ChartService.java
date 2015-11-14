package com.highpowerbear.hpbtrader.linear.rest;

import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.StrategyLog;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.entity.TradeLog;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import com.highpowerbear.hpbtrader.linear.quote.TiCalculator;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Ema;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Macd;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Stochastics;
import com.highpowerbear.hpbtrader.linear.rest.model.ChartParams;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Singleton
@Path("chart")
public class ChartService {
    @Inject private DatabaseDao databaseDao;
    @Inject private TiCalculator tiCalculator;
    
    @GET
    @Path("quotes/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Quote> getQuotes(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        return databaseDao.getQuotes(seriesId, numBars);
    }
    
    @GET
    @Path("ema/{seriesId}/{emaPeriod}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ema> getEma(@PathParam("seriesId") Integer seriesId, @PathParam("emaPeriod") Integer emaPeriod, @QueryParam("numBars") Integer numBars) {
        List<Quote> quotes = databaseDao.getQuotes(seriesId, LinSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateEma(quotes, emaPeriod);
    }
    
    @GET
    @Path("stoch/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Stochastics> getStoch(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        List<Quote> quotes = databaseDao.getQuotes(seriesId, LinSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateStoch(quotes);
    }
    
    @GET
    @Path("macd/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Macd> getMacd(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        List<Quote> quotes = databaseDao.getQuotes(seriesId, LinSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateMacd(quotes);
    }
    
    @GET
    @Path("chartparams")
    @Produces(MediaType.APPLICATION_JSON)
    public ChartParams getChartParams() {
        return null;
    }

    @GET
    @Path("chartparams_series")
    @Produces(MediaType.APPLICATION_JSON)
    public ChartParams getChartParamsSeries() {
        return null;
    }
    
    @GET
    @Path("strategylogs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StrategyLog> getStrategyLogs() {
        return null;
    }
    
    @GET
    @Path("trades")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Trade> getTrades() {
        return null;
    }
    
    @GET
    @Path("tradelogs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradeLog> getTradeLogs() {
        return null;
    }
    
    @GET
    @Path("lasttradelogs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradeLog> getLastTradeLogs() {
        return null;
    }
}
