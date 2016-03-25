package com.highpowerbear.hpbtrader.shared.techanalysis;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Ema;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Macd;
import com.highpowerbear.hpbtrader.shared.techanalysis.indicator.Stochastics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
public class TiCalculator {
    public List<Ema> calculateEma(List<DataBar> dataBars, Integer emaPeriod) {
        if (dataBars.size() < HtrDefinitions.BARS_REQUIRED || emaPeriod > HtrDefinitions.MAX_EMA_PERIOD) {
            return null;
        }
        return doCalculateEma(dataBars, emaPeriod);
    }
    
    public List<Stochastics> calculateStoch(List<DataBar> dataBars) {
        if (dataBars.size() < HtrDefinitions.BARS_REQUIRED) {
            return null;
        }
        return doCalculateStoch(dataBars, 14, 3, 3);
    }
    
    public List<Macd> calculateMacd(List<DataBar> dataBars) {
        if (dataBars.size() < HtrDefinitions.BARS_REQUIRED) {
            return null;
        }
        return doCalculateMacd(dataBars, 12, 26, 9);
    }
    
    private List<Ema> doCalculateEma(List<DataBar> dataBars, int period) {
        List<Ema> emaList = new ArrayList<>();
        double sma = 0d;
        for (int i = 0; i < period; i++) {
            sma += dataBars.get(i).getbClose();
        }
        sma = sma/(double) period;
        double ema = sma;
        double mult = 2d/((double) period +  1d);
        for (int i = period; i < dataBars.size(); i++) {
            ema = (dataBars.get(i).getbClose() - ema) * mult + ema;
            if (i >= HtrDefinitions.BARS_REQUIRED) {
                Long timeInMillis = dataBars.get(i).getbCloseDateMillis();
                emaList.add(new Ema(timeInMillis, ema));
            }
        }
        return emaList;
    }
    
    private List<Stochastics> doCalculateStoch(List<DataBar> dataBars, int lookbackPeriod, int smaKPeriod, int smaDPeriod) {
        List<Stochastics> stochList = new ArrayList<>();
        double[] kFast = new double[smaKPeriod];
        double[] k = new double[smaDPeriod];
        double d;
        int currentBarIndex = lookbackPeriod - 1;
        while (currentBarIndex < dataBars.size()) {
            double highestHigh = dataBars.get(currentBarIndex).getbHigh();
            double lowestLow = dataBars.get(currentBarIndex).getbLow();
            for (int i = currentBarIndex; i >= currentBarIndex - lookbackPeriod + 1; i--) {
                if (dataBars.get(i).getbHigh() > highestHigh) {
                    highestHigh = dataBars.get(i).getbHigh();
                }
                if (dataBars.get(i).getbLow() < lowestLow) {
                    lowestLow = dataBars.get(i).getbLow();
                }
            }
            System.arraycopy(kFast, 1, kFast, 0, smaKPeriod - 1);
            double currentBar = dataBars.get(currentBarIndex).getbClose();
            kFast[smaKPeriod - 1] =  (currentBar - lowestLow)/(highestHigh - lowestLow) * 100d;
            System.arraycopy(k, 1, k, 0, smaDPeriod - 1);
            k[smaDPeriod - 1] = 0d;
            for (int i = 0; i < smaKPeriod; i++) {
                k[smaDPeriod - 1] += kFast[i];
            }
            k[smaDPeriod - 1] = k[smaDPeriod - 1]/(double) smaKPeriod;
            d = 0d;
            for (int i = 0; i < smaDPeriod; i++) {
                d += k[i];
            }
            d = d/(double) smaDPeriod;
            if (currentBarIndex >= HtrDefinitions.BARS_REQUIRED) {
                Long timeInMillis = dataBars.get(currentBarIndex).getbCloseDateMillis();
                stochList.add(new Stochastics(timeInMillis, k[smaDPeriod - 1], d));
            }
            currentBarIndex++;
        }
        return stochList;
    }
    
    private List<Macd> doCalculateMacd(List<DataBar> dataBars, int p1, int p2, int p3) {
        List<Macd> macdList = new ArrayList<>();
        double macdEma1 = 0d, macdEma2 = 0d;
        double macdL, macdSl = 0d, macdH;
        double m1 = 2d/((double) p1 +  1d);
        double m2 = 2d/((double) p2 +  1d);
        double m3 = 2d/((double) p3 +  1d);
        for (int i = 0; i < p1; i++) {
            macdEma1 += dataBars.get(i).getbClose();
        }
        macdEma1 = macdEma1/(double) p1;
        for (int i = p1; i < p2; i++) {
            macdEma1 = (dataBars.get(i).getbClose() - macdEma1) * m1 + macdEma1;
        }
        for (int i = 0; i < p2; i++) {
            macdEma2 += dataBars.get(i).getbClose();
        }
        macdEma2 = macdEma2/(double) p2;
        for (int i = p2; i < p2 + p3; i++) {
            macdEma1 = (dataBars.get(i).getbClose() - macdEma1) * m1 + macdEma1;
            macdEma2 = (dataBars.get(i).getbClose() - macdEma2) * m2 + macdEma2;
            macdL = macdEma1 - macdEma2;
            macdSl += macdL;
        }
        macdSl = macdSl/(double) p3;
        for (int i = p3; i < dataBars.size(); i ++) {
            macdEma1 = (dataBars.get(i).getbClose() - macdEma1) * m1 + macdEma1;
            macdEma2 = (dataBars.get(i).getbClose() - macdEma2) * m2 + macdEma2;
            macdL = macdEma1 - macdEma2;
            macdSl = (macdL - macdSl) * m3 + macdSl;
            macdH = macdL - macdSl;
            if (i >= HtrDefinitions.BARS_REQUIRED) {
                Long timeInMillis = dataBars.get(i).getbCloseDateMillis();
                macdList.add(new Macd(timeInMillis, macdL, macdSl,macdH));
            }
        }
        return macdList;
    }
}