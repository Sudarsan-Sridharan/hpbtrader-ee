package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by robertk on 3.12.2015.
 */
@ApplicationScoped
@Path("indicator")
public class IndicatorService {

    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;
    @Inject private TiCalculator tiCalculator;

    @GET
    @Path("series/{seriesId}/ema/{emaPeriod}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateEma(@PathParam("seriesId") Integer seriesId, @PathParam("emaPeriod") Integer emaPeriod, @QueryParam("numBars") Integer numBars) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Bar> bars = barDao.getBars(series, HtrSettings.BARS_REQUIRED + numBars);
        return Response.ok(tiCalculator.calculateEma(bars, emaPeriod)).build();
    }

    @GET
    @Path("series/{seriesId}/stoch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateStoch(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Bar> bars = barDao.getBars(series, HtrSettings.BARS_REQUIRED + numBars);
        return Response.ok(tiCalculator.calculateStoch(bars)).build();
    }

    @GET
    @Path("series/{seriesId}/macd")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateMacd(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        Series series = seriesDao.findSeries(seriesId);
        if (series == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Bar> bars = barDao.getBars(series, HtrSettings.BARS_REQUIRED + numBars);
        return Response.ok(tiCalculator.calculateMacd(bars)).build();
    }
}
