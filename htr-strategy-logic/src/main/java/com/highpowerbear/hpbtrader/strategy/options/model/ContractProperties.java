package com.highpowerbear.hpbtrader.strategy.options.model;

import com.highpowerbear.hpbtrader.strategy.common.StrategyDefinitions;
import com.highpowerbear.hpbtrader.strategy.common.StrategyUtil;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author robertk
 */
public class ContractProperties implements Comparable<ContractProperties> {
    private static final Logger l = Logger.getLogger(StrategyDefinitions.LOGGER);
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    private final Double DEF_maxSpread = 0.01;
    private final Double DEF_maxValidSpread = 0.15;
    private final Integer DEF_minVolume = 0;
    private final Integer DEF_minOpenInterest = 0;
    private final Double DEF_callStrikeDiff = 0.5;
    private final Double DEF_putStrikeDiff = 0.01;
    private final Boolean DEF_autoLimit = Boolean.TRUE;
    private final Double DEF_bidPriceOffsetBuy = 0.01;
    private final Double DEF_bidPriceOffsetSell = 0.00;
    private final Integer DEF_tradingQuantCall = 10;
    private final Integer DEF_tradingQuantPut = 5;
    private final String DEF_tradingStartTime = "09:30";
    private final String DEF_tradingStopTime = "15:00";
    
    private String underlying;
    
    // trading constraints
    private Double maxSpread = DEF_maxSpread;
    private Double maxValidSpread = DEF_maxValidSpread;
    private Integer minVolume = DEF_minVolume;
    private Integer minOpenInterest = DEF_minOpenInterest;
    // contract-order properties
    private Double callStrikeDiff = DEF_callStrikeDiff;
    private Double putStrikeDiff = DEF_putStrikeDiff;
    private Boolean autoLimit = DEF_autoLimit;
    private Double bidPriceOffsetBuy = DEF_bidPriceOffsetBuy;
    private Double bidPriceOffsetSell = DEF_bidPriceOffsetSell;
    private Integer tradingQuantCall = DEF_tradingQuantCall;
    private Integer tradingQuantPut = DEF_tradingQuantPut;
    private String tradingStartTime = DEF_tradingStartTime;
    private String tradingStopTime = DEF_tradingStopTime;
    
    public ContractProperties(String underlying) {
        this.underlying = underlying;
    }

