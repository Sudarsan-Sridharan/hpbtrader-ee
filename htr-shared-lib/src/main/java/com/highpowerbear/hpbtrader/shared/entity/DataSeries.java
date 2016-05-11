package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 *
 * @author rkolar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Cacheable(false)
@Table(name = "dataseries", schema = "hpbtrader", catalog = "hpbtrader")
public class DataSeries implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;
    @ManyToOne
    private Instrument instrument;
    @Enumerated(EnumType.STRING)
    @Column(name = "sinterval")
    private HtrEnums.Interval interval;
    private Integer displayOrder;
    private boolean active;
    private String alias; // symbol_currency_exchange_interval

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSeries dataSeries = (DataSeries) o;

        return !(id != null ? !id.equals(dataSeries.id) : dataSeries.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public HtrEnums.Interval getInterval() {
        return interval;
    }

    public void setInterval(HtrEnums.Interval interval) {
        this.interval = interval;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
