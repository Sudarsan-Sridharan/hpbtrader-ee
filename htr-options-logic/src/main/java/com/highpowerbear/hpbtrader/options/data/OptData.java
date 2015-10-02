package com.highpowerbear.hpbtrader.options.data;

import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.Order;
import com.highpowerbear.hpbtrader.options.model.ContractProperties;
import com.highpowerbear.hpbtrader.options.model.MarketData;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class OptData {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    // if a map can be modified from two different threads at the same time, use ConcurrentHashMap
    
    private Map<String, ContractProperties> contractPropertiesMap = new ConcurrentHashMap<>(); // underlying --> contractProperties
    private Map<String, UnderlyingData> underlyingDataMap = new ConcurrentHashMap<>(); // underlying --> underlyingData

    private Map<Integer, String> optionChainRequestMap = new ConcurrentHashMap<>(); // requestId --> underlying symbol
    private Map<Integer, String> marketDataRequestMap = new ConcurrentHashMap<>(); // requestId --> contract symbol (stock or option)
    private Map<String, MarketData> marketDataMap = new ConcurrentHashMap<>(); // contract symbol (stock or option) --> marketData

    private Map<Long, Integer> openOrderHeartbeatMap = new ConcurrentHashMap<>(); // order dbId --> number of failed heartbeats left before declaring order UNKNOWN

    public Map<String, ContractProperties> getContractPropertiesMap() {
        return contractPropertiesMap;
    }

    public Map<String, UnderlyingData> getUnderlyingDataMap() {
        return underlyingDataMap;
    }

    public Map<Integer, String> getOptionChainRequestMap() {
        return optionChainRequestMap;
    }

    public Map<Integer, String> getMarketDataRequestMap() {
        return marketDataRequestMap;
    }

    public Map<String, MarketData> getMarketDataMap() {
        return marketDataMap;
    }

    public Map<Long, Integer> getOpenOrderHeartbeatMap() {
        return openOrderHeartbeatMap;
    }

    public List<ContractProperties> getContractPropertiesRows() {
        l.fine("Getting contract properties rows from contractPropertiesMap");
        List<ContractProperties> contractPropertiesRows = new ArrayList<>(contractPropertiesMap.values());
        Collections.sort(contractPropertiesRows);
        return contractPropertiesRows;
    }
    
    public List<MarketData> getPurchasedDataRows() {
        List<MarketData> purchasedDataRows = new ArrayList<>();
        for (UnderlyingData ud : underlyingDataMap.values()) {
            purchasedDataRows.addAll(ud.getPurchasedSymbols().stream().map(marketDataMap::get).collect(Collectors.toList()));
        }
        Collections.sort(purchasedDataRows);
        return purchasedDataRows;
    }
    
    public List<MarketData> getMarketDataRows() {
        List<MarketData> marketDataRows = new ArrayList<>(marketDataMap.values());
        Collections.sort(marketDataRows);
        return marketDataRows;
    }
    
    public boolean existsUnderlying(String underlying) {
        return (underlyingDataMap.keySet().contains(underlying));
    }
    
    public Integer getHeartbeat(Order order) {
        return openOrderHeartbeatMap.get(order.getId());
    }
}