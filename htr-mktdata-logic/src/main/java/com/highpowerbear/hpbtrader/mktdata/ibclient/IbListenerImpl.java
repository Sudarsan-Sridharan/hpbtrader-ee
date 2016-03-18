package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.SingletonRepo;
import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.mktdata.process.RtDataController;
import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.ibclient.AbstractIbListener;

/**
 *
 * @author rkolar
 */
public class IbListenerImpl extends AbstractIbListener {
    private HistDataController histDataController = SingletonRepo.getInstance().getHistDataController();
    private RtDataController rtDataController = SingletonRepo.getInstance().getRtDataController();

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
        //super.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);

        if (date.startsWith(HtrDefinitions.FINISH)) {
            histDataController.reqFinished(reqId);
        } else {
            DataBar dataBar = new DataBar();
            dataBar.setbOpen(open);
            dataBar.setHigh(high);
            dataBar.setLow(low);
            dataBar.setbClose(close);
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
}
