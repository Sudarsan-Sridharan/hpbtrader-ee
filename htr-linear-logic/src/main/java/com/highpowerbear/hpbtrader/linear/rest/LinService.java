package com.highpowerbear.hpbtrader.linear.rest;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by robertk on 11/14/2015.
 */
@Singleton
@Path("linear")
public class LinService {
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/connect/{connect}")
    public IbAccount connectIbAccount(@PathParam("accountId") String accountId, @PathParam("connect") Boolean connect) {
        return null;
    }

    @GET
    @Path("bars/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bar> getBars(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return null;
        }
        return barDao.getBars(series, numBars);
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
