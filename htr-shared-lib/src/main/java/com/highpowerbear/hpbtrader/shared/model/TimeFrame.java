package com.highpowerbear.hpbtrader.shared.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Calendar;

/**
 * Created by robertk on 31.5.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeFrame {
    private Calendar fromDate;
    private Calendar toDate;

    public TimeFrame(Calendar fromDate, Calendar toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public boolean isValid() {
        return (fromDate != null && toDate != null && fromDate.before(toDate));
    }

    public Calendar getFromDate() {
        return fromDate;
    }

    public void setFromDate(Calendar fromDate) {
        this.fromDate = fromDate;
    }

    public Calendar getToDate() {
        return toDate;
    }

    public void setToDate(Calendar toDate) {
        this.toDate = toDate;
    }
}
