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
public class Macd {
    private Long timeInMillis;
    
    private Double macdL;
    private Double macdSl;
    private Double macdH;

    public Macd(Long timeInMillis, Double macdL, Double macdSl, Double macdH) {
        this.timeInMillis = timeInMillis;
        this.macdL = macdL;
        this.macdSl = macdSl;
        this.macdH = macdH;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public Double getMacdL() {
        return macdL;
    }

    public Double getMacdSl() {
        return macdSl;
    }

    public Double getMacdH() {
        return macdH;
    }
}
