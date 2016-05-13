package com.highpowerbear.hpbtrader.strategy.rest;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;

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
public class StrategyService {
    @Inject private DataSeriesDao dataSeriesDao;

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
