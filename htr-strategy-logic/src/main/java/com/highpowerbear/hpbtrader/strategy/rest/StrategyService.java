package com.highpowerbear.hpbtrader.strategy.rest;

import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyController;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by robertk on 11/14/2015.
 */
@Singleton
@Path("strategies")
public class StrategyService {

    @Inject private StrategyDao strategyDao;
    @Inject private TradeDao tradeDao;
    @Inject private StrategyController strategyController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestList<StrategyLog> getStrategies() {
        return null;
    }

    @GET
    @Path("{strategyid}/strategylogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStrategyLogs(
            @PathParam("strategyid") Integer strategyId,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProcessContext ctx = strategyController.getStrategyContextMap().get(strategy);
        List<StrategyLog> strategyLogs = ctx.getPagedStrategyLogs(start, limit);
        Long numStrategyLogs = ctx.getNumStrategyLogs();
        return Response.ok(new RestList<>(strategyLogs, numStrategyLogs)).build();
    }

    @GET
    @Path("{strategyid}/iborders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIbOrders(
            @PathParam("strategyid") Integer strategyId,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProcessContext ctx = strategyController.getStrategyContextMap().get(strategy);
        List<IbOrder> ibOrders = ctx.getPagedIbOrders(start, limit);
        Long numIbOrders = ctx.getNumIbOrders();
        return Response.ok(new RestList<>(ibOrders, numIbOrders)).build();
    }

    @GET
    @Path("{strategyid}/trades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrades(
            @PathParam("strategyid") Integer strategyId,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProcessContext ctx = strategyController.getStrategyContextMap().get(strategy);
        List<Trade> trades = ctx.getPagedTrades(start, limit);
        Long numTrades = ctx.getNumTrades();
        return Response.ok(new RestList<>(trades, numTrades)).build();
    }

    @GET
    @Path("{tradeid}/tradelogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeLogs(
            @PathParam("tradeid") Long tradeId,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        Trade trade = tradeDao.findTrade(tradeId);
        if (trade == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProcessContext ctx = strategyController.getStrategyContextMap().get(trade.getStrategy());
        List<TradeLog> tradeLogs = ctx.getPagedTradeLogs(trade, start, limit);
        Long numTradeLogs = ctx.getNumTradeLogs(trade);
        return Response.ok(new RestList<>(tradeLogs, numTradeLogs)).build();
    }
}
