package com.highpowerbear.hpbtrader.mktdata.process;

import com.highpowerbear.hpbtrader.mktdata.indicator.Ema;
import com.highpowerbear.hpbtrader.mktdata.indicator.Macd;
import com.highpowerbear.hpbtrader.mktdata.indicator.Stochastics;
import com.highpowerbear.hpbtrader.shared.common.HtrSettings;
import com.highpowerbear.hpbtrader.shared.entity.Bar;

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
    public List<Ema> calculateEma(List<Bar> bars, Integer emaPeriod) {
        if (bars.size() < HtrSettings.BARS_REQUIRED || emaPeriod > HtrSettings.MAX_EMA_PERIOD) {
            return null;
        }
        return doCalculateEma(bars, emaPeriod);
    }
    
    public List<Stochastics> calculateStoch(List<Bar> bars) {
        if (bars.size() < HtrSettings.BARS_REQUIRED) {
            return null;
        }
        return doCalculateStoch(bars, 14, 3, 3);
    }
    
    public List<Macd> calculateMacd(List<Bar> bars) {
        if (bars.size() < HtrSettings.BARS_REQUIRED) {
            return null;
        }
        return doCalculateMacd(bars, 12, 26, 9);
    }
    
    private List<Ema> doCalculateEma(List<Bar> bars, int period) {
        List<Ema> emaList = new ArrayList<>();
        double sma = 0d;
        for (int i = 0; i < period; i++) {
            sma += bars.get(i).getqClose();
        }
        sma = sma/(double) period;
        double ema = sma;
        double mult = 2d/((double) period +  1d);
        for (int i = period; i < bars.size(); i++) {
            ema = (bars.get(i).getqClose() - ema) * mult + ema;
            if (i >= HtrSettings.BARS_REQUIRED) {
                Long timeInMillis = bars.get(i).getTimeInMillisBarClose();
                emaList.add(new Ema(timeInMillis, ema));
            }
        }
        return emaList;
    }
    
    private List<Stochastics> doCalculateStoch(List<Bar> bars, int lookbackPeriod, int smaKPeriod, int smaDPeriod) {
        List<Stochastics> stochList = new ArrayList<>();
        double[] kFast = new double[smaKPeriod];
        double[] k = new double[smaDPeriod];
        double d;
        int currentBarIndex = lookbackPeriod - 1;
        while (currentBarIndex < bars.size()) {
            double highestHigh = bars.get(currentBarIndex).getHigh();
            double lowestLow = bars.get(currentBarIndex).getLow();
            for (int i = currentBarIndex; i >= currentBarIndex - lookbackPeriod + 1; i--) {
                if (bars.get(i).getHigh() > highestHigh) {
                    highestHigh = bars.get(i).getHigh();
                }
                if (bars.get(i).getLow() < lowestLow) {
                    lowestLow = bars.get(i).getLow();
                }
            }
            for (int i = 0; i < smaKPeriod - 1; i++) {
                kFast[i] = kFast[i + 1];
            }
            double currentBar = bars.get(currentBarIndex).getqClose();
            kFast[smaKPeriod - 1] =  (currentBar - lowestLow)/(highestHigh - lowestLow) * 100d;
            for (int i = 0; i < smaDPeriod - 1; i++) {
                k[i] = k[i + 1];
            }
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
            if (currentBarIndex >= HtrSettings.BARS_REQUIRED) {
                Long timeInMillis = bars.get(currentBarIndex).getTimeInMillisBarClose();
                stochList.add(new Stochastics(timeInMillis, k[smaDPeriod - 1], d));
            }
            currentBarIndex++;
        }
        return stochList;
    }
    
    private List<Macd> doCalculateMacd(List<Bar> bars, int p1, int p2, int p3) {
        List<Macd> macdList = new ArrayList<>();
        double macdEma1 = 0d, macdEma2 = 0d;
        double macdL, macdSl = 0d, macdH;
        double m1 = 2d/((double) p1 +  1d);
        double m2 = 2d/((double) p2 +  1d);
        double m3 = 2d/((double) p3 +  1d);
        for (int i = 0; i < p1; i++) {
            macdEma1 += bars.get(i).getqClose();
        }
        macdEma1 = macdEma1/(double) p1;
        for (int i = p1; i < p2; i++) {
            macdEma1 = (bars.get(i).getqClose() - macdEma1) * m1 + macdEma1;
        }
        for (int i = 0; i < p2; i++) {
            macdEma2 += bars.get(i).getqClose();
        }
        macdEma2 = macdEma2/(double) p2;
        for (int i = p2; i < p2 + p3; i++) {
            macdEma1 = (bars.get(i).getqClose() - macdEma1) * m1 + macdEma1;
            macdEma2 = (bars.get(i).getqClose() - macdEma2) * m2 + macdEma2;
            macdL = macdEma1 - macdEma2;
            macdSl += macdL;
        }
        macdSl = macdSl/(double) p3;
        for (int i = p3; i < bars.size(); i ++) {
            macdEma1 = (bars.get(i).getqClose() - macdEma1) * m1 + macdEma1;
            macdEma2 = (bars.get(i).getqClose() - macdEma2) * m2 + macdEma2;
            macdL = macdEma1 - macdEma2;
            macdSl = (macdL - macdSl) * m3 + macdSl;
            macdH = macdL - macdSl;
            if (i >= HtrSettings.BARS_REQUIRED) {
                Long timeInMillis = bars.get(i).getTimeInMillisBarClose();
                macdList.add(new Macd(timeInMillis, macdL, macdSl,macdH));
            }
        }
        return macdList;
    }
}