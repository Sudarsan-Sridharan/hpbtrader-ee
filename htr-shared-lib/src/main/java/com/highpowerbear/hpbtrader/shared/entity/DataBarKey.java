package com.highpowerbear.hpbtrader.shared.entity;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by robertk on 10/2/2016.
 */
@Embeddable
public class DataBarKey implements Serializable {
    @Temporal(value= TemporalType.TIMESTAMP)
    private Calendar barCloseDate;
    private Integer dataSeriesId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataBarKey that = (DataBarKey) o;

        if (barCloseDate != null ? !barCloseDate.equals(that.barCloseDate) : that.barCloseDate != null) return false;
        return dataSeriesId != null ? dataSeriesId.equals(that.dataSeriesId) : that.dataSeriesId == null;

    }

    @Override
    public int hashCode() {
        int result = barCloseDate != null ? barCloseDate.hashCode() : 0;
        result = 31 * result + (dataSeriesId != null ? dataSeriesId.hashCode() : 0);
        return result;
    }

    public Calendar getBarCloseDate() {
        return barCloseDate;
    }

    public void setBarCloseDate(Calendar barCloseDate) {
        this.barCloseDate = barCloseDate;
    }

    public Integer getDataSeriesId() {
        return dataSeriesId;
    }

    public void setDataSeriesId(Integer dataSeriesId) {
        this.dataSeriesId = dataSeriesId;
    }
}