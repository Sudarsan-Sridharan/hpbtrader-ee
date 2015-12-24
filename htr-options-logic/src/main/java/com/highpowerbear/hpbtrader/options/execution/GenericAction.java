package com.highpowerbear.hpbtrader.options.execution;

import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.model.ReadinessStatus;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.shared.entity.Trade;
import com.highpowerbear.hpbtrader.shared.persistence.OptionDao;
import com.ib.client.Order;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
public class GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject protected OptionDao optionDao;
    @Inject protected StatusChecker statusChecker;
    @Inject protected OptData optData;
    
    protected final String PROC_NOT_ENABLED = "processing not enabled";
    protected final String CONTR_CHANGE = "contract change in progress";
    protected final String OUT_TIME = "outside configured trading hours";
    protected final String CONVERT_OK = "OK";
    
    public Long process(String underlying, String name) {
        return null;
    }
    
    protected Trade createTrade(Trade activeTrade) {
        return null;
    }
    
    protected Order createOrder(Trade trade, ReadinessStatus rs) {
        return null;
    }

    protected boolean lock(String underlying, Trade activeTrade) {
        UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
        ud.lockCallContract();
        ud.lockPutContract();
        return true;
    }
    
    protected void release(String underlying, Trade activeTrade) {
        UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
        ud.releaseCallContract();
        ud.releasePutContract();
    }
}
