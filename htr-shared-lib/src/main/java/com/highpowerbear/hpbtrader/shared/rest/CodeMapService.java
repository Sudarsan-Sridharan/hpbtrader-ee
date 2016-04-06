package com.highpowerbear.hpbtrader.shared.rest;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.model.CodeMapEntry;
import com.highpowerbear.hpbtrader.shared.model.RestList;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertk on 6.4.2016.
 */
@ApplicationScoped
@Path("codemap")
public class CodeMapService {

    @GET
    @Path("iborderstatus/displaytext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderStatusDisplayTexts() {
        List<CodeMapEntry<HtrEnums.IbOrderStatus, String>> entries = new ArrayList<>();
        for (HtrEnums.IbOrderStatus k : HtrEnums.IbOrderStatus.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayText()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("iborderstatus/displaycolor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderStatusDisplayColors() {
        List<CodeMapEntry<HtrEnums.IbOrderStatus, HtrEnums.DisplayColor>> entries = new ArrayList<>();
        for (HtrEnums.IbOrderStatus k : HtrEnums.IbOrderStatus.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayColor()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("strategymode/displaycolor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStrategyModeDisplayColors() {
        List<CodeMapEntry<HtrEnums.StrategyMode, HtrEnums.DisplayColor>> entries = new ArrayList<>();
        for (HtrEnums.StrategyMode k : HtrEnums.StrategyMode.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayColor()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("tradetype/displaytext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeTypeDisplayTexts() {
        List<CodeMapEntry<HtrEnums.TradeType, String>> entries = new ArrayList<>();
        for (HtrEnums.TradeType k : HtrEnums.TradeType.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayText()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("tradetype/displaycolor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeTypeDisplayColors() {
        List<CodeMapEntry<HtrEnums.TradeType, HtrEnums.DisplayColor>> entries = new ArrayList<>();
        for (HtrEnums.TradeType k : HtrEnums.TradeType.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayColor()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("tradestatus/displaytext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeStatusDisplayTexts() {
        List<CodeMapEntry<HtrEnums.TradeStatus, String>> entries = new ArrayList<>();
        for (HtrEnums.TradeStatus k : HtrEnums.TradeStatus.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayText()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("tradestatus/displaycolor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTradeStatusDisplayColors() {
        List<CodeMapEntry<HtrEnums.TradeStatus, HtrEnums.DisplayColor>> entries = new ArrayList<>();
        for (HtrEnums.TradeStatus k : HtrEnums.TradeStatus.values()) {
            entries.add(new CodeMapEntry<>(k, k.getDisplayColor()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }

    @GET
    @Path("realtimestatus/displaycolor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRealtimeStatusDisplayColors() {
        List<CodeMapEntry<HtrEnums.RealtimeStatus, HtrEnums.DisplayColor>> entries = new ArrayList<>();
        for (HtrEnums.RealtimeStatus s : HtrEnums.RealtimeStatus.values()) {
            entries.add(new CodeMapEntry<>(s, s.getDisplayColor()));
        }
        return Response.ok(new RestList<>(entries, (long) entries.size())).build();
    }
}
