package com.highpowerbear.hpbtrader.options.process;

import com.highpowerbear.hpbtrader.options.entity.IbOrder;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.model.Position;
import com.highpowerbear.hpbtrader.options.model.TradeLot;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.process.action.*;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Named
@Singleton
public class SignalProcessor {
    @Inject protected OptDao optDao;
    @Inject private OpenLong openLong;
    @Inject private OpenShort openShort;
    @Inject private Reverse reverse;
    @Inject private Close close;
    @Inject private CloseManual closeManual;
    
    public synchronized Long openLong(String underlying, String name) {
        return openLong.process(underlying, name);
    }
    
    public synchronized Long openShort(String underlying, String name) {
        return openShort.process(underlying, name);
    }
    
    public synchronized Long close(String underlying, String name) {
        return close.process(underlying, name);
    }
    
    public synchronized Long closeManual(TradeLot tradeLot) {
        Trade trade = tradeLot.getTrade();
        String underlying = trade.getUnderlying();
        String name = "Manual close " + trade.getId() + "/" + tradeLot.getLot().getNumber();
        return closeManual.process(underlying, name);
    }
    
    public synchronized Long reverse(String underlying, String name) {
        return reverse.process(underlying, name);
    }
    
    public String orderStatus(Long signalID) {
        // example: FILLED,SUBMITTED (if reversal signal)
        List<IbOrder> ibOrders = optDao.getOrdersBySignalId(signalID);
        StringBuilder sb = new StringBuilder();
        for (IbOrder o : ibOrders) {
            sb.append(o.getOrderStatus()).append(",");
        }
        if (sb.lastIndexOf(",") > 0 ) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }
    
    public String activeTrade(String underlying) {
        // example: INIT_OPEN
        List<Trade> activeTrades = optDao.getActiveTrades(underlying);
        Trade activeTrade = (!activeTrades.isEmpty() ? activeTrades.get(0) : null);
        return (activeTrade != null ? activeTrade.getTradeStatus().toString() : null);
    }
    
    public String position(String underlying) {
        // example: SPY   131025C00172000=50,SPY   131025P00172000=50
        List<Position> positionList = optDao.getPosition(underlying);
        StringBuilder sb = new StringBuilder();
        for (Position p : positionList) {
            sb.append(p.getSymbol()).append("=").append(p.getPosition()).append(",");
        }
        if (sb.lastIndexOf(",") > 0 ) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return (!positionList.isEmpty() ? sb.toString() : null);
    }
}