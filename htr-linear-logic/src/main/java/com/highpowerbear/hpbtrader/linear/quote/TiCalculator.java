package com.highpowerbear.hpbtrader.linear.quote;

import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Ema;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Macd;
import com.highpowerbear.hpbtrader.linear.quote.indicator.Stochastics;

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
    public List<Ema> calculateEma(List<Quote> quotes, Integer emaPeriod) {
        if (quotes.size() < LinSettings.BARS_REQUIRED || emaPeriod > LinSettings.MAX_EMA_PERIOD) {
            return null;
        }
        return doCalculateEma(quotes, emaPeriod);
    }
    
    public List<Stochastics> calculateStoch(List<Quote> quotes) {
        if (quotes.size() < LinSettings.BARS_REQUIRED) {
            return null;
        }
        return doCalculateStoch(quotes, 14, 3, 3);
    }
    
    public List<Macd> calculateMacd(List<Quote> quotes) {
        if (quotes.size() < LinSettings.BARS_REQUIRED) {
            return null;
        }
        return doCalculateMacd(quotes, 12, 26, 9);
    }
    
    private List<Ema> doCalculateEma(List<Quote> quotes, int period) {
        List<Ema> emaList = new ArrayList<>();
        double sma = 0d;
        for (int i = 0; i < period; i++) {
            sma += quotes.get(i).getqClose();
        }
        sma = sma/(double) period;
        double ema = sma;
        double mult = 2d/((double) period +  1d);
        for (int i = period; i < quotes.size(); i++) {
            ema = (quotes.get(i).getqClose() - ema) * mult + ema;
            if (i >= LinSettings.BARS_REQUIRED) {
                Long timeInMillis = quotes.get(i).getTimeInMillisBarClose();
                emaList.add(new Ema(timeInMillis, ema));
            }
        }
        return emaList;
    }
    
    private List<Stochastics> doCalculateStoch(List<Quote> quotes, int lookbackPeriod, int smaKPeriod, int smaDPeriod) {
        List<Stochastics> stochList = new ArrayList<>();
        double[] kFast = new double[smaKPeriod];
        double[] k = new double[smaDPeriod];
        double d;
        int currentQuoteIndex = lookbackPeriod - 1;
        while (currentQuoteIndex < quotes.size()) {
            double highestHigh = quotes.get(currentQuoteIndex).getHigh();
            double lowestLow = quotes.get(currentQuoteIndex).getLow();
            for (int i = currentQuoteIndex; i >= currentQuoteIndex - lookbackPeriod + 1; i--) {
                if (quotes.get(i).getHigh() > highestHigh) {
                    highestHigh = quotes.get(i).getHigh();
                }
                if (quotes.get(i).getLow() < lowestLow) {
                    lowestLow = quotes.get(i).getLow();
                }
            }
            for (int i = 0; i < smaKPeriod - 1; i++) {
                kFast[i] = kFast[i + 1];
            }
            double currentQuote = quotes.get(currentQuoteIndex).getqClose();
            kFast[smaKPeriod - 1] =  (currentQuote - lowestLow)/(highestHigh - lowestLow) * 100d;
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
            if (currentQuoteIndex >= LinSettings.BARS_REQUIRED) {
                Long timeInMillis = quotes.get(currentQuoteIndex).getTimeInMillisBarClose();
                stochList.add(new Stochastics(timeInMillis, k[smaDPeriod - 1], d));
            }
            currentQuoteIndex++;
        }
        return stochList;
    }
    
    private List<Macd> doCalculateMacd(List<Quote> quotes, int p1, int p2, int p3) {
        List<Macd> macdList = new ArrayList<>();
        double macdEma1 = 0d, macdEma2 = 0d;
        double macdL, macdSl = 0d, macdH;
        double m1 = 2d/((double) p1 +  1d);
        double m2 = 2d/((double) p2 +  1d);
        double m3 = 2d/((double) p3 +  1d);
        for (int i = 0; i < p1; i++) {
            macdEma1 += quotes.get(i).getqClose();
        }
        macdEma1 = macdEma1/(double) p1;
        for (int i = p1; i < p2; i++) {
            macdEma1 = (quotes.get(i).getqClose() - macdEma1) * m1 + macdEma1;
        }
        for (int i = 0; i < p2; i++) {
            macdEma2 += quotes.get(i).getqClose();
        }
        macdEma2 = macdEma2/(double) p2;
        for (int i = p2; i < p2 + p3; i++) {
            macdEma1 = (quotes.get(i).getqClose() - macdEma1) * m1 + macdEma1;
            macdEma2 = (quotes.get(i).getqClose() - macdEma2) * m2 + macdEma2;
            macdL = macdEma1 - macdEma2;
            macdSl += macdL;
        }
        macdSl = macdSl/(double) p3;
        for (int i = p3; i < quotes.size(); i ++) {
            macdEma1 = (quotes.get(i).getqClose() - macdEma1) * m1 + macdEma1;
            macdEma2 = (quotes.get(i).getqClose() - macdEma2) * m2 + macdEma2;
            macdL = macdEma1 - macdEma2;
            macdSl = (macdL - macdSl) * m3 + macdSl;
            macdH = macdL - macdSl;
            if (i >= LinSettings.BARS_REQUIRED) {
                Long timeInMillis = quotes.get(i).getTimeInMillisBarClose();
                macdList.add(new Macd(timeInMillis, macdL, macdSl,macdH));
            }
        }
        return macdList;
    }
}