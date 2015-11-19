package com.highpowerbear.hpbtrader.linear.rest;

import com.highpowerbear.hpbtrader.shared.entity.IbAccount;

import javax.ejb.Singleton;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by robertk on 11/14/2015.
 */
@Singleton
@Path("linear")
public class LinService {

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ibaccounts/{accountId}/connect/{connect}")
    public IbAccount connecIbAccount(@PathParam("accountId") String accountId, @PathParam("connect") Boolean connect) {
        return null;
    }

}
