package com.highpowerbear.hpbtrader.mktdata.ibclient;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.mktdata.common.SingletonRepo;
import com.highpowerbear.hpbtrader.mktdata.process.HistDataController;
import com.highpowerbear.hpbtrader.mktdata.process.RtDataController;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
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

        if (date.startsWith(MktDefinitions.FINISH)) {
            histDataController.reqFinished(reqId);
        } else {
            Bar bar = new Bar();
            bar.setqOpen(open);
            bar.setHigh(high);
            bar.setLow(low);
            bar.setqClose(close);
            bar.setVolume(volume == -1 ? 0 : volume);
            bar.setCount(count);
            bar.setWap(WAP);
            bar.setHasGaps(hasGaps);
            histDataController.barReceived(reqId, date, bar);
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
