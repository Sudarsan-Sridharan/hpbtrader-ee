package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Ema;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Macd;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Stochastics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by robertk on 25.11.2015.
 */
@ApplicationScoped
@Path("mktdata")
public class MktDataService {

    @Inject private TiCalculator tiCalculator;
    @Inject private BarDao barDao;

    @GET
    @Path("ema/{seriesId}/{emaPeriod}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ema> getEma(@PathParam("seriesId") Integer seriesId, @PathParam("emaPeriod") Integer emaPeriod, @QueryParam("numBars") Integer numBars) {
        List<Bar> bars = barDao.getBars(seriesId, HtrSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateEma(bars, emaPeriod);
    }

    @GET
    @Path("stoch/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Stochastics> getStoch(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        List<Bar> bars = barDao.getBars(seriesId, HtrSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateStoch(bars);
    }

    @GET
    @Path("macd/{seriesId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Macd> getMacd(@PathParam("seriesId") Integer seriesId, @QueryParam("numBars") Integer numBars) {
        List<Bar> bars = barDao.getBars(seriesId, HtrSettings.BARS_REQUIRED + numBars);
        return tiCalculator.calculateMacd(bars);
    }
}
