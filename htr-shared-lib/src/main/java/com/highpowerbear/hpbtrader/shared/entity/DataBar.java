package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrUtil;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author rkolar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "databar", schema = "hpbtrader", catalog = "hpbtrader")
public class DataBar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar barCloseDate;
    @ManyToOne
    @XmlTransient
    private DataSeries dataSeries;
    private Double barOpen;
    private Double barHigh;
    private Double barLow;
    private Double barClose;
    private Integer volume;
    private Integer count;
    private Double wap;
    private Boolean hasGaps;

    @XmlElement
    public Integer getDataSeriesId() {
        return dataSeries.getId();
    }

    public long getBarCloseDateMillis() {
        return barCloseDate.getTimeInMillis();
    }

    public String print() {
        return dataSeries.getInstrument().getSymbol() + ": " + HtrUtil.getFormattedDate(barCloseDate) + ", " + barOpen + ", " + barHigh + ", " + barLow + ", " + barClose + ", " + volume + ", " + count + ", " + hasGaps;
    }

    public void mergeFrom(DataBar from) {
        this.barOpen = from.getbBarOpen();
        this.barHigh = from.getbBarHigh();
        this.barLow = from.getbBarLow();
        this.barClose = from.getbBarClose();
        this.volume = from.getVolume();
        this.count = from.getCount();
        this.wap = from.getWap();
        this.hasGaps = from.getHasGaps();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataBar dataBar = (DataBar) o;

        return barCloseDate != null ? barCloseDate.equals(dataBar.barCloseDate) : dataBar.barCloseDate == null;

    }

    @Override
    public int hashCode() {
        return barCloseDate != null ? barCloseDate.hashCode() : 0;
    }

    public Calendar getBarCloseDate() {
        return barCloseDate;
    }

    public void setbBarCloseDate(Calendar bCloseDate) {
        this.barCloseDate = bCloseDate;
    }

    public DataSeries getDataSeries() {
        return dataSeries;
    }

    public void setDataSeries(DataSeries dataSeries) {
        this.dataSeries = dataSeries;
    }

    public Double getbBarOpen() {
        return barOpen;
    }

    public void setbBarOpen(Double bOpen) {
        this.barOpen = bOpen;
    }

    public Double getbBarHigh() {
        return barHigh;
    }

    public void setbBarHigh(Double bHigh) {
        this.barHigh = bHigh;
    }

    public Double getbBarLow() {
        return barLow;
    }

    public void setbBarLow(Double bLow) {
        this.barLow = bLow;
    }

    public Double getbBarClose() {
        return barClose;
    }

    public void setbBarClose(Double bClose) {
        this.barClose = bClose;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getWap() {
        return wap;
    }

    public void setWap(Double wap) {
        this.wap = wap;
    }

    public Boolean getHasGaps() {
        return hasGaps;
    }

    public void setHasGaps(Boolean hasGaps) {
        this.hasGaps = hasGaps;
    }
}
