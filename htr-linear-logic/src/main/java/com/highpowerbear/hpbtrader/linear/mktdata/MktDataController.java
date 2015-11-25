package com.highpowerbear.hpbtrader.linear.mktdata;

import com.highpowerbear.hpbtrader.linear.common.EventBroker;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinSettings;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyLogic;
import com.highpowerbear.hpbtrader.linear.websocket.WebsocketController;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.persistence.BarDao;
import com.highpowerbear.hpbtrader.shared.persistence.SeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robertk on 4/13/14.
 */
@Named
@Singleton
public class MktDataController {
    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private SeriesDao seriesDao;
    @Inject private BarDao barDao;
    @Inject private StrategyDao strategyDao;
    @Inject private LinData linData;
    @Inject private StrategyController strategyController;
    @Inject private IbController ibController;
    @Inject private EventBroker eventBroker;
    @Inject private WebsocketController websocketController;

    private DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void processBars(Series series) {
        if (!series.getEnabled()) {
            l.info("Series not enabled, bars won't be processed, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
            return;
        }
        l.info("START processBars, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
        List<Bar> barsReceived = linData.getBarsReceivedMap().get(series.getId());
        Integer backfillStatus = linData.getBackfillStatusMap().get(series.getId());
        l.info("backfillStatus=" + backfillStatus);
        if (backfillStatus == null || backfillStatus == 0) {
            barDao.createBars(barsReceived);
            Strategy activeStrategy = strategyDao.getActiveStrategy(series);
            StrategyLogic strategyLogic = linData.getStrategyLogicMap().get(series.getId());
            if (barDao.getNumBars(series) >= HtrSettings.BARS_REQUIRED) {
                strategyController.process(activeStrategy, strategyLogic);
            }
            eventBroker.trigger(HtrEnums.DataChangeEvent.BAR_UPDATE);
        } else {
            linData.getBackfillStatusMap().put(series.getId(), --backfillStatus);
        }
        l.info("END processBars, seriesId=" + series.getId() + ", symbol=" + series.getSymbol());
    }
}
