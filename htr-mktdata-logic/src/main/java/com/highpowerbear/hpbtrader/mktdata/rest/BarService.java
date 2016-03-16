package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by robertk on 2.12.2015.
 */
@ApplicationScoped
@Path("bar")
public class BarService {

    @Inject private HistDataController histDataController;
    @Inject private DataSeriesDao dataSeriesDao;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("series/{seriesId}/backfill")
    public Response backfillBars(@PathParam("seriesId") Integer seriesId) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        histDataController.backfill(dataSeries);
        return Response.ok().build();
    }

    @GET
    @Path("series/{seriesId}/bars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBars(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, numBars);
        return Response.ok(new RestList<>(dataSeriesDao.getBars(dataSeries, numBars), (long) dataBars.size())).build();
    }

    @GET
    @Path("series/{seriesId}/pagedbars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedBars(@PathParam("seriesId") Integer seriesId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = (start == null ? 0 : start);
        limit = (limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit);
        return Response.ok(new RestList<>(dataSeriesDao.getPagedBars(dataSeries, start, limit), dataSeriesDao.getNumBars(dataSeries))).build();
    }

}
