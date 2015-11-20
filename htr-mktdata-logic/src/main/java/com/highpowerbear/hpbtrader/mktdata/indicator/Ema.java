package com.highpowerbear.hpbtrader.mktdata.indicator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author robertk
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Ema {
    private Long timeInMillis;
    private Double ema;

    public Ema(Long timeInMillis, Double ema) {
        this.timeInMillis = timeInMillis;
        this.ema = ema;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public Double getEma() {
        return ema;
    }
}