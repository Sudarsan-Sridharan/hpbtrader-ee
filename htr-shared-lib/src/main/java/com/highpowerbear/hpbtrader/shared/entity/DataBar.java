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

    @EmbeddedId
    @XmlTransient
    private DataBarKey dataBarKey = new DataBarKey();
    @ManyToOne
    @MapsId("dataSeriesId")
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

    @XmlElement
    public Calendar getBarCloseDate() {
        return dataBarKey.getBarCloseDate();
    }

    public long getBarCloseDateMillis() {
        return dataBarKey.getBarCloseDate().getTimeInMillis();
    }

    public String print() {
        return dataSeries.getInstrument().getSymbol() + ": " + HtrUtil.getFormattedDate(dataBarKey.getBarCloseDate()) + ", " + barOpen + ", " + barHigh + ", " + barLow + ", " + barClose + ", " + volume + ", " + count + ", " + hasGaps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataBar dataBar = (DataBar) o;

        if (dataBarKey != null ? !dataBarKey.equals(dataBar.dataBarKey) : dataBar.dataBarKey != null) return false;
        return dataSeries != null ? dataSeries.equals(dataBar.dataSeries) : dataBar.dataSeries == null;

    }

    @Override
    public int hashCode() {
        int result = dataBarKey != null ? dataBarKey.hashCode() : 0;
        result = 31 * result + (dataSeries != null ? dataSeries.hashCode() : 0);
        return result;
    }

    public DataBarKey getDataBarKey() {
        return dataBarKey;
    }

    public void setDataBarKey(DataBarKey dataBarKey) {
        this.dataBarKey = dataBarKey;
    }

    public void setBarCloseDate(Calendar barCloseDate) {
        this.dataBarKey.setBarCloseDate(barCloseDate);
    }

    public DataSeries getDataSeries() {
        return dataSeries;
    }

    public void setDataSeries(DataSeries dataSeries) {
        this.dataSeries = dataSeries;
        this.dataBarKey.setDataSeriesId(dataSeries.getId());
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
