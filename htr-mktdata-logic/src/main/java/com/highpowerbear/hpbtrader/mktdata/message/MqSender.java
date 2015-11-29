package com.highpowerbear.hpbtrader.mktdata.message;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.Series;

import javax.ejb.Stateless;
import java.util.logging.Logger;

/**
 * Created by robertk on 25.11.2015.
 */
@Stateless
public class MqSender {
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);
    // TODO
    public void notifyBarsAdded(Series series) {
    }
}
