package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrUtil;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author rkolar
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "databar", uniqueConstraints = @UniqueConstraint(columnNames = {"qDateBarClose", "series_id"}))
public class DataBar implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="databar", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="databar")
    private Long id;
    @ManyToOne
    private DataSeries dataSeries;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar qDateBarClose;
    @XmlElement
    private Double bOpen;
    @XmlElement
    private Double high;
    @XmlElement
    private Double low;
    @XmlElement
    private Double bClose;
    @XmlElement
    private Integer volume;
    private Integer count;
    private Double wap;
    private Boolean hasGaps;

    @XmlElement
    public long getTimeInMillisBarClose() {
        return qDateBarClose.getTimeInMillis();
    }

    public String print() {
        return dataSeries.getInstrument().getSymbol() + ": " + HtrUtil.getFormattedDate(qDateBarClose) + ", " + bOpen + ", " + high + ", " + low + ", " + bClose + ", " + volume + ", " + count + ", " + hasGaps;
    }

    public void mergeFrom(DataBar from) {
        this.bOpen = from.getbOpen();
        this.high = from.getHigh();
        this.low = from.getLow();
        this.bClose = from.getbClose();
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

    public Calendar getqDateBarClose() {
        return qDateBarClose;
    }

    public void setqDateBarClose(Calendar qDateBarClose) {
        this.qDateBarClose = qDateBarClose;
    }

    public Double getbOpen() {
        return bOpen;
    }

    public void setbOpen(Double bOpen) {
        this.bOpen = bOpen;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getbClose() {
        return bClose;
    }

    public void setbClose(Double bClose) {
        this.bClose = bClose;
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
