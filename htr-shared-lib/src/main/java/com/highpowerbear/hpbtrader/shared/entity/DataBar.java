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
@XmlAccessorType(XmlAccessType.NONE)
@Entity
public class DataBar implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="databar", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="databar")
    private Long id;
    @ManyToOne
    @XmlTransient
    private DataSeries dataSeries;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar barCloseDate;
    @XmlElement
    private Double barOpen;
    @XmlElement
    private Double barHigh;
    @XmlElement
    private Double barLow;
    @XmlElement
    private Double barClose;
    @XmlElement
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

        return !(id != null ? !id.equals(dataBar.id) : dataBar.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataSeries getDataSeries() {
        return dataSeries;
    }

    public void setDataSeries(DataSeries dataSeries) {
        this.dataSeries = dataSeries;
    }

    public Calendar getBarCloseDate() {
        return barCloseDate;
    }

    public void setbBarCloseDate(Calendar bCloseDate) {
        this.barCloseDate = bCloseDate;
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
