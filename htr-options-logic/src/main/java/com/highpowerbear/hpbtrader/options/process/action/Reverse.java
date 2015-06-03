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
public class Reverse extends GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Override
    public Long process(String underlying, String name) {
        InputSignal inputSignal = createSignal(underlying, name, OptEnums.SignalAction.REVERSE);
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
        // exactly one trade must be active (open) to allow a reversal signal
        if (activeTrades.size() != 1) {
            setNotAccepted(inputSignal, OptUtil.printTrades(activeTrades));
            return null;
        }
        Trade activeTrade = activeTrades.get(0);
        if (!activeTrade.canAcceptSignal(inputSignal.getAction())) {
            setNotAccepted(inputSignal, OptUtil.printTrades(activeTrades));
            return null;
        }
        if (!lock(inputSignal, activeTrade)) {
            setNotAccepted(inputSignal, CONTR_CHANGE);
            return null;
        }
        ReadinessStatus rs = statusChecker.getReadinessStatus(inputSignal.getUnderlying());
        if (!rs.isReady()) {
            setNotAccepted(inputSignal, rs.getDescription());
            release(inputSignal, activeTrade);
            return null;
        }
        if (OptDefinitions.CONSTRAINTS_ENABLED) {
            ConstraintsStatus cs = statusChecker.getConstraintsStatus(activeTrade, rs);
            if(!cs.isMatch()) {
                setNotAccepted(inputSignal, cs.getDescription());
                release(inputSignal, activeTrade);
                return null;
            }
        }
        Trade reverseTrade = createTrade(inputSignal, activeTrade);
        if (OptDefinitions.CONSTRAINTS_ENABLED) {
            ConstraintsStatus cs = statusChecker.getConstraintsStatus(reverseTrade, rs);
            if(!cs.isMatch()) {
                setNotAccepted(inputSignal, cs.getDescription());
                release(inputSignal, activeTrade);
                return null;
            }
        }
        
        // close active trade
        activeTrade.addInitEvent(OptEnums.TradeStatus.INIT_CLOSE);
        optDao.updateTrade(activeTrade);
        
        IbOrder closingIbOrder = createOrder(activeTrade, rs);
        closingIbOrder.setInputSignal(inputSignal);
        optDao.addOrder(closingIbOrder);
        
        // opening new reverse trade
        optDao.addTrade(reverseTrade);
        
        IbOrder openingIbOrder = createOrder(reverseTrade, rs);
        openingIbOrder.setInputSignal(inputSignal);
        optDao.addOrder(openingIbOrder);
        
        ibController.submitIbOrder(closingIbOrder);
        ibController.submitIbOrder(openingIbOrder);
        inputSignal.setSignalStatus(OptEnums.SignalStatus.CONVERTED);
        inputSignal.setStatusDescription(CONVERT_OK);
        optDao.updateSignal(inputSignal);
        l.info("Signal --> activeTrade + reverseTrade --> closingOrder + openingOrder: " + inputSignal.print() + " --> " + activeTrade.print() + " + " + reverseTrade.print() + "-->" + closingIbOrder.print() + " + " + openingIbOrder.print());
        
        release(inputSignal, activeTrade);
        return inputSignal.getId();
    }
}
