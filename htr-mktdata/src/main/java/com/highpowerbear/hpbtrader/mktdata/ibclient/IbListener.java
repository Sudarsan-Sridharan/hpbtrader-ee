package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.mktdata.process.RtDataController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.IbAccount;
import com.highpowerbear.hpbtrader.shared.ibclient.GenerictIbListener;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author rkolar
 */
@Dependent
public class IbListener extends GenerictIbListener {

    @Inject private HistDataController histDataController;
    @Inject private RtDataController rtDataController;
    @Inject private IbController ibController;

    private IbAccount ibAccount;

    public IbListener configure(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
        return this;
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        //super.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);

        if (date.startsWith(HtrDefinitions.FINISH)) {
            histDataController.reqFinished(reqId);
        } else {
            DataBar dataBar = new DataBar();
            dataBar.setbBarOpen(open);
            dataBar.setbBarHigh(high);
            dataBar.setbBarLow(low);
            dataBar.setbBarClose(close);
            dataBar.setVolume(volume == -1 ? 0 : volume);
            dataBar.setCount(count);
            dataBar.setWap(WAP);
            dataBar.setHasGaps(hasGaps);
            histDataController.barReceived(reqId, date, dataBar);
        }
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        rtDataController.tickPriceReceived(tickerId, field, price);
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        rtDataController.tickSizeReceived(tickerId, field, size);
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        rtDataController.tickGenericReceived(tickerId, tickType, value);
    }

    @Override
    public void managedAccounts(String accountsList) {
        super.managedAccounts(accountsList);
        ibController.getIbConnection(ibAccount).setAccounts(accountsList);
    }
}
