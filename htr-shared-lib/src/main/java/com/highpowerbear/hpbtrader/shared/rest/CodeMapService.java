package com.highpowerbear.hpbtrader.shared.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by robertk on 6.4.2016.
 */
@ApplicationScoped
@Path("codemap")
public class CodeMapService {

    @GET
    @Path("iborderstatus/texts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderStatusTexts() {
        Map<HtrEnums.IbOrderStatus, String> map = new LinkedHashMap<>();
        for (HtrEnums.IbOrderStatus k : HtrEnums.IbOrderStatus.values()) {
            map.put(k, k.getDisplayText());
        }
        return Response.ok(map).build();
    }

    @GET
    @Path("iborderstatus/colors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderStatusColors() {
        Map<HtrEnums.IbOrderStatus, String> map = new LinkedHashMap<>();
        for (HtrEnums.IbOrderStatus k : HtrEnums.IbOrderStatus.values()) {
            map.put(k, k.getDisplayColor().toLowerCase().replace("_", ""));
        }
        return Response.ok(map).build();
    }

    @GET
    @Path("strategymode/colors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStrategyModeColors() {
        Map<HtrEnums.StrategyMode, String> map = new LinkedHashMap<>();
        for (HtrEnums.StrategyMode k : HtrEnums.StrategyMode.values()) {
            map.put(k, k.getDisplayColor());
        }
        return Response.ok(map).build();
    }

    @GET
    @Path("tradetype/texts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeTypeTexts() {
        Map<HtrEnums.TradeType, String> map = new LinkedHashMap<>();
        for (HtrEnums.TradeType k : HtrEnums.TradeType.values()) {
            map.put(k, k.getDisplayText());
        }
        return Response.ok(map).build();
    }

    @GET
    @Path("tradetype/colors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeTypeColors() {
        Map<HtrEnums.TradeType, String> map = new LinkedHashMap<>();
        for (HtrEnums.TradeType k : HtrEnums.TradeType.values()) {
            map.put(k, k.getDisplayColor());
        }
        return Response.ok(map).build();
    }

    @GET
    @Path("tradestatus/texts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeStatusTexts() {
        Map<HtrEnums.TradeStatus, String> map = new LinkedHashMap<>();
        for (HtrEnums.TradeStatus k : HtrEnums.TradeStatus.values()) {
            map.put(k, k.getDisplayText());
        }
        return Response.ok(map).build();
    }

    @GET
    @Path("tradestatus/colors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeStatusColors() {
        Map<HtrEnums.TradeStatus, String> map = new LinkedHashMap<>();
        for (HtrEnums.TradeStatus k : HtrEnums.TradeStatus.values()) {
            map.put(k, k.getDisplayColor());
        }
        return Response.ok(map).build();
    }
}
