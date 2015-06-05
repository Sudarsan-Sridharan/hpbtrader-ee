package com.highpowerbear.hpbtrader.options.rest;

import com.highpowerbear.hpbtrader.options.common.OptData;
import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.common.SingletonRepo;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.process.SignalProcessor;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Path("options")
public class OptService {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    private OptDao optDao = SingletonRepo.getInstance().getOptDao();
    private SignalProcessor processor = SingletonRepo.getInstance().getSignalProcessor();
    private OptData optData = SingletonRepo.getInstance().getOptData();
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String echo(@QueryParam("id") int id) {
        return "You sent " + id;
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("openLong/{underlying}") // openLong/SPY?name=test
    public Long openLong(
            @PathParam("underlying") String underlying, 
            @QueryParam("name") String name) {
        
        String method = "openLong";
        String params = underlying + ", " + name;
        Boolean isValid = (existsUnderlying(underlying));
        if (!isValid) {
            errorParams(method, params);
            return OptEnums.SignalErrorLongResponse.INVALID_REQUEST.getValue();
        }
        Long id = processor.openLong(underlying, name);
        return (id != null ? id : OptEnums.SignalErrorLongResponse.NOT_ACCEPTED.getValue());
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("openShort/{underlying}") // openShort/SPY?name=test
    public Long openShort(
            @PathParam("underlying") String underlying, 
            @QueryParam("name") String name) {
        
        String method = "openShort";
        String params = underlying + ", " + name;
        Boolean isValid = (existsUnderlying(underlying));
        if (!isValid) {
            errorParams(method, params);
            return OptEnums.SignalErrorLongResponse.INVALID_REQUEST.getValue();
        }
        Long id = processor.openShort(underlying, name);
        return (id != null ? id : OptEnums.SignalErrorLongResponse.NOT_ACCEPTED.getValue());
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("close/{underlying}") // close/SPY?name=test
    public Long close(
            @PathParam("underlying") String underlying,
            @QueryParam("name") String name) {
        
        String method = "close";
        String params = underlying + ", " + name;
        Boolean isValid = (existsUnderlying(underlying));
        if (!isValid) {
            errorParams(method, params);
            return OptEnums.SignalErrorLongResponse.INVALID_REQUEST.getValue();
        }
        Long id = processor.close(underlying, name);
        return (id != null ? id : OptEnums.SignalErrorLongResponse.NOT_ACCEPTED.getValue());
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("reverse/{underlying}") // reverse/SPY?name=test
    public Long reverse(
            @PathParam("underlying") String underlying, 
            @QueryParam("name") String name) {
        
        String method = "reverse";
        String params = underlying + ", " + name;
        Boolean isValid = (existsUnderlying(underlying));
        if (!isValid) {
            errorParams(method, params);
            return OptEnums.SignalErrorLongResponse.INVALID_REQUEST.getValue();
        }
        Long id = processor.reverse(underlying, name);
        return (id != null ? id : OptEnums.SignalErrorLongResponse.NOT_ACCEPTED.getValue());
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("orderStatus") // orderStatus?signalID=12345678
    public String orderStatus(@QueryParam("signalID") Long signalID) {
        String method = "orderStatus";
        String params = "" + signalID;
        Boolean isValid = (existsSignal(signalID));
        //createRestApiLog(method, params, isValid);
        if (!isValid) {
            errorParams(method, params);
            return OptEnums.SignalErrorStringResponse.INVALID_SIGNAL_ID.getValue();
        }
        return processor.orderStatus(signalID);
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("activeTrade/{underlying}") // activeTrade/SPY
    public String activeTrade(@PathParam("underlying") String underlying) {
        String method = "activeTrade";
        Boolean isValid = (existsUnderlying(underlying));
        //createRestApiLog(method, params, isValid);
        if (!isValid) {
            errorParams(method, underlying);
            return OptEnums.UnderlyingErrorResponse.INVALID_UNDERLYING.getValue();
        }
        String activeTrade = processor.activeTrade(underlying);
        return (activeTrade != null ? activeTrade : OptEnums.UnderlyingErrorResponse.NONE.getValue());
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("position/{underlying}") // position/SPY
    public String position(@PathParam("underlying") String underlying) {
        String method = "position";
        Boolean isValid = (existsUnderlying(underlying));
        //createRestApiLog(method, params, isValid);
        if (!isValid) {
            errorParams(method, underlying);
            return OptEnums.UnderlyingErrorResponse.INVALID_UNDERLYING.getValue();
        }
        String positionList = processor.position(underlying);
        return (positionList != null ? positionList : OptEnums.UnderlyingErrorResponse.NONE.getValue());
    }
    
    private boolean existsUnderlying(String underlying) {
        return (underlying != null && optData.existsUnderlying(underlying));
    }
    
    private boolean existsSignal(Long signalID) {
        return(signalID != null && optDao.existsSignal(signalID));
    }
    
    private void errorParams(String method, String params) {
        l.log(Level.SEVERE, "Error", method + " parameters not valid: " + params);
    }
}