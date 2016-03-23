package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
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
public class TechAnalysisService {

    @Inject private DataSeriesDao dataSeriesDao;
    @Inject private TiCalculator tiCalculator;

    @GET
    @Path("series/{seriesId}/ema/{emaPeriod}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateEma(@PathParam("seriesId") Integer seriesId, @PathParam("emaPeriod") Integer emaPeriod, @QueryParam("numBars") Integer numBars) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, HtrDefinitions.BARS_REQUIRED + numBars);
        return Response.ok(tiCalculator.calculateEma(dataBars, emaPeriod)).build();
    }

    @GET
    @Path("series/{seriesId}/stoch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateStoch(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, HtrDefinitions.BARS_REQUIRED + numBars);
        return Response.ok(tiCalculator.calculateStoch(dataBars)).build();
    }

    @GET
    @Path("series/{seriesId}/macd")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateMacd(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        DataSeries dataSeries = dataSeriesDao.findSeries(seriesId);
        if (dataSeries == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<DataBar> dataBars = dataSeriesDao.getBars(dataSeries, HtrDefinitions.BARS_REQUIRED + numBars);
        return Response.ok(tiCalculator.calculateMacd(dataBars)).build();
    }
}
