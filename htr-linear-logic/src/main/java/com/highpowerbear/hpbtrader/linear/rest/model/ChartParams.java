package com.highpowerbear.hpbtrader.linear.rest.model;

/**
 * Created by robertk on 11/14/2015.
 */
import com.highpowerbear.hpbtrader.linear.entity.Series;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author robertk
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartParams {
    private Integer seriesId;
    private String symbol;
    private String currency;
    private String interval;
    private Integer numBars;
    private Integer ema1Period;
    private Integer ema2Period;

    public ChartParams(Series series) {
        this.seriesId = series.getId();
        this.symbol = series.getSymbol();
        this.currency = series.getCurrency().toString();
        this.interval = series.getInterval().getDisplayName();
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public String getInterval() {
        return interval;
    }

    public Integer getNumBars() {
        return numBars;
    }

    public Integer getEma1Period() {
        return ema1Period;
    }

    public Integer getEma2Period() {
        return ema2Period;
    }

    public void setNumBars(Integer numBars) {
        this.numBars = numBars;
    }

    public void setEma1Period(Integer ema1Period) {
        this.ema1Period = ema1Period;
    }

    public void setEma2Period(Integer ema2Period) {
        this.ema2Period = ema2Period;
    }
}
