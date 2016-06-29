package com.highpowerbear.hpbtrader.exec.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by robertk on 6/3/2016.
 */
@ApplicationScoped
@Path("iborders")
public class IbOrderService {

    @Inject private IbOrderDao ibOrderDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestList<IbOrder> getPagedIbOrders(@QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        start = start == null ? 0 : start;
        limit = limit == null ? HtrDefinitions.JPA_MAX_RESULTS : limit;
        return new RestList<>(ibOrderDao.getPagedIbOrders(start, limit), ibOrderDao.getNumIbOrders());
    }
}