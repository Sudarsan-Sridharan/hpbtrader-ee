package com.highpowerbear.hpbtrader.options.process.action;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.IbOrder;
import com.highpowerbear.hpbtrader.options.entity.InputSignal;
import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.model.ConstraintsStatus;
import com.highpowerbear.hpbtrader.options.model.ContractProperties;
import com.highpowerbear.hpbtrader.options.model.ReadinessStatus;
import com.highpowerbear.hpbtrader.options.process.GenericAction;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
@Named
@ApplicationScoped
public class OpenLong extends GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Override
    public Long process(String underlying, String name) {
        InputSignal inputSignal = createSignal(underlying, name, OptEnums.SignalAction.OPEN_LONG);
        optDao.addSignal(inputSignal);
        if (!OptDefinitions.PROCESS_ENABLED) {
            setNotAccepted(inputSignal, PROC_NOT_ENABLED);
            return null;
        }
        ContractProperties cp = optData.getContractPropertiesMap().get(inputSignal.getUnderlying());
        if (!cp.isTradingTime()) {
            setNotAccepted(inputSignal, OUT_TIME);
            return null;
        }
        List<Trade> activeTrades = optDao.getActiveTrades(underlying);
        // no trade must be left open to allow opening a new trade
        if (!activeTrades.isEmpty()) {
            setNotAccepted(inputSignal, OptUtil.printTrades(activeTrades));
            return null;
        }
        if (!lock(inputSignal, null)) {
            setNotAccepted(inputSignal, CONTR_CHANGE);
            return null;
        }
        ReadinessStatus rs = statusChecker.getReadinessStatus(inputSignal.getUnderlying());
        if (!rs.isReady()) {
            setNotAccepted(inputSignal, rs.getDescription());
            release(inputSignal, null);
            return null;
        }
        Trade trade = createTrade(inputSignal, null);
        if(OptDefinitions.CONSTRAINTS_ENABLED) {
            ConstraintsStatus cs = statusChecker.getConstraintsStatus(trade, rs);
            if(!cs.isMatch()) {
                setNotAccepted(inputSignal, cs.getDescription());
                release(inputSignal, null);
                return null;
            }
        }
        optDao.addTrade(trade);
        
        IbOrder ibOrder = createOrder(trade, rs);
        ibOrder.setInputSignal(inputSignal);
        optDao.addOrder(ibOrder);
        
        ibController.submitIbOrder(ibOrder);
        inputSignal.setSignalStatus(OptEnums.SignalStatus.CONVERTED);
        inputSignal.setStatusDescription(CONVERT_OK);
        optDao.updateSignal(inputSignal);
        l.info("Signal --> Trade --> Order: " + inputSignal.print() + " --> " + trade.print() + "-->" + ibOrder.print());
        
        release(inputSignal, null);
        return inputSignal.getId();
    }
}
