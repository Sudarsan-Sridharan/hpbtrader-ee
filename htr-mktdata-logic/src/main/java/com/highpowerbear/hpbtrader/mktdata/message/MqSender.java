package com.highpowerbear.hpbtrader.mktdata.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;

import javax.ejb.Stateless;
import java.util.logging.Logger;

/**
 * Created by robertk on 25.11.2015.
 */
@Stateless
public class MqSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);
    // TODO
    public void notifyBarsAdded(DataSeries dataSeries) {
    }
}
