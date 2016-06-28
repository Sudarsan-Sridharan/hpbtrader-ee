package com.highpowerbear.hpbtrader.strategy.linear.logic;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.*;
import com.highpowerbear.hpbtrader.shared.model.OperResult;
import com.highpowerbear.hpbtrader.shared.persistence.DataSeriesDao;
import com.highpowerbear.hpbtrader.shared.techanalysis.TiCalculator;
import com.highpowerbear.hpbtrader.strategy.common.SingletonRepo;
import com.highpowerbear.hpbtrader.strategy.linear.ProcessContext;
import com.highpowerbear.hpbtrader.strategy.linear.StrategyLogic;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
public abstract class AbstractStrategyLogic implements StrategyLogic {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);
    private final int INDICATORS_LIST_SIZE = 10;

    private DataSeriesDao dataSeriesDao = SingletonRepo.getInstance().getDataSeriesDao();
    private DataSeries inputDataSeries;
    private boolean offset;

    protected TiCalculator tiCalculator = SingletonRepo.getInstance().getTiCalculator();
    protected NumberFormat nf = NumberFormat.getInstance(Locale.US);

    protected ProcessContext ctx;
    protected List<DataBar> dataBars;
    protected DataBar lastDataBar;
    protected DataBar prevDataBar;

    protected Trade activeTrade;
    protected IbOrder ibOrder;

    public AbstractStrategyLogic(ProcessContext ctx) {
        this.inputDataSeries = dataSeriesDao.getDataSeriesByAlias(ctx.getStrategy().getDefaultInputSeriesAlias());
        nf.setMinimumFractionDigits(6);
        nf.setMaximumFractionDigits(6);
    }

    @Override
    public Strategy getStrategy() {
        return this.ctx.getStrategy();
    }

    @Override
    public ProcessContext getProcessContext() {
        return this.ctx;
    }

    @Override
    public OperResult<Boolean, String> prepare() {
        this.offset = false;
        this.dataBars = dataSeriesDao.getLastDataBars(inputDataSeries, HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE);
        return doPrepare();
    }

    @Override
    public OperResult<Boolean, String> prepare(Calendar lastDate) {
        this.offset = true;
        this.dataBars = dataSeriesDao.getDataBars(inputDataSeries, HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE, lastDate);
        return doPrepare();
    }

    private OperResult<Boolean, String> doPrepare() {
        this.ibOrder = null;
        this.activeTrade = ctx.getActiveTrade();
        this.lastDataBar = dataBars.get(dataBars.size() - 1);
        this.prevDataBar = dataBars.get(dataBars.size() - 2);

        if (dataBars.size() < HtrDefinitions.BARS_REQUIRED + INDICATORS_LIST_SIZE) {
            return new OperResult<>(false, "not enough  bars available");
        }
        long barTypeMillis = inputDataSeries.getBarType().getMillis();
        long nowMillis = HtrUtil.getCalendar().getTimeInMillis();
        boolean isCurrentBar = ((lastDataBar.getBarCloseDateMillis() + barTypeMillis) > nowMillis);
        if (!isCurrentBar) {
            return new OperResult<>(false, "not current bar");
        }
        if (activeTrade != null && activeTrade.isInit()) {
            return new OperResult<>(false, "active trade in state " + activeTrade.getTradeStatus());
        }
        reloadParameters();
        calculateIndicators();

        return new OperResult<>(true, "OK");
    }

    @Override
    public abstract void process();

    @Override
    public IbOrder getIbOrder() {
        return this.ibOrder;
    }

    @Override
    public Trade getActiveTrade() {
        return this.activeTrade;
    }

    @Override
    public DataBar getLastDataBar() {
        return this.lastDataBar;
    }

    protected abstract void calculateIndicators();
    protected abstract void reloadParameters();
    
    protected void createIbOrder() {
        this.ibOrder = new IbOrder();
        ibOrder.setStrategy(ctx.getStrategy());
        ibOrder.setStrategyMode(ctx.getStrategy().getStrategyMode());
        ibOrder.setSubmitType(HtrEnums.SubmitType.AUTO);
        ibOrder.setQuantity(ctx.getStrategy().getTradingQuantity());
        ibOrder.setOrderType(HtrEnums.OrderType.MKT);
        ibOrder.setLimitPrice(null); // N/A for market order
        ibOrder.setStopPrice(null); // N/A for market order
        ibOrder.addEvent(HtrEnums.IbOrderStatus.NEW, (offset ? lastDataBar.getBarCloseDate() : HtrUtil.getCalendar()));
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
        Double unrealizedPl;
        if (activeTrade.isLong()) {
            unrealizedPl = HtrUtil.round5((lastDataBar.getbBarClose() - activeTrade.getOpenPrice()) * ctx.getStrategy().getTradingQuantity());
        } else {
            unrealizedPl = HtrUtil.round5((activeTrade.getOpenPrice() - lastDataBar.getbBarClose()) * ctx.getStrategy().getTradingQuantity());
        }
        if (HtrEnums.SecType.FUT.equals(ctx.getStrategy().getTradeInstrument().getSecType())) {
            unrealizedPl *= HtrEnums.FutureMultiplier.getMultiplierBySymbol(ctx.getStrategy().getTradeInstrument().getSymbol());
        }
        if (HtrEnums.SecType.OPT.equals(ctx.getStrategy().getTradeInstrument().getSecType())) {
            unrealizedPl *= (HtrEnums.MiniOption.isMiniOption(ctx.getStrategy().getTradeInstrument().getSymbol()) ? 10 : 100);
        }
        activeTrade.setUnrealizedPl(unrealizedPl);
    }
}
