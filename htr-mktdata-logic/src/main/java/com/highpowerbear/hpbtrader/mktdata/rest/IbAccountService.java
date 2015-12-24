package com.highpowerbear.hpbtrader.mktdata.rest;

import com.highpowerbear.hpbtrader.mktdata.ibclient.IbController;
import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.shared.defintions.HtrConstants;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.model.RestList;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.IbAccountDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by robertk on 25.11.2015.
 */
@ApplicationScoped
@Path("ibaccount")
public class IbAccountService {

    @Inject private TiCalculator tiCalculator;
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;
    @Inject private IbAccountDao ibAccountDao;
    @Inject private IbController ibController;
    @Inject private HistDataController histDataController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts")
    public RestList<IbAccount> getIbAccounts() {
        List<IbAccount> ibAccounts = ibAccountDao.getIbAccounts();
        ibAccounts.forEach(ibAccount -> {
            ibAccount.setIbConnection(ibController.getIbConnectionMap().get(ibAccount));
            ibAccount.getIbConnection().setIsConnected(ibController.isConnected(ibAccount));
        });
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
    @Path("ibaccounts/{accountId}/connect")
    public Response connectIbAccount(@PathParam("accountId") String accountId) {
        IbAccount ibAccount = ibAccountDao.findIbAccount(accountId);
        if (ibAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ibController.connect(ibAccount);
        HtrUtil.waitMilliseconds(HtrConstants.ONE_SECOND);
        ibAccount.setIbConnection(ibController.getIbConnectionMap().get(ibAccount));
        ibAccount.getIbConnection().setIsConnected(ibController.isConnected(ibAccount));
        return Response.ok(ibAccount).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/disconnect")
    public Response disconnectIbAccount(@PathParam("accountId") String accountId) {
        IbAccount ibAccount = ibAccountDao.findIbAccount(accountId);
        if (ibAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ibController.disconnect(ibAccount);
        HtrUtil.waitMilliseconds(HtrConstants.ONE_SECOND);
        ibAccount.setIbConnection(ibController.getIbConnectionMap().get(ibAccount));
        ibAccount.getIbConnection().setIsConnected(ibController.isConnected(ibAccount));
        return Response.ok(ibAccount).build();
    }
}
