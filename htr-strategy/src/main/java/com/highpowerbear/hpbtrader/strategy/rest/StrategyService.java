package com.highpowerbear.hpbtrader.strategy.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.GenericTuple;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.model.TimeFrame;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.strategy.process.ProcessContext;
import com.highpowerbear.hpbtrader.strategy.process.ProcessQueueManager;
import com.highpowerbear.hpbtrader.strategy.process.StrategyController;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 11/14/2015.
 */
@ApplicationScoped
@Path("strategies")
public class StrategyService {

    @Inject private StrategyDao strategyDao;
    @Inject private TradeDao tradeDao;
    @Inject private StrategyController strategyController;
    @Inject private ProcessQueueManager processQueueManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestList<Strategy> getStrategies() {
        List<Strategy> strategies =  strategyDao.getStrategies();
        return new RestList<>(strategies, (long) strategies.size());
    }

    @DELETE
    @Path("{strategyid}")
    public Response deleteStrategy(@PathParam("strategyid") Integer strategyId) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        strategyDao.deleteStrategy(strategy);
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{strategyid}/backtest")
    public Response backtestStrategy(@PathParam("strategyid") Integer strategyId, TimeFrame timeFrame) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Calendar fromDate = timeFrame.getFromDate() == null ? HtrUtil.getCalendarMonthsOffset(HtrDefinitions.BACKTEST_DEFAULT_MONTHS) : timeFrame.getFromDate();
        Calendar toDate = timeFrame.getToDate() == null ? HtrUtil.getCalendar() : timeFrame.getToDate();
        processQueueManager.queueBacktestStrategy(new GenericTuple<>(strategy, new TimeFrame(fromDate, toDate)));
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{strategyid}/backtest/status")
    public Response getBacktestStatus(@PathParam("strategyid") Integer strategyId, TimeFrame timeFrame) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(strategyController.getBacktestStatus(strategy)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{strategyid}/manualorder")
    public Response manualOrder(@PathParam("strategyid") Integer strategyId, IbOrder ibOrder) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ibOrder.setStrategy(strategy);
        ibOrder.setIbAccount(strategy.getIbAccount());
        ibOrder.setTriggerDesc(HtrDefinitions.MANUAL_ORDER);
        strategyController.manualOrder(ibOrder);
        return Response.ok().build();
    }

    @GET
    @Path("{strategyid}/strategyperformances/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStrategyPerformances(
            @PathParam("strategyid") Integer strategyId,
            @PathParam("type") String type,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        boolean isBacktest = HtrEnums.StrategyDataType.BACKTEST.name().equalsIgnoreCase(type);
        ProcessContext ctx = getProcessContext(strategyId, isBacktest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<StrategyPerformance> strategyPerformances = ctx.getStrategyPerformances(start, limit);
        Long numStrategyPerformances = ctx.getNumStrategyPerformances();
        return Response.ok(new RestList<>(strategyPerformances, numStrategyPerformances)).build();
    }

    @GET
    @Path("{strategyid}/iborders/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIbOrders(
            @PathParam("strategyid") Integer strategyId,
            @PathParam("type") String type,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        boolean isBacktest = HtrEnums.StrategyDataType.BACKTEST.name().equalsIgnoreCase(type);
        ProcessContext ctx = getProcessContext(strategyId, isBacktest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<IbOrder> ibOrders = ctx.getIbOrders(start, limit);
        Long numIbOrders = ctx.getNumIbOrders();
        return Response.ok(new RestList<>(ibOrders, numIbOrders)).build();
    }

    @GET
    @Path("{strategyid}/trades/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrades(
            @PathParam("strategyid") Integer strategyId,
            @PathParam("type") String type,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        boolean isBacktest = HtrEnums.StrategyDataType.BACKTEST.name().equalsIgnoreCase(type);
        ProcessContext ctx = getProcessContext(strategyId, isBacktest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<Trade> trades = ctx.getTrades(start, limit);
        Long numTrades = ctx.getNumTrades();
        return Response.ok(new RestList<>(trades, numTrades)).build();
    }

    @GET
    @Path("trade/{tradeid}/tradelogs/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeLogs(
            @PathParam("tradeid") Long tradeId,
            @PathParam("type") String type,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        boolean isBacktest = HtrEnums.StrategyDataType.BACKTEST.name().equalsIgnoreCase(type);
        Trade trade = tradeDao.findTrade(tradeId);
        if (trade == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProcessContext ctx = getProcessContext(trade.getStrategy().getId(), isBacktest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<TradeLog> tradeLogs = ctx.getTradeLogs(trade, start, limit);
        Long numTradeLogs = ctx.getNumTradeLogs(trade);
        return Response.ok(new RestList<>(tradeLogs, numTradeLogs)).build();
    }

    private ProcessContext getProcessContext(Integer strategyId, boolean isBacktest) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return null;
        }
        return isBacktest ? strategyController.getBacktestContext(strategy) : strategyController.getTradingContext(strategy);
    }
}
