package com.highpowerbear.hpbtrader.options.persistence;

import com.highpowerbear.hpbtrader.options.entity.*;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import com.highpowerbear.hpbtrader.options.model.Position;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rkolar
 */
public interface OptDao {
    void addOptionContract(OptionContract optionContract);
    String getCallSymbol(String underlying, Calendar minExpiry, Double maxStrike);
    String getPutSymbol(String underlying, Calendar minExpiry, Double minStrike);
    List<OptionContract> getOptionContracts(Set<String> underlyings);
    void addSignal(InputSentiment inputSentiment);
    void updateSignal(InputSentiment inputSentiment);
    boolean existsSignal(Long signalId);
    List<InputSentiment> getSignals(String underlying);
    void addOrder(OptionOrder optionOrder);
    void updateOrder(OptionOrder optionOrder);
    OptionOrder getOrder(Long id);
    OptionOrder getOrderByIbPermId(Integer ibPermId);
    OptionOrder getOrderByIbOrderId(Integer ibOrderId);
    List<OptionOrder> getOrders(String underlying);
    List<OptionOrder> getOrdersBySignalId(Long signalId);
    List<OptionOrder> getNewRetryOrders();
    List<OptionOrder> getOpenOrders();
    void addTrade(Trade trade);
    void updateTrade(Trade trade);
    List<Trade> getTrades(String underlying);
    List<Trade> getActiveTrades(String underlying);
    Trade getActiveTrade(String underlying, IbApiEnums.OptionType optionType);
    List<Position> getPosition(String underlying);
}