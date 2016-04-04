package com.highpowerbear.hpbtrader.shared.techanalysis.indicator;

import com.highpowerbear.hpbtrader.shared.techanalysis.TiIndicator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Ema implements TiIndicator {

    private Calendar barCloseDate;
    private Double ema;

    public Ema(Calendar barCloseDate, Double ema) {
        this.barCloseDate = barCloseDate;
        this.ema = ema;
    }

    @Override
    public Calendar getBarCloseDate() {
        return barCloseDate;
    }

    public Double getEma() {
        return ema;
    }
}