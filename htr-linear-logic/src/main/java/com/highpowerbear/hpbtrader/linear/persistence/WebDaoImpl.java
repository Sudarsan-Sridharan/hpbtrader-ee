package com.highpowerbear.hpbtrader.linear.persistence;

import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Quote;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.entity.Trade;
import com.highpowerbear.hpbtrader.linear.quote.model.RealtimeData;
import com.highpowerbear.hpbtrader.linear.persistence.model.SeriesRecord;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertk on 4/28/15.
 */
@Named
@ApplicationScoped
public class WebDaoImpl implements Serializable, WebDao {
    @Inject private DatabaseDao databaseDao;
    @Inject private LinData linData;

    @Override
    public List<SeriesRecord> getSeriesRecords(boolean disabledToo) {
        List<SeriesRecord> seriesRecords = new ArrayList<>();
        for (Series s : databaseDao.getAllSeries(disabledToo)) {
            SeriesRecord r = new SeriesRecord();
            r.setId(s.getId());
            r.setSeries(s);
            r.setDisplayOrder(s.getDisplayOrder());
            r.setUnderlying(s.getUnderlying());
            r.setSymbol(s.getSymbol());
            r.setCurrency(s.getCurrency().name());
            r.setInterval(s.getInterval().getDisplayName());
            r.setSecType(s.getSecType().getDisplayName());
            r.setExchange(s.getExchange().getDisplayName());
            r.setEnabled(s.getIsEnabled());
            r.setRealtimeDataEnabled(isRealtimeDataEnabled(s));
            r.setNumQuotes(databaseDao.getNumQuotes(s));
            r.setNumStrategies(s.getNumStrategies());
            Quote lastQuote = databaseDao.getLastQuote(s);
            r.setLastQuote(lastQuote != null ? databaseDao.getLastQuote(s).getqClose() : null);
            r.setActiveStrategy(s.getActiveStrategy().getStrategyType().getDisplayName());
            r.setStrategyMode(s.getActiveStrategy().getStrategyMode().name());
            r.setStrategyModeClass(s.getActiveStrategy().getStrategyMode().getColorClass());
            r.setCurrentPosition(s.getActiveStrategy().getCurrentPosition());
            r.setNumTrades(databaseDao.getNumTrades(s.getActiveStrategy()));
            r.setNumAllOrders(s.getActiveStrategy().getNumAllOrders());
            r.setNumFilledOrders(s.getActiveStrategy().getNumFilledOrders());
            r.setCumulativePl(s.getActiveStrategy().getCumulativePl());
            r.setCumulativePlClass(r.getCumulativePl() > 0d ? "col-green" : (r.getCumulativePl() == 0d ? "" : "col-red"));
            Trade activeTrade = databaseDao.getActiveTrade(s.getActiveStrategy());
            r.setTradeType(activeTrade != null ? activeTrade.getTradeType().getDisplayName() : "-");
            r.setTradeTypeClass(activeTrade != null ? activeTrade.getTradeType().getColorClass() : "");
            r.setUnrealizedPl(activeTrade != null ? activeTrade.getUnrealizedPl() : 0d);
            r.setUnrealizedPlClass(r.getUnrealizedPl() > 0d ? "col-green" : (r.getUnrealizedPl() == 0d ? "" : "col-red"));
            r.setTradeStatus(activeTrade != null ? activeTrade.getTradeStatus().getDisplayName() : "-");
            r.setTradeStatusClass(activeTrade != null ? activeTrade.getTradeStatus().getColorClass() : "");
            seriesRecords.add(r);
        }
        return seriesRecords;
    }

    @Override
    public Boolean getAllowManual(Series series) {
        Trade activeTrade = databaseDao.getActiveTrade(series.getActiveStrategy());
        return (series.getIsEnabled() && databaseDao.getLastQuote(series) != null && (activeTrade == null || activeTrade.isOpen()));
    }

    @Override
    public Integer getHearbeatCount(Order order) {
        return linData.getOpenOrderHeartbeatMap().get(order.getId());
    }

    @Override
    public List<RealtimeData> getRealtimeDataList() {
        return new ArrayList<>(linData.getRealtimeDataMap().values());
    }

    @Override
    public Boolean isRealtimeDataEnabled(Series series) {
        return linData.getRealtimeDataMap().containsKey(series.getSymbol());
    }
}
