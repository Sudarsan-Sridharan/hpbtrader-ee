package com.highpowerbear.hpbtrader.options.model;

import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
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
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);
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
        this.maxSpread = (maxSpread != null && maxSpread >= OptDefinitions.MIN_maxSpread ? OptUtil.round5(maxSpread) : this.maxSpread);
    }

    public Double getMaxValidSpread() {
        return maxValidSpread;
    }

    public void setMaxValidSpread(Double maxValidSpread) {
        this.maxValidSpread = (maxValidSpread != null && maxValidSpread >= OptDefinitions.MIN_maxValidSpread ? OptUtil.round5(maxValidSpread) : this.maxValidSpread);
    }

    public Integer getMinVolume() {
        return minVolume;
    }

    public void setMinVolume(Integer minVolume) {
        this.minVolume = (minVolume != null && minVolume >= OptDefinitions.MIN_minVolume ? minVolume : this.minVolume);
    }

    public Integer getMinOpenInterest() {
        return minOpenInterest;
    }

    public void setMinOpenInterest(Integer minOpenInterest) {
        this.minOpenInterest = (minOpenInterest != null && minOpenInterest >= OptDefinitions.MIN_minOpenInterest ? minOpenInterest : this.minOpenInterest);
    }

    public Double getCallStrikeDiff() {
        return callStrikeDiff;
    }

    public void setCallStrikeDiff(Double callStrikeDiff) {
        this.callStrikeDiff = (callStrikeDiff != null && callStrikeDiff >= OptDefinitions.MIN_callStrikeDiff ? OptUtil.round5(callStrikeDiff) : this.callStrikeDiff);
    }

    public Double getPutStrikeDiff() {
        return putStrikeDiff;
    }

    public void setPutStrikeDiff(Double putStrikeDiff) {
        this.putStrikeDiff = (putStrikeDiff != null && putStrikeDiff >= OptDefinitions.MIN_putStrikeDiff ? OptUtil.round5(putStrikeDiff) : this.putStrikeDiff);
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
        this.bidPriceOffsetBuy = (bidPriceOffsetBuy != null && OptUtil.abs(bidPriceOffsetBuy) <= OptDefinitions.MAX_ABS_bidPriceOffset ? OptUtil.round5(bidPriceOffsetBuy) : this.bidPriceOffsetBuy);
    }

    public Double getBidPriceOffsetSell() {
        return bidPriceOffsetSell;
    }

    public void setBidPriceOffsetSell(Double bidPriceOffsetSell) {
        this.bidPriceOffsetSell = (bidPriceOffsetSell != null && OptUtil.abs(bidPriceOffsetSell) <= OptDefinitions.MAX_ABS_bidPriceOffset ? OptUtil.round5(bidPriceOffsetSell) : this.bidPriceOffsetSell);
    }

    public Integer getTradingQuantCall() {
        return tradingQuantCall;
    }

    public void setTradingQuantCall(Integer tradingQuantCall) {
        this.tradingQuantCall = (tradingQuantCall != null && tradingQuantCall > 0 && tradingQuantCall <= OptDefinitions.MAX_tradingQuantCall ? tradingQuantCall : this.tradingQuantCall);
    }

    public Integer getTradingQuantPut() {
        return tradingQuantPut;
    }

    public void setTradingQuantPut(Integer tradingQuantPut) {
        this.tradingQuantPut = (tradingQuantPut != null && tradingQuantPut > 0 && tradingQuantPut <= OptDefinitions.MAX_tradingQuantPut ? tradingQuantPut : this.tradingQuantPut);
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
        Calendar startTimeToday = OptUtil.getTodayMidnightCalendar();
        startTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStartTime.split(":")[0]));
        startTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStartTime.split(":")[1]));
        Calendar stopTimeToday = OptUtil.getTodayMidnightCalendar();
        stopTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStopTime.split(":")[0]));
        stopTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStopTime.split(":")[1]));
        return (startTimeToday.getTimeInMillis() <= stopTimeToday.getTimeInMillis());
    }
    
    public boolean isTradingTime() {
        Calendar startTimeToday = OptUtil.getTodayMidnightCalendar();
        startTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStartTime.split(":")[0]));
        startTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStartTime.split(":")[1]));
        Calendar stopTimeToday = OptUtil.getTodayMidnightCalendar();
        stopTimeToday.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tradingStopTime.split(":")[0]));
        stopTimeToday.set(Calendar.MINUTE, Integer.valueOf(tradingStopTime.split(":")[1]));
        Calendar now = OptUtil.getNowCalendar();
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
