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
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
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
    private final String POISON_PROCESS = "POISON_PROCESS";
    private final GenericTuple<Strategy, TimeFrame> POISON_BACKTEST = new GenericTuple<>(null, null);

    private void init(@Observes @Initialized(ApplicationScoped.class) Object evt) { // mechanism for cdi eager initialization without using singleton ejb
        l.info("BEGIN ProcessQueueManager.init");
        initTrading();
        initBacktest();
        l.info("END ProcessQueueManager.init");
    }

    private void finish(@Observes @Destroyed(ApplicationScoped.class) Object evt) {
        l.info("BEGIN ProcessQueueManager.finish");
        for (Strategy strategy : strategyDao.getStrategies()) {
            queueProcessStrategy(strategy, POISON_PROCESS);
        }
        queueBacktestStrategy(POISON_BACKTEST);
        boolean stillRuning = true;
        while (stillRuning) {
            stillRuning = false;
            for (BlockingQueue<String> tradingQueue : tradingQueueMap.values()) {
                if (tradingQueue.peek() != null) {
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
        l.info("END ProcessQueueManager.finish");
    }

    private void initTrading() {
        for (Strategy strategy : strategyDao.getStrategies()) {
            if (strategy.isActive()) {
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
                        if (!Objects.equals(POISON_PROCESS, seriesAlias)) {
                            ctrl.processStrategy(strategy);
                        } else {
                            return;
                        }
                    }
                });
            }
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
                            ctrl.backtestInProgressMap.put(strategy, Boolean.TRUE);
                            ctrl.backtestStrategy(strategy, timeFrame.getFromDate(), timeFrame.getToDate());
                            ctrl.backtestInProgressMap.remove(strategy);
                        }
                    }
                } else {
                    return;
                }
            }
        });
    }

    public void queueProcessStrategy(Strategy strategy, String seriesAlias) {
        if (strategy.isActive()) {
            try {
                BlockingQueue<String> queue = tradingQueueMap.get(strategy);
                if (queue != null) {
                    int rc = tradingQueueMap.get(strategy).remainingCapacity();
                    l.info("Adding item to trading queue, strategy=" + strategy.getId() + ", seriesAlias=" + seriesAlias + ", remaining capacity=" + rc);
                    tradingQueueMap.get(strategy).put(seriesAlias);
                }
            } catch (InterruptedException ie) {
                l.log(Level.SEVERE, "Error", ie);
            }
        }
    }

    public void queueBacktestStrategy(GenericTuple<Strategy, TimeFrame> backtestParam) {
        try {
            int rc = backtestQueue.remainingCapacity();
            Integer strategyId = backtestParam.getFirst() != null ? backtestParam.getFirst().getId() : null;
            l.info("Adding item to backtest queue, strategy=" + strategyId + ", remaining capacity=" + rc);
            backtestQueue.put(backtestParam);
        } catch (InterruptedException ie) {
            l.log(Level.SEVERE, "Error", ie);
        }
    }
}
