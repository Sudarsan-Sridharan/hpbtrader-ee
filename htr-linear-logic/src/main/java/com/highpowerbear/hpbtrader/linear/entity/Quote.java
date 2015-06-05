package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.common.LinUtil;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author rkolar
 */
@Entity
@Table(name = "lin_quote", uniqueConstraints = @UniqueConstraint(columnNames = {"qDateBarClose", "series_id"}))
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"timeInMillisBarClose", "qOpen", "high", "low", "qClose", "volume"})
public class Quote implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="lin_quote", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_quote")
    private Long id;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar qDateBarClose;
    @XmlElement
    private Double qOpen;
    @XmlElement
    private Double high;
    @XmlElement
    private Double low;
    @XmlElement
    private Double qClose;
    @XmlElement
    private Integer volume;
    private Integer count;
    private Double wap;
    private Boolean hasGaps;
    @ManyToOne
    private Series series;
    
    @XmlElement
    public long getTimeInMillisBarClose() {
        return qDateBarClose.getTimeInMillis();
    }
    
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
    public Boolean getHasGaps() {
        return hasGaps;
    }

    public void setHasGaps(Boolean hasGaps) {
        this.hasGaps = hasGaps;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getqClose() {
        return qClose;
    }

    public void setqClose(Double qClose) {
        this.qClose = qClose;
    }
    
    public Calendar getqDateBarClose() {
        return qDateBarClose;
    }

    public void setqDateBarClose(Calendar qDateBarClose) {
        this.qDateBarClose = qDateBarClose;
    }
    
    public Double getqOpen() {
        return qOpen;
    }

    public void setqOpen(Double qOpen) {
        this.qOpen = qOpen;
    }
    
    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }
    
    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
    
    public Double getWap() {
        return wap;
    }

    public void setWap(Double wap) {
        this.wap = wap;
    }
    
    public boolean valuesEqual(Quote otherQuote) {
        if (otherQuote == null) {
            return false;
        }
        if (    this.qOpen.equals(otherQuote.getqOpen()) && 
                this.high.equals(otherQuote.getHigh()) && 
                this.low.equals(otherQuote.getLow()) &&
                this.qClose.equals(otherQuote.getqClose()) &&
                this.volume.equals(otherQuote.getVolume()) &&
                this.count.equals(otherQuote.getCount()) &&
                this.wap.equals(otherQuote.getWap()) &&
                this.hasGaps.equals(otherQuote.getHasGaps())
           )
        {
            return true;
        }
        return false;
    }
    
    public void copyValuesFrom(Quote otherQuote) {
        if (otherQuote == null) {
            return;
        }
        this.qOpen = otherQuote.getqOpen();
        this.high = otherQuote.getHigh();
        this.low = otherQuote.getLow();
        this.qClose = otherQuote.getqClose();
        this.volume = otherQuote.getVolume();
        this.count = otherQuote.getCount();
        this.wap = otherQuote.getWap();
        this.hasGaps = otherQuote.getHasGaps();
    }
    
    public String printValues() {
        return series.getSymbol() + ": " + LinUtil.getFormattedDate(qDateBarClose) + ", " + qOpen + ", " + high + ", " + low + ", " + qClose + ", " + volume + ", " + count + ", " + hasGaps;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Quote)) {
            return false;
        }
        Quote other = (Quote) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
