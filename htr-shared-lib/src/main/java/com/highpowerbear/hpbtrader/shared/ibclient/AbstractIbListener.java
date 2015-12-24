package com.highpowerbear.hpbtrader.shared.ibclient;

import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;
import com.ib.client.*;

import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
public abstract class AbstractIbListener implements EWrapper {
    private static final Logger l = Logger.getLogger(HtrSettings.LOGGER);

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        l.fine(EWrapperMsgGenerator.tickPrice(tickerId, field, price, canAutoExecute));
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        l.fine(EWrapperMsgGenerator.tickSize(tickerId, field, size));
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        l.fine(EWrapperMsgGenerator.tickOptionComputation(tickerId, field, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice));
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        l.fine(EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value));
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        l.fine(EWrapperMsgGenerator.tickString(tickerId, tickType, value));
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {
        l.fine(EWrapperMsgGenerator.tickEFP(tickerId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureExpiry, dividendImpact, dividendsToExpiry));
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        l.fine(EWrapperMsgGenerator.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld));
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        l.fine(EWrapperMsgGenerator.openOrder(orderId, contract, order, orderState));
    }

    @Override
    public void openOrderEnd() {
        l.fine(EWrapperMsgGenerator.openOrderEnd());
    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {
        l.fine(EWrapperMsgGenerator.updateAccountValue(key, value, currency, accountName));
    }

    @Override
    public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        l.fine(EWrapperMsgGenerator.updatePortfolio(contract, position, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL, accountName));
    }

    @Override
    public void updateAccountTime(String timeStamp) {
        l.fine(EWrapperMsgGenerator.updateAccountTime(timeStamp));
    }

    @Override
    public void accountDownloadEnd(String accountName) {
        l.fine(EWrapperMsgGenerator.accountDownloadEnd(accountName));
    }

    @Override
    public void nextValidId(int orderId) {
        l.fine(EWrapperMsgGenerator.nextValidId(orderId));
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        l.fine(EWrapperMsgGenerator.contractDetails(reqId, contractDetails));
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        l.fine(EWrapperMsgGenerator.bondContractDetails(reqId, contractDetails));
    }

    @Override
    public void contractDetailsEnd(int reqId) {
        l.fine(EWrapperMsgGenerator.contractDetailsEnd(reqId));
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        l.fine(EWrapperMsgGenerator.execDetails(reqId, contract, execution));
    }

    @Override
    public void execDetailsEnd(int reqId) {
        l.fine(EWrapperMsgGenerator.execDetailsEnd(reqId));
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
        l.fine(EWrapperMsgGenerator.updateMktDepth(tickerId, position, operation, side, price, size));
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
        l.fine(EWrapperMsgGenerator.updateMktDepthL2(tickerId, position, marketMaker, operation, side, price, size));
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        l.fine(EWrapperMsgGenerator.updateNewsBulletin(msgId, msgType, message, origExchange));
    }

    @Override
    public void managedAccounts(String accountsList) {
        l.fine(EWrapperMsgGenerator.managedAccounts(accountsList));
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        l.fine(EWrapperMsgGenerator.receiveFA(faDataType, xml));
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        l.fine(EWrapperMsgGenerator.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps));
    }

    @Override
    public void scannerParameters(String xml) {
        l.fine(EWrapperMsgGenerator.scannerParameters(xml));
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
        l.fine(EWrapperMsgGenerator.scannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr));
    }

    @Override
    public void scannerDataEnd(int reqId) {
        l.fine(EWrapperMsgGenerator.scannerDataEnd(reqId));
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
        l.fine(EWrapperMsgGenerator.realtimeBar(reqId, time, open, high, low, close, volume, wap, count));
    }

    @Override
    public void currentTime(long time) {
        l.fine(EWrapperMsgGenerator.currentTime(time));
    }

    @Override
    public void fundamentalData(int reqId, String data) {
        l.fine(EWrapperMsgGenerator.fundamentalData(reqId, data));
    }

    @Override
    public void deltaNeutralValidation(int reqId, UnderComp underComp) {
        l.fine(EWrapperMsgGenerator.deltaNeutralValidation(reqId, underComp));
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        l.fine(EWrapperMsgGenerator.tickSnapshotEnd(reqId));
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        l.fine(EWrapperMsgGenerator.marketDataType(reqId, marketDataType));
    }

    @Override
    public void error(Exception e) {
        l.fine(EWrapperMsgGenerator.error(e));
    }

    @Override
    public void error(String str) {
        l.fine(EWrapperMsgGenerator.error(str));
    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        l.fine(EWrapperMsgGenerator.error(id, errorCode, errorMsg));
    }

    @Override
    public void connectionClosed() {
        l.fine(EWrapperMsgGenerator.connectionClosed());
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
        l.fine(EWrapperMsgGenerator.commissionReport(commissionReport));
    }
    
    @Override
    public void position(String account, Contract contract, int pos, double avgCost) {
        l.fine(EWrapperMsgGenerator.position(account, contract, pos, avgCost));
    }

    @Override
    public void positionEnd() {
        l.fine(EWrapperMsgGenerator.positionEnd());
    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        l.fine(EWrapperMsgGenerator.accountSummary(reqId, account, tag, value, currency));
    }

    @Override
    public void accountSummaryEnd(int reqId) {
        l.fine(EWrapperMsgGenerator.accountSummaryEnd(reqId));
    }

    @Override
    public void verifyMessageAPI(String s) {
    }

    @Override
    public void verifyCompleted(boolean b, String s) {
    }

    @Override
    public void displayGroupList(int i, String s) {
    }

    @Override
    public void displayGroupUpdated(int i, String s) {
    }
}
