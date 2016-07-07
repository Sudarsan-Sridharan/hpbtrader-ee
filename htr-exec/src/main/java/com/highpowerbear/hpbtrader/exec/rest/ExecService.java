package com.highpowerbear.hpbtrader.exec.rest;

import com.highpowerbear.hpbtrader.exec.ibclient.HeartbeatControl;
import com.highpowerbear.hpbtrader.exec.ibclient.IbController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.entity.IbOrder;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbOrderDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by robertk on 18.5.2016.
 */
@ApplicationScoped
@Path("exec")
public class ExecService {

    @Inject private IbAccountDao ibAccountDao;
    @Inject private IbOrderDao ibOrderDao;
    @Inject private IbController ibController;
    @Inject private HeartbeatControl heartbeatControl;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestList<IbAccount> getIbAccounts() {
        List<IbAccount> ibAccounts = ibAccountDao.getIbAccounts();
        ibAccounts.forEach(ibAccount -> ibAccount.setExecConnection(ibController.getIbConnection(ibAccount)));
        return new RestList<>(ibAccounts, (long) ibAccounts.size());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts")
    public Response updateIbAccount(IbAccount ibAccount) {
        IbAccount ibAccountDb = ibAccountDao.findIbAccount(ibAccount.getAccountId());
        if (ibAccountDb == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ibAccountDao.updateIbAccount(ibAccount)).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/connect/{connect}")
    public Response connectIbAccount(@PathParam("accountId") String accountId,  @PathParam("connect") Boolean connect) {
        IbAccount ibAccount = ibAccountDao.findIbAccount(accountId);
        if (ibAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (connect) {
            ibController.connectExec(ibAccount);
        } else {
            ibController.disconnectExec(ibAccount);
        }
        HtrUtil.waitMilliseconds(HtrDefinitions.ONE_SECOND_MILLIS);
        ibAccount.setExecConnection(ibController.getIbConnection(ibAccount));
        return Response.ok(ibAccount).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/iborders")
    public Response getPagedIbOrders(
            @PathParam("accountId") String accountId,
            @QueryParam("filter") String jsonFilter,
            @QueryParam("start") Integer start,
            @QueryParam("limit") Integer limit) {

        start = (start != null ? start : 0);
        limit = (limit != null ? limit : HtrDefinitions.JPA_MAX_RESULTS);
        IbAccount ibAccount = ibAccountDao.findIbAccount(accountId);
        if (ibAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<IbOrder> ibOrders = new ArrayList<>();
        for (IbOrder ibOrder : ibOrderDao.getPagedIbOrders(ibAccount, start, limit)) {
            Map<IbOrder, Integer> hm = heartbeatControl.getOpenOrderHeartbeatMap().get(ibOrder.getStrategy().getIbAccount());
            ibOrder.setHeartbeatCount(hm.get(ibOrder));
            ibOrders.add(ibOrder);
        }
        return Response.ok(new RestList<>(ibOrders, ibOrderDao.getNumIbOrders(ibAccount))).build();
    }
}
