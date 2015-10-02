package com.highpowerbear.hpbtrader.options.ibclient;

import com.highpowerbear.hpbtrader.options.common.*;
import com.highpowerbear.hpbtrader.options.data.ChainsRetriever;
import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.entity.OptionContract;
import com.highpowerbear.hpbtrader.options.entity.Order;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.data.DataRetriever;
import com.highpowerbear.hpbtrader.options.data.OptionDataRetriever;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.OrderState;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
public class IbListenerImpl extends GenericIbListener {
    @Inject private OptDao optDao;
    @Inject private IbController ibController;
    @Inject private DataRetriever dataRetriever;
    @Inject private OptionDataRetriever optionDataRetriever;
    @Inject private ChainsRetriever chainsRetriever;
    @Inject private OptData optData;
    @Inject private EventBroker eventBroker;
    
    private void updateHeartbeat(Order o) {
        Integer failedHeartbeatsLeft = optData.getOpenOrderHeartbeatMap().get(o.getId());
        if (failedHeartbeatsLeft != null) {
            optData.getOpenOrderHeartbeatMap().put(o.getId(), (failedHeartbeatsLeft < OptDefinitions.MAX_ORDER_HEARTBEAT_FAILS ? failedHeartbeatsLeft + 1 : failedHeartbeatsLeft));
        }
    }
    
    private void removeHeartbeat(Order o) {
        Integer failedHeartbeatsLeft = optData.getOpenOrderHeartbeatMap().get(o.getId());
        if (failedHeartbeatsLeft != null) {
            optData.getOpenOrderHeartbeatMap().remove(o.getId());
        }
    }
 
    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        super.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        
        if (!(  IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) || 
                IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) || 
                IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status))) 
        {
            return;
        }
        Order order = optDao.getOrderByIbPermId(permId);
        if (order == null) {
            return;
        }
        if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && OptEnums.OrderStatus.SUBMITTED.equals(order.getOrderStatus())) {
            updateHeartbeat(order);
            eventBroker.trigger(OptEnums.DataChangeEvent.OPT_ORDER);
        } else if (IbApiEnums.OrderStatus.SUBMITTED.getName().equalsIgnoreCase(status) && !OptEnums.OrderStatus.SUBMITTED.equals(order.getOrderStatus())) {
            updateHeartbeat(order);
            order.addEvent(OptEnums.OrderStatus.SUBMITTED);
            optDao.updateOrder(order);
        } else if (IbApiEnums.OrderStatus.CANCELLED.getName().equalsIgnoreCase(status) && !OptEnums.OrderStatus.EXT_CANCELED.equals(order.getOrderStatus())) {
            order.addEvent(OptEnums.OrderStatus.EXT_CANCELED);
            optDao.updateOrder(order);
            Trade trade = order.getTrade();
            trade.addEventByOrderCanceled(order);
            optDao.updateTrade(trade);
            removeHeartbeat(order);
        } else if (IbApiEnums.OrderStatus.FILLED.getName().equalsIgnoreCase(status) && remaining == 0 && !OptEnums.OrderStatus.FILLED.equals(order.getOrderStatus())) {
            order.addEvent(OptEnums.OrderStatus.FILLED);
            order.setFillPrice(avgFillPrice);
            optDao.updateOrder(order);
            Trade trade = order.getTrade();
            trade.addEventByOrderFilled(order);
            optDao.updateTrade(trade);
            removeHeartbeat(order);
        }
    }

    @Override
    public void openOrder(int orderId, Contract contract, com.ib.client.Order ibOrder, OrderState orderState) {
        super.openOrder(orderId, contract, ibOrder, orderState);
        
        Order order = optDao.getOrderByIbOrderId(orderId);
        if (order != null && order.getIbPermId() == null) {
            order.setIbPermId(ibOrder.m_permId);
            optDao.updateOrder(order);
        }
    }
    
    @Override
    public void nextValidId(int orderId) {
        super.nextValidId(orderId);
        ibController.setNextValidOrderId(orderId);
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        //l.debug("reqId=" + reqId + ", contract=" + contractDetails.m_summary.m_localSymbol + ". epiry=" + contractDetails.m_summary.m_expiry);
        if (IbApiEnums.SecType.OPT.getName().equals(contractDetails.m_summary.m_secType)) {
            OptionContract optionContract = new OptionContract();
            optionContract.setOptionSymbol(contractDetails.m_summary.m_localSymbol);
            optionContract.setUnderlying(OptEnums.Underlying.valueOf(contractDetails.m_summary.m_symbol));
            optionContract.setExpiry(OptUtil.expiryFullToCalendar(contractDetails.m_summary.m_expiry));
            optionContract.setOptionType(IbApiEnums.OptionType.getByName(contractDetails.m_summary.m_right));
            optionContract.setStrike(contractDetails.m_summary.m_strike);
            optDao.addOptionContract(optionContract);
        }

    }

    @Override
    public void contractDetailsEnd(int reqId) {
        super.contractDetailsEnd(reqId);
        chainsRetriever.optionChainRequestCompleted(reqId);
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        dataRetriever.updateRealtimeData(tickerId, field, price);
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        dataRetriever.updateRealtimeData(tickerId, field, size);
    }
    
    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        dataRetriever.updateRealtimeData(tickerId, tickType, value);
    }
    
    @Override
    public void tickString(int tickerId, int tickType, String value) {
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
    }
    
    @Override
    public void error(int id, int errorCode, String errorMsg) {
        super.error(id, errorCode, errorMsg);
        if (IbApiEnums.ErrorCode.NO_SECURITY_DEFINTION.getValue().equals(errorCode)) {
            if (optData.getOptionChainRequestMap().containsKey(id)) {
                optData.getOptionChainRequestMap().remove(id);
            }
        }
    }
}
