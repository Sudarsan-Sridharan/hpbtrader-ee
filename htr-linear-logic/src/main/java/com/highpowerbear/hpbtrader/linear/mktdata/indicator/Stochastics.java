package com.highpowerbear.hpbtrader.linear.mktdata.indicator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author robertk
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Stochastics {
    private Long timeInMillis;
    
    private Double stochK;
    private Double stochD;

    public Stochastics(Long timeInMillis, Double stochK, Double stochD) {
        this.timeInMillis = timeInMillis;
        this.stochK = stochK;
        this.stochD = stochD;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public Double getStochK() {
        return stochK;
    }

    public Double getStochD() {
        return stochD;
    }
}
