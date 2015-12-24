package com.highpowerbear.hpbtrader.options.data;

import com.highpowerbear.hpbtrader.options.common.EventBroker;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.model.UnderlyingData;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.defintions.HtrEnums;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by robertk on 29.9.2015.
 */
@Named
@ApplicationScoped
public class ChainsRetriever {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    @Inject private OptData optData;
    @Inject EventBroker eventBroker;

    public void reloadOptionChains() {
        optData.getUnderlyingDataMap().keySet().forEach(this::retrieveOptionChains);
    }

    private void retrieveOptionChains(String underlying) {
        l.info("START request for loading option chains for underlying=" + underlying);
        UnderlyingData ud = optData.getUnderlyingDataMap().get(underlying);
        Calendar cal = HtrUtil.getNowCalendar();
        String thisMonth = OptUtil.toExpiryStringShort(cal);
        cal.add(Calendar.MONTH, +1);
        String nextMonth = OptUtil.toExpiryStringShort(cal);

        // call contracts, this month
        HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        com.ib.client.Contract ibContract = new com.ib.client.Contract();
        ibContract.m_symbol = underlying;
        ibContract.m_secType = HtrEnums.SecType.OPT.name();
        ibContract.m_expiry = thisMonth;
        ibContract.m_right = HtrEnums.OptionType.CALL.name();
        ibContract.m_exchange = HtrEnums.Exchange.SMART.name();
        ibContract.m_currency = HtrEnums.Currency.USD.name();
        ibContract.m_multiplier = HtrEnums.Multiplier.M_100.getValue();
        ibContract.m_includeExpired = false;
        int reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_CALL_THIS_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            //ibController.requestOptionChain(reqId, ibContract);
            // TODO
        }

        // call contracts, next month
        HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        ibContract = cloneIbOptionContract(ibContract);
        ibContract.m_expiry = nextMonth;
        reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_CALL_NEXT_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            //ibController.requestOptionChain(reqId, ibContract);
            // TODO
        }

        // put contracts, this month
        HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        ibContract = cloneIbOptionContract(ibContract);
        ibContract.m_expiry = thisMonth;
        ibContract.m_right = HtrEnums.OptionType.PUT.name();
        reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_PUT_THIS_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            //ibController.requestOptionChain(reqId, ibContract);
            // TODO
        }

        // put contracts, next month
        HtrUtil.waitMilliseconds(OptDefinitions.ONE_SECOND_MILLIS);
        ibContract = cloneIbOptionContract(ibContract);
        ibContract.m_expiry = nextMonth;
        reqId = ud.getIbRequestIdBase() + OptEnums.RequestIdOffset.CHAIN_PUT_NEXT_MONTH.getValue();
        if (optData.getOptionChainRequestMap().get(reqId) == null) {
            optData.getOptionChainRequestMap().put(reqId, underlying);
            //ibController.requestOptionChain(reqId, ibContract);
            // TODO
        }
        l.info("END request for loading option chains for underlying=" + underlying);
    }

    public void optionChainRequestCompleted(int reqId) {
        eventBroker.trigger(OptEnums.DataChangeEvent.OPT_CONTRACT);
        String underlying = optData.getOptionChainRequestMap().get(reqId);
        optData.getOptionChainRequestMap().remove(reqId);
        if (!optData.getOptionChainRequestMap().containsValue(underlying)) {
            optData.getUnderlyingDataMap().get(underlying).markChainsReady();
        }
    }

    private com.ib.client.Contract cloneIbOptionContract(com.ib.client.Contract contract) {
        com.ib.client.Contract clonedContract = new com.ib.client.Contract();
        clonedContract.m_symbol = contract.m_symbol;
        clonedContract.m_secType = contract.m_secType;
        clonedContract.m_expiry = contract.m_expiry;
        clonedContract.m_right = contract.m_right;
        clonedContract.m_exchange = contract.m_exchange;
        clonedContract.m_currency = contract.m_currency;
        clonedContract.m_multiplier = contract.m_multiplier;
        clonedContract.m_includeExpired = contract.m_includeExpired;
        return clonedContract;
    }
}
