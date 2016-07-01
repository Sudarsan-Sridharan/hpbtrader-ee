package com.highpowerbear.hpbtrader.strategy.process;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.Strategy;
import com.highpowerbear.hpbtrader.shared.model.GenericTuple;
import com.highpowerbear.hpbtrader.shared.model.TimeFrame;
import com.highpowerbear.hpbtrader.shared.persistence.StrategyDao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 29.6.2016.
 */
@ApplicationScoped
public class ProcessQueueManager {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Resource private ManagedExecutorService managedExecutorService;
    @Inject private StrategyDao strategyDao;
    @Inject private StrategyController ctrl;

    private Map<Strategy, BlockingQueue<String>> tradingQueueMap = new ConcurrentHashMap<>();
    private BlockingQueue<GenericTuple<Strategy, TimeFrame>> backtestQueue = new ArrayBlockingQueue<>(HtrDefinitions.BLOCKING_QUEUE_CAPACITY);
    private final String POISON_PROCESS = "NO_SERIES";
    private final GenericTuple<Strategy, TimeFrame> POISON_BACKTEST = new GenericTuple<>(null, null);

    @PostConstruct
    private void init() {
        initTrading();
        initBacktest();
    }

    @PreDestroy
    public void finish() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            queueProcessStrategy(strategy, POISON_PROCESS);
        }
        queueBacktestStrategy(POISON_BACKTEST);
        boolean stillRuning = true;
        while (stillRuning) {
            stillRuning = false;
            for (Strategy strategy : strategyDao.getStrategies()) {
                if (tradingQueueMap.get(strategy).peek() != null) {
                    stillRuning = true;
                }
            }
            if (backtestQueue.peek() != null) {
                stillRuning = true;
            }
            if (stillRuning) {
                HtrUtil.waitMilliseconds(100);
            }
        }
    }

    private void initTrading() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(HtrDefinitions.BLOCKING_QUEUE_CAPACITY);
            tradingQueueMap.put(strategy, queue);
            managedExecutorService.submit(() -> {
                String seriesAlias = null;
                while (true) {
                    try {
                        seriesAlias = queue.take();
                        l.info("Process strategy request detected, strategy id=" + strategy.getId() + ", triggered by series=" + seriesAlias);
                    } catch (InterruptedException ie) {
                        l.warning(ie.getMessage());
                    }
                    if (!Objects.equals(seriesAlias, POISON_PROCESS)) {
                        ctrl.processStrategy(ctrl.tradingLogicMap.get(strategy));
                    } else{
                        return;
                    }
                }
            });
        }
    }

    private void initBacktest() {
        managedExecutorService.submit(() -> {
            GenericTuple<Strategy, TimeFrame> backtestParam = null;
            while (true) {
                try {
                    backtestParam = backtestQueue.take();
                    l.info("Backtest strategy request detected, processing...");
                } catch (InterruptedException ie) {
                    l.warning(ie.getMessage());
                }
                if (!Objects.equals(backtestParam, POISON_BACKTEST)) {
                    if (backtestParam != null) {
                        Strategy strategy = backtestParam.getFirst();
                        TimeFrame timeFrame = backtestParam.getSecond();
                        if (timeFrame.isValid()) {
                            ctrl.backtestStatusMap.put(strategy, Boolean.FALSE);
                            ctrl.backtestStrategy(strategy, timeFrame.getFromDate(), timeFrame.getToDate());
                            ctrl.backtestStatusMap.put(strategy, Boolean.TRUE);
                        }
                    }
                } else {
                    return;
                }
            }
        });
    }

    public void queueProcessStrategy(Strategy strategy, String seriesAlias) {
        try {
            tradingQueueMap.get(strategy).put(seriesAlias);
        } catch (InterruptedException ie) {
            l.log(Level.SEVERE, "Error", ie);
        }
    }

    public void queueBacktestStrategy(GenericTuple<Strategy, TimeFrame> backtestParam) {
        try {
            backtestQueue.put(backtestParam);
        } catch (InterruptedException ie) {
            l.log(Level.SEVERE, "Error", ie);
        }
    }

}
