package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.persistence.TradeDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;
import com.highpowerbear.hpbtrader.strategy.common.SingletonRepo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
public abstract class AbstractStrategyLogic implements StrategyLogic {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);
    protected final int INDICATORS_LIST_SIZE = 10;

    protected DataSeriesDao dataSeriesDao = SingletonRepo.getInstance().getDataSeriesDao();
    protected TradeDao tradeDao = SingletonRepo.getInstance().getTradeDao();

    protected Strategy strategy;
    protected DataSeries inputDataSeries;
    protected TiCalculator tiCalculator;
    protected NumberFormat doubleValueFormat;

    // update before each processing in preflight method
    protected List<DataBar> dataBars;
    protected DataBar lastDataBar;
    protected DataBar prevDataBar;
    protected Trade activeTrade;

    protected boolean backtest = false;

    protected IbOrder resultIbOrder;

    public AbstractStrategyLogic(Strategy strategy) {
        this.strategy = strategy;
        this.inputDataSeries = dataSeriesDao.getSeriesByAlias(strategy.getDefaultInputSeriesAlias());
        this.tiCalculator = SingletonRepo.getInstance().getTiCalculator();
        this.doubleValueFormat = NumberFormat.getInstance(Locale.US);
        doubleValueFormat.setMinimumFractionDigits(6);
        doubleValueFormat.setMaximumFractionDigits(6);
    }

    @Override
    public boolean preflight(boolean backtest) {
        this.backtest = backtest;
        l.info("START preflight " + getInfo());

        this.dataBars = dataSeriesDao.getBars(inputDataSeries, HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE);
        this.lastDataBar = dataBars.get(dataBars.size() - 1);
        this.prevDataBar = dataBars.get(dataBars.size() - 2);
        this.activeTrade = tradeDao.getActiveTrade(strategy);

        if (dataBars.size() < HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            l.info("END preflight " +  getInfo() + ", not enough  bars available");
            return false;
        }
        long intervalMillis = inputDataSeries.getInterval().getMillis();
        long nowMillis = HtrUtil.getCalendar().getTimeInMillis();
        boolean isCurrentBar = ((lastDataBar.getBarCloseDateMillis() + intervalMillis) > nowMillis);
        if (!isCurrentBar) {
            l.info("END preflight " + getInfo() + ", not current bar");
            return false;
        }
        if (activeTrade != null && activeTrade.isInit()) {
            l.info("END preflight " + getInfo() + ", active trade in state " + activeTrade.getTradeStatus());
            return false;
        }

        reloadParameters();
        calculateIndicators();

        return true;
    }

    @Override
    public abstract IbOrder process();

    @Override
    public String getInfo() {
        return inputDataSeries.getInstrument().getSymbol() + ", " + inputDataSeries.getInstrument().getCurrency() + ", " + inputDataSeries.getInterval().name() + ", " + strategy.getStrategyType() + " --> " + this.getClass().getSimpleName();
    }

    protected abstract void calculateIndicators();
    protected abstract void reloadParameters();
    
    protected void createOrder() {
        resultIbOrder = new IbOrder();
        resultIbOrder.setStrategy(strategy);
        resultIbOrder.setStrategyMode(strategy.getStrategyMode());
        resultIbOrder.setSubmitType(HtrEnums.SubmitType.AUTO);
        resultIbOrder.setQuantity(strategy.getTradingQuantity());
        resultIbOrder.setOrderType(HtrEnums.OrderType.MKT);
        resultIbOrder.setLimitPrice(null); // N/A for market order
        resultIbOrder.setStopPrice(null); // N/A for market order
        resultIbOrder.addEvent(HtrEnums.IbOrderStatus.NEW, (backtest ? lastDataBar.getBarCloseDate() : HtrUtil.getCalendar()), null);
    }

    protected Double getPrice() {
        return lastDataBar.getbBarClose();
    }

    protected boolean targetMet() {
        if (activeTrade == null || activeTrade.getProfitTarget() == null) {
            return false;
        }
        return (activeTrade.isLong() ? (lastDataBar.getbBarClose() >= activeTrade.getProfitTarget()) : (lastDataBar.getbBarClose() <= activeTrade.getProfitTarget()));
    }

    protected boolean stopTriggered() {
        if (activeTrade == null || activeTrade.getStopLoss() == null) {
            return false;
        }
        return (activeTrade.isLong() ? (lastDataBar.getbBarClose() <= activeTrade.getStopLoss()) : (lastDataBar.getbBarClose() >= activeTrade.getStopLoss()));
    }

    protected Double calculateInitialStop(Double stopPct) {
        return activeTrade.isLong() ? HtrUtil.round5(lastDataBar.getbBarClose() - (stopPct / 100.0) * lastDataBar.getbBarClose()) : HtrUtil.round5(lastDataBar.getbBarClose() + (stopPct / 100.0) * lastDataBar.getbBarClose());
    }

    protected void setTrailStop(Double stopPct) {
        if (activeTrade == null) {
            return;
        }
        if (activeTrade.isLong()) {
            if (lastDataBar.getbBarClose() > prevDataBar.getbBarClose()) {
                Double newStop = HtrUtil.round5(lastDataBar.getbBarClose() - (stopPct / 100.0) * lastDataBar.getbBarClose());
                if (newStop > activeTrade.getStopLoss()) {
                    activeTrade.setStopLoss(newStop);
                }
            }
        } else {
            if (lastDataBar.getbBarClose() < prevDataBar.getbBarClose()) {
                Double newStop = HtrUtil.round5(lastDataBar.getbBarClose() + (stopPct / 100.0) * lastDataBar.getbBarClose());
                if (newStop < activeTrade.getStopLoss()) {
                    activeTrade.setStopLoss(newStop);
                }
            }
        }
    }

    protected Double calculateTarget(Double targetPct) {
        return activeTrade.isLong() ? HtrUtil.round5(lastDataBar.getbBarClose() + (targetPct / 100.0) * lastDataBar.getbBarClose()) : HtrUtil.round5(lastDataBar.getbBarClose() - (targetPct / 100.0) * lastDataBar.getbBarClose());
    }

    protected void setPl() {
        if (activeTrade == null || activeTrade.getOpenPrice() == null) {
            return;
        }
        Double unrealizedPl = (activeTrade.isLong() ? HtrUtil.round5((lastDataBar.getbBarClose() - activeTrade.getOpenPrice()) * strategy.getTradingQuantity()) : HtrUtil.round5((activeTrade.getOpenPrice() - lastDataBar.getbBarClose()) * strategy.getTradingQuantity()));
        if (HtrEnums.SecType.FUT.equals(strategy.getTradeInstrument().getSecType())) {
            unrealizedPl *= HtrEnums.FutureMultiplier.getMultiplierBySymbol(strategy.getTradeInstrument().getSymbol());
        }
        if (HtrEnums.SecType.OPT.equals(strategy.getTradeInstrument().getSecType())) {
            unrealizedPl *= (HtrEnums.MiniOption.isMiniOption(strategy.getTradeInstrument().getSymbol()) ? 10 : 100);
        }
        activeTrade.setUnrealizedPl(unrealizedPl);
    }
}
