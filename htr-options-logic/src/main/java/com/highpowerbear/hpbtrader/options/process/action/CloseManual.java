package com.highpowerbear.hpbtrader.options.process.action;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.entity.IbOrder;
import com.highpowerbear.hpbtrader.options.entity.InputSignal;
import com.highpowerbear.hpbtrader.options.entity.Trade;
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
public class CloseManual extends GenericAction {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Override
    public Long process(String underlying, String name) {
        InputSignal inputSignal = createSignal(underlying, name, OptEnums.SignalAction.CLOSE);
        inputSignal.setOrigin(OptEnums.SignalOrigin.MANUAL);
        optDao.addSignal(inputSignal);
        List<Trade> activeTrades = optDao.getActiveTrades(underlying);
        if (activeTrades.isEmpty()) {
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
        activeTrade.addInitEvent(OptEnums.TradeStatus.OPEN.equals(activeTrade.getTradeStatus()) ? OptEnums.TradeStatus.INIT_FIRST_EXIT : OptEnums.TradeStatus.INIT_CLOSE);
        optDao.updateTrade(activeTrade);
        
        IbOrder ibOrder = createOrder(activeTrade, rs);
        ibOrder.setInputSignal(inputSignal);
        optDao.addOrder(ibOrder);
        ibController.submitIbOrder(ibOrder);
        
        inputSignal.setSignalStatus(OptEnums.SignalStatus.CONVERTED);
        inputSignal.setStatusDescription(CONVERT_OK);
        optDao.updateSignal(inputSignal);
        l.info("Signal --> Trade --> Order: " + inputSignal.print() + " --> " + activeTrade.print() + "-->" + ibOrder.print());
        
        release(inputSignal, activeTrade);
        return inputSignal.getId();
    }
}
