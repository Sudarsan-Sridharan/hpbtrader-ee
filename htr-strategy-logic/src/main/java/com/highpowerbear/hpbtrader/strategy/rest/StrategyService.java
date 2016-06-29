package com.highpowerbear.hpbtrader.strategy.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
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
    @Path("{strategyid}/backtest/finished")
    public Response isBacktestFinished(@PathParam("strategyid") Integer strategyId, TimeFrame timeFrame) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(strategyController.isBacktestFinished(strategy)).build();
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
        strategyController.manualOrder(ibOrder);
        return Response.ok().build();
    }

    @GET
    @Path("{strategyid}/strategylogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedStrategyLogs(@PathParam("strategyid") Integer strategyId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedStrategyLogs(strategyId, false, start, limit);
    }

    @GET
    @Path("{strategyid}/backtest/strategylogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBacktestStrategyLogs(@PathParam("strategyid") Integer strategyId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedStrategyLogs(strategyId, true, start, limit);
    }

    @GET
    @Path("{strategyid}/iborders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedIbOrders(@PathParam("strategyid") Integer strategyId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedIbOrders(strategyId, false, start, limit);
    }

    @GET
    @Path("{strategyid}/backtest/iborders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedBacktestIbOrders(@PathParam("strategyid") Integer strategyId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedIbOrders(strategyId, true, start, limit);
    }

    @GET
    @Path("{strategyid}/trades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedTrades(@PathParam("strategyid") Integer strategyId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedTrades(strategyId, false, start, limit);
    }

    @GET
    @Path("{strategyid}/backtest/trades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedBacktestTrades(@PathParam("strategyid") Integer strategyId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedTrades(strategyId, true, start, limit);
    }

    @GET
    @Path("trade/{tradeid}/tradelogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedTradeLogs(@PathParam("tradeid") Long tradeId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedTradeLogs(tradeId, false, start, limit);
    }

    @GET
    @Path("{tradeid}/backtest/tradelogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBacktestTradeLogs(@PathParam("tradeid") Long tradeId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return getPagedTradeLogs(tradeId, false, start, limit);
    }

    private Response getPagedStrategyLogs(Integer strategyId, boolean backtest, Integer start, Integer limit) {
        ProcessContext ctx = getProcessContext(strategyId, backtest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<StrategyLog> strategyLogs = ctx.getPagedStrategyLogs(start, limit);
        Long numStrategyLogs = ctx.getNumStrategyLogs();
        return Response.ok(new RestList<>(strategyLogs, numStrategyLogs)).build();
    }

    private Response getPagedIbOrders(Integer strategyId, boolean backtest, Integer start, Integer limit) {
        ProcessContext ctx = getProcessContext(strategyId, backtest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<IbOrder> ibOrders = ctx.getPagedIbOrders(start, limit);
        Long numIbOrders = ctx.getNumIbOrders();
        return Response.ok(new RestList<>(ibOrders, numIbOrders)).build();
    }

    private Response getPagedTrades(Integer strategyId, boolean backtest, Integer start, Integer limit) {
        ProcessContext ctx = getProcessContext(strategyId, backtest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<Trade> trades = ctx.getPagedTrades(start, limit);
        Long numTrades = ctx.getNumTrades();
        return Response.ok(new RestList<>(trades, numTrades)).build();
    }

    private Response getPagedTradeLogs(Long tradeId, boolean backtest, Integer start, Integer limit) {
        Trade trade = tradeDao.findTrade(tradeId);
        if (trade == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProcessContext ctx = getProcessContext(trade.getStrategy().getId(), backtest);
        if (ctx == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        List<TradeLog> tradeLogs = ctx.getPagedTradeLogs(trade, start, limit);
        Long numTradeLogs = ctx.getNumTradeLogs(trade);
        return Response.ok(new RestList<>(tradeLogs, numTradeLogs)).build();
    }

    private ProcessContext getProcessContext(Integer strategyId, boolean backtest) {
        Strategy strategy = strategyDao.findStrategy(strategyId);
        if (strategy == null) {
            return null;
        }
        return backtest ? strategyController.getBacktestContext(strategy) : strategyController.getTradingContext(strategy);
    }
}
