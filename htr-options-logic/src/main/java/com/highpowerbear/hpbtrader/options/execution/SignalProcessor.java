package com.highpowerbear.hpbtrader.options.execution;

import com.highpowerbear.hpbtrader.options.model.Position;
import com.highpowerbear.hpbtrader.options.execution.action.*;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.persistence.OptionDao;
import com.ib.client.Order;

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
    @Inject protected OptionDao optionDao;
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

    public synchronized Long reverse(String underlying, String name) {
        return reverse.process(underlying, name);
    }
    
    public String orderStatus(Long signalID) {
        return null;
    }
    
    public String activeTrade(String underlying) {
        return null;
    }
    
    public String position(String underlying) {
        return null;
    }
}