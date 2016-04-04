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
public class Macd implements TiIndicator {

    private Calendar barCloseDate;
    
    private Double macdL;
    private Double macdSl;
    private Double macdH;

    public Macd(Calendar barCloseDate, Double macdL, Double macdSl, Double macdH) {
        this.barCloseDate = barCloseDate;
        this.macdL = macdL;
        this.macdSl = macdSl;
        this.macdH = macdH;
    }

    @Override
    public Calendar getBarCloseDate() {
        return barCloseDate;
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
