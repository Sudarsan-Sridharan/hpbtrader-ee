package com.highpowerbear.hpbtrader.linear.rest;

import com.highpowerbear.hpbtrader.linear.model.ChartParams;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.StrategyLog;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.entity.TradeLog;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;

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
    @Inject private BarDao barDao;
    
    @GET
    @Path("bars/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bar> getBars(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        return barDao.getBars(seriesId, numBars);
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