    @Override
    public int compareTo(ContractProperties other) {
        return (this.underlying.compareTo(other.underlying));
    }
    
    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }
    
    public Double getMaxSpread() {
        return maxSpread;
    }

    public void setMaxSpread(Double maxSpread) {
        this.maxSpread = (maxSpread != null && maxSpread >= StrategyDefinitions.MIN_maxSpread ? StrategyUtil.round5(maxSpread) : this.maxSpread);
    }

    public Double getMaxValidSpread() {
        return maxValidSpread;
    }

    public void setMaxValidSpread(Double maxValidSpread) {
        this.maxValidSpread = (maxValidSpread != null && maxValidSpread >= StrategyDefinitions.MIN_maxValidSpread ? StrategyUtil.round5(maxValidSpread) : this.maxValidSpread);
    }

    public Integer getMinVolume() {
        return minVolume;
    }

    public void setMinVolume(Integer minVolume) {
        this.minVolume = (minVolume != null && minVolume >= StrategyDefinitions.MIN_minVolume ? minVolume : this.minVolume);
    }

    public Integer getMinOpenInterest() {
        return minOpenInterest;
    }

    public void setMinOpenInterest(Integer minOpenInterest) {
        this.minOpenInterest = (minOpenInterest != null && minOpenInterest >= StrategyDefinitions.MIN_minOpenInterest ? minOpenInterest : this.minOpenInterest);
    }

    public Double getCallStrikeDiff() {
        return callStrikeDiff;
    }

    public void setCallStrikeDiff(Double callStrikeDiff) {
        this.callStrikeDiff = (callStrikeDiff != null && callStrikeDiff >= StrategyDefinitions.MIN_callStrikeDiff ? StrategyUtil.round5(callStrikeDiff) : this.callStrikeDiff);
    }

    public Double getPutStrikeDiff() {
        return putStrikeDiff;
    }

    public void setPutStrikeDiff(Double putStrikeDiff) {
        this.putStrikeDiff = (putStrikeDiff != null && putStrikeDiff >= StrategyDefinitions.MIN_putStrikeDiff ? StrategyUtil.round5(putStrikeDiff) : this.putStrikeDiff);
    }

    public Boolean getAutoLimit() {
        return autoLimit;
    }

    public void setAutoLimit(Boolean autoLimit) {
        this.autoLimit = autoLimit;
    }

    public Double getBidPriceOffsetBuy() {
        return bidPriceOffsetBuy;
    }

    public void setBidPriceOffsetBuy(Double bidPriceOffsetBuy) {
        this.bidPriceOffsetBuy = (bidPriceOffsetBuy != null && StrategyUtil.abs(bidPriceOffsetBuy) <= StrategyDefinitions.MAX_ABS_bidPriceOffset ? StrategyUtil.round5(bidPriceOffsetBuy) : this.bidPriceOffsetBuy);
    }

    public Double getBidPriceOffsetSell() {
        return bidPriceOffsetSell;
    }

    public void setBidPriceOffsetSell(Double bidPriceOffsetSell) {
        this.bidPriceOffsetSell = (bidPriceOffsetSell != null && StrategyUtil.abs(bidPriceOffsetSell) <= StrategyDefinitions.MAX_ABS_bidPriceOffset ? StrategyUtil.round5(bidPriceOffsetSell) : this.bidPriceOffsetSell);
    }

    public Integer getTradingQuantCall() {
        return tradingQuantCall;
    }

    public void setTradingQuantCall(Integer tradingQuantCall) {
        this.tradingQuantCall = (tradingQuantCall != null && tradingQuantCall > 0 && tradingQuantCall <= StrategyDefinitions.MAX_tradingQuantCall ? tradingQuantCall : this.tradingQuantCall);
    }

    public Integer getTradingQuantPut() {
        return tradingQuantPut;
    }

    public void setTradingQuantPut(Integer tradingQuantPut) {
        this.tradingQuantPut = (tradingQuantPut != null && tradingQuantPut > 0 && tradingQuantPut <= StrategyDefinitions.MAX_tradingQuantPut ? tradingQuantPut : this.tradingQuantPut);
    }

    public String getTradingStartTime() {
        return tradingStartTime;
    }

    public void setTradingStartTime(String tradingStartTime) {
        try {
            Date date = timeFormat.parse(tradingStartTime);
            this.tradingStartTime = timeFormat.format(date);
            if (!checkStartBeforeStopTime()) {
                this.tradingStartTime = this.tradingStopTime;
            }
        } catch (ParseException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }

    public String getTradingStopTime() {
        return tradingStopTime;
    }

    public void setTradingStopTime(String tradingStopTime) {
        try {
            Date date = timeFormat.parse(tradingStopTime);
            this.tradingStopTime = timeFormat.format(date);
            if (!checkStartBeforeStopTime()) {
                this.tradingStartTime = this.tradingStopTime;
            }
        } catch (ParseException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
    
    private boolean checkStartBeforeStopTime() {
        Calendar startTimeToday = StrategyUtil.getTodayMidnightCalendar();
        startTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStartTime.split(":")[0]));
        startTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStartTime.split(":")[1]));
        Calendar stopTimeToday = StrategyUtil.getTodayMidnightCalendar();
        stopTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStopTime.split(":")[0]));
        stopTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStopTime.split(":")[1]));
        return (startTimeToday.getTimeInMillis() <= stopTimeToday.getTimeInMillis());
    }
    
    public boolean isTradingTime() {
        Calendar startTimeToday = StrategyUtil.getTodayMidnightCalendar();
        startTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStartTime.split(":")[0]));
        startTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStartTime.split(":")[1]));
        Calendar stopTimeToday = StrategyUtil.getTodayMidnightCalendar();
        stopTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStopTime.split(":")[0]));
        stopTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStopTime.split(":")[1]));
        Calendar now = HtrUtil.getNowCalendar();
        return (now.getTimeInMillis() >= startTimeToday.getTimeInMillis() && now.getTimeInMillis() <= stopTimeToday.getTimeInMillis());
    }
    
    public String toCsv() {
        String DEL = ",";
        return
            checkNull(maxSpread) + DEL +
            checkNull(maxValidSpread) + DEL +
            checkNull(minVolume) + DEL +
            checkNull(minOpenInterest) + DEL +
            checkNull(callStrikeDiff) + DEL +
            checkNull(putStrikeDiff) + DEL +  
            checkNull(autoLimit) + DEL + 
            checkNull(bidPriceOffsetBuy) + DEL +
            checkNull(bidPriceOffsetSell) + DEL +
            checkNull(tradingQuantCall) + DEL +
            checkNull(tradingQuantPut) + DEL +
            checkNull(tradingStartTime) + DEL +
            checkNull(tradingStopTime);
    }
    
    private String checkNull(Double d) {
        return (d != null ? String.valueOf(d) : "");
    }
    
    private String checkNull(Integer i) {
        return (i != null ? String.valueOf(i) : "");
    }
    
    private String checkNull(String s) {
        return (s != null ? s : "");
    }
    
    private String checkNull(Boolean b) {
        return (b != null ? String.valueOf(b) : "false");
    }
}
