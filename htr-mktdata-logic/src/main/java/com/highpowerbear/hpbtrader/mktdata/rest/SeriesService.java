package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by robertk on 2.12.2015.
 */
@ApplicationScoped
@Path("series")
public class SeriesService {

    @Inject private DataSeriesDao dataSeriesDao;

    @GET
    @Path("series")
    @Produces(MediaType.APPLICATION_JSON)
    public RestList<DataSeries> getSeries(@QueryParam("disabledToo") boolean disabledToo) {
        List<DataSeries> dataSeriesList = dataSeriesDao.getAllSeries(disabledToo);
        return new RestList<>(dataSeriesList, (long) dataSeriesList.size());
    }

    // TODO add series, remove series, move series up/down
}
