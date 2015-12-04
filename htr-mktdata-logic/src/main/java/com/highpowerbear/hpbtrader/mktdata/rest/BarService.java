package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;

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
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("series/{seriesId}/backfill")
    public Response backfillBars(@PathParam("seriesId") Integer seriesId) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        histDataController.backfill(series);
        return Response.ok().build();
    }

    @GET
    @Path("series/{seriesId}/bars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBars(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Bar> bars = barDao.getBars(series, numBars);
        return Response.ok(new RestList<>(barDao.getBars(series, numBars), (long) bars.size())).build();
    }

    @GET
    @Path("series/{seriesId}/pagedbars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagedBars(@PathParam("seriesId") Integer seriesId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        start = (start == null ? 0 : start);
        limit = (limit == null ? HtrSettings.JPA_MAX_RESULTS : limit);
        return Response.ok(new RestList<>(barDao.getPagedBars(series, start, limit), barDao.getNumBars(series))).build();
    }

}
