package com.highpowerbear.hpbtrader.shared.techanalysis.indicator;

import com.highpowerbear.hpbtrader.shared.techanalysis.TiIndicator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Stochastics implements TiIndicator {

    private Calendar barCloseDate;
    
    private Double stochK;
    private Double stochD;

    public Stochastics(Calendar barCloseDate, Double stochK, Double stochD) {
        this.barCloseDate = barCloseDate;
        this.stochK = stochK;
        this.stochD = stochD;
    }

    @Override
    public Calendar getBarCloseDate() {
        return barCloseDate;
    }

    public Double getStochK() {
        return stochK;
    }

    public Double getStochD() {
        return stochD;
    }
}
