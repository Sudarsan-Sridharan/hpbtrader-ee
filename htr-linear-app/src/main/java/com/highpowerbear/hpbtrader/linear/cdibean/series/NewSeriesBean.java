package com.highpowerbear.hpbtrader.linear.cdibean.series;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by rkolar on 5/30/14.
 */
@Named
@SessionScoped
public class NewSeriesBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger l = Logger.getLogger(LinSettings.LOGGER);
    @Inject private DatabaseDao databaseDao;
    @Inject private StrategyController strategyController;
    @Inject private SeriesBean seriesBean;

    private String underlying;
    private String symbol;
    private List<LinEnums.Currency> currencies;
    private LinEnums.Currency currency;
    private List<LinEnums.Exchange> exchanges;
    private LinEnums.Exchange exchange;
    private List<LinEnums.Interval> intervals;
    private LinEnums.Interval interval;
    private List<LinEnums.SecType> secTypes;
    private LinEnums.SecType secType;
    private List<LinEnums.StrategyType> strategyTypes;
    private LinEnums.StrategyType strategyType;

    public void init() {
        currencies = Arrays.asList(LinEnums.Currency.values());
        exchanges = Arrays.asList(LinEnums.Exchange.values());
        intervals = Arrays.asList(LinEnums.Interval.values());
        secTypes = Arrays.asList(LinEnums.SecType.values());
        strategyTypes = Arrays.asList(LinEnums.StrategyType.values());
    }

    public void addNewSeries() {
        Series series = new Series();
        series.setUnderlying(underlying);
        series.setSymbol(symbol);
        series.setCurrency(currency);
        series.setExchange(exchange);
        series.setSecType(secType);
        series.setInterval(interval);
        Integer highestDisplayOrder = databaseDao.getHighestDisplayOrder();
        series.setDisplayOrder(highestDisplayOrder != null ? highestDisplayOrder + 1 : 1);
        boolean success = databaseDao.addSeries(series);
        if (!success) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Could not add series."));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }
        Strategy strategy = new Strategy();
        strategy.setSeries(series);
        strategy.setStrategyType(strategyType);
        strategy.setIsActive(true);
        strategy.setStrategyMode(LinEnums.StrategyMode.SIM);
        strategy.setParams(strategyType.getDefaultParams());
        strategy.setTradingQuantity(series.getSecType().getDefaultTradingQuantity());
        databaseDao.addStrategy(strategy);
        strategyController.swapStrategyLogic(series);
        seriesBean.setTabStrategyId(strategy.getId());
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<LinEnums.Currency> getCurrencies() {
        return currencies;
    }

    public LinEnums.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(LinEnums.Currency currency) {
        this.currency = currency;
    }

    public List<LinEnums.Exchange> getExchanges() {
        return exchanges;
    }

    public LinEnums.Exchange getExchange() {
        return exchange;
    }

    public void setExchange(LinEnums.Exchange exchange) {
        this.exchange = exchange;
    }

    public List<LinEnums.Interval> getIntervals() {
        return intervals;
    }

    public LinEnums.Interval getInterval() {
        return interval;
    }

    public void setInterval(LinEnums.Interval interval) {
        this.interval = interval;
    }

    public List<LinEnums.SecType> getSecTypes() {
        return secTypes;
    }

    public LinEnums.SecType getSecType() {
        return secType;
    }

    public void setSecType(LinEnums.SecType secType) {
        this.secType = secType;
    }

    public List<LinEnums.StrategyType> getStrategyTypes() {
        return strategyTypes;
    }

    public LinEnums.StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(LinEnums.StrategyType strategyType) {
        this.strategyType = strategyType;
    }
}