package com.highpowerbear.hpbtrader.options.execution;

import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.model.*;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Trade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
public class StatusChecker {
    @Inject private OptData optData;
    
    public ReadinessStatus getReadinessStatus(String underlying) {
        ReadinessStatus rs = new ReadinessStatus(underlying);
        UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
        if (ud.getActiveCallSymbol() == null || ud.getFrontExpiryCallSymbol() == null || ud.getNextExpiryCallSymbol() == null || 
            ud.getActivePutSymbol()  == null || ud.getFrontExpiryPutSymbol()  == null || ud.getNextExpiryPutSymbol()  == null) 
        {
            rs.setReady(false);
            rs.setDescription("Option contracts cannot be determined");
            return rs;
        }
        MarketData activeCallMarketData = optData.getMarketDataMap().get(ud.getActiveCallSymbol());
        MarketData frontExpiryCallMarketData = optData.getMarketDataMap().get(ud.getFrontExpiryCallSymbol());
        MarketData nextExpiryCallMarketData = optData.getMarketDataMap().get(ud.getNextExpiryCallSymbol());
        
        MarketData activePutMarketData = optData.getMarketDataMap().get(ud.getActivePutSymbol());
        MarketData frontExpiryPutMarketData = optData.getMarketDataMap().get(ud.getFrontExpiryPutSymbol());
        MarketData nextExpiryPutMarketData = optData.getMarketDataMap().get(ud.getNextExpiryPutSymbol());
        
        if (activeCallMarketData == null || frontExpiryCallMarketData == null || nextExpiryCallMarketData == null ||
            activePutMarketData  == null || frontExpiryPutMarketData  == null || nextExpiryPutMarketData  == null) 
        {
            rs.setReady(false);
            rs.setDescription("Market data not ready for option contracts");
            return rs;
        }
        rs.setActiveCallMarketDataSnapshot(activeCallMarketData.createSnapshot());
        rs.setActivePutMarketDataSnapshot(activePutMarketData.createSnapshot());
        
        if (!isValidPrice(rs.getActiveCallMarketDataSnapshot().getBid().getValue()) ||
            !isValidPrice(rs.getActiveCallMarketDataSnapshot().getAsk().getValue()) ||
            !isValidPrice(frontExpiryCallMarketData.getBid().getValue())            ||
            !isValidPrice(frontExpiryCallMarketData.getAsk().getValue())            ||
            !isValidPrice(nextExpiryCallMarketData.getBid().getValue())             ||
            !isValidPrice(nextExpiryCallMarketData.getAsk().getValue())             ||
                
            !isValidPrice(rs.getActivePutMarketDataSnapshot().getBid().getValue())  ||
            !isValidPrice(rs.getActivePutMarketDataSnapshot().getAsk().getValue())  ||
            !isValidPrice(frontExpiryPutMarketData.getBid().getValue())             ||
            !isValidPrice(frontExpiryPutMarketData.getAsk().getValue())             ||
            !isValidPrice(nextExpiryPutMarketData.getBid().getValue())              ||
            !isValidPrice(nextExpiryPutMarketData.getAsk().getValue()))
        {
            rs.setReady(false);
            rs.setDescription("Market data not valid for option contracts");
            return rs;
        }
        rs.setReady(true);
        rs.setDescription("Ready");
        return rs;
    }
    
    public ConstraintsStatus getConstraintsStatus(HtrEnums.OptionType optionType, Trade trade, ReadinessStatus rs) {
        MarketData mdSnapshot = (HtrEnums.OptionType.CALL.equals(optionType) ? rs.getActiveCallMarketDataSnapshot() : rs.getActivePutMarketDataSnapshot());
        ConstraintsStatus cs = new ConstraintsStatus(mdSnapshot.getSymbol());
        ContractProperties cp = optData.getContractPropertiesMap().get(mdSnapshot.getUnderlying());
        Double bas = mdSnapshot.getBidAskSpread();
        if (bas > cp.getMaxSpread()) {
            cs.setMatch(false);
            cs.setDescription(mdSnapshot.getSymbol() + " bid/ask spread: " + bas + " > " + cp.getMaxSpread());
            return cs;
        }
        if (bas < 0d) { // in rare cases of negative bid/as spread
            cs.setMatch(false);
            cs.setDescription(mdSnapshot.getSymbol() + " bid/ask spread: " + bas + " is reversed");
            return cs;
        }
        Integer vol = mdSnapshot.getVolume().getValue();
        if (isValidSize(vol) &&  vol < cp.getMinVolume()) {
            cs.setMatch(false);
            cs.setDescription(mdSnapshot.getSymbol() + " volume: " + vol + " < " + cp.getMinVolume());
            return cs;
        }
        Integer oi = (HtrEnums.OptionType.CALL.equals(optionType) ? mdSnapshot.getCallOpenInterest().getValue() : mdSnapshot.getPutOpenInterest().getValue());
        if (isValidSize(oi) && oi < cp.getMinOpenInterest()) {
            cs.setMatch(false);
            cs.setDescription(mdSnapshot.getSymbol() + " open interest: " + oi + " < " + cp.getMinOpenInterest());
            return cs;
        }
        cs.setMatch(true);
        cs.setDescription("Constraints match");
        return cs;
    }
    
    public List<ReadinessStatus> getUnderlyingStatuses() {
        List<ReadinessStatus> statuses = optData.getContractPropertiesMap().keySet().stream().map(this::getReadinessStatus).collect(Collectors.toList());
        Collections.sort(statuses);
        return statuses;
    }
    
    private boolean isValidPrice(Double price) {
        return (price > 0d);
    }
    
    private boolean isValidSize(Integer size) {
        return (size >= 0);
    }
}
