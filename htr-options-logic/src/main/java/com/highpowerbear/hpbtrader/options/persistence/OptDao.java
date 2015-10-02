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
    void addOrder(Order order);
    void updateOrder(Order order);
    Order getOrder(Long id);
    Order getOrderByIbPermId(Integer ibPermId);
    Order getOrderByIbOrderId(Integer ibOrderId);
    List<Order> getOrders(String underlying);
    List<Order> getOrdersBySignalId(Long signalId);
    List<Order> getNewRetryOrders();
    List<Order> getOpenOrders();
    void addTrade(Trade trade);
    void updateTrade(Trade trade);
    List<Trade> getTrades(String underlying);
    List<Trade> getActiveTrades(String underlying);
    Trade getActiveTrade(String underlying, IbApiEnums.OptionType optionType);
    List<Position> getPosition(String underlying);
}