package com.highpowerbear.hpbtrader.strategy.rest;

import com.highpowerbear.hpbtrader.strategy.common.StrategyDefinitions;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@ApplicationScoped
@Path("options")
public class OptService {
    private static final Logger l = Logger.getLogger(StrategyDefinitions.LOGGER);
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String echo(@QueryParam("id") int id) {
        return "You sent " + id;
    }
}