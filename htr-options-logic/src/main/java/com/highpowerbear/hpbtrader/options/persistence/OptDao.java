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
    void addSignal(InputSignal inputSignal);
    void updateSignal(InputSignal inputSignal);
    boolean existsSignal(Long signalId);
    List<InputSignal> getSignals(String underlying);
    void addOrder(IbOrder ibOrder);
    void updateOrder(IbOrder ibOrder);
    IbOrder getOrder(Long id);
    IbOrder getOrderByIbPermId(Integer ibPermId);
    IbOrder getOrderByIbOrderId(Integer ibOrderId);
    List<IbOrder> getOrders(String underlying);
    List<IbOrder> getOrdersBySignalId(Long signalId);
    List<IbOrder> getNewRetryOrders();
    List<IbOrder> getOpenOrders();
    void addTrade(Trade trade);
    void updateTrade(Trade trade);
    List<Trade> getTrades(String underlying);
    List<Trade> getActiveTrades(String underlying);
    Trade getActiveTrade(String underlying, IbApiEnums.OptionType optionType);
    List<Position> getPosition(String underlying);
    void addContractLog(ContractLog contractLog);
    List<ContractLog> getContractLogs(String underlying);
}