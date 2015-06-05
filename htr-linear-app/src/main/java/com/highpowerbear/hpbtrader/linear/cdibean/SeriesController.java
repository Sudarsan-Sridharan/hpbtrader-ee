package com.highpowerbear.hpbtrader.linear.cdibean;


import com.highpowerbear.hpbtrader.linear.cdibean.series.SeriesBean;
import com.highpowerbear.hpbtrader.linear.cdibean.tab.ChartParams;
import com.highpowerbear.hpbtrader.linear.common.LinData;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.entity.Strategy;
import com.highpowerbear.hpbtrader.linear.ibclient.IbController;
import com.highpowerbear.hpbtrader.linear.quote.QuoteController;
import com.highpowerbear.hpbtrader.linear.quote.model.RealtimeData;
import com.highpowerbear.hpbtrader.linear.strategy.StrategyController;
import com.highpowerbear.hpbtrader.linear.persistence.DatabaseDao;
import com.highpowerbear.hpbtrader.linear.persistence.WebDao;
import com.highpowerbear.hpbtrader.linear.persistence.model.SeriesRecord;
import org.primefaces.event.SelectEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Named
@ApplicationScoped
public class SeriesController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private StrategyController strategyController;
    @Inject private LinData htrData;
    @Inject private IbController ibController;
    @Inject private QuoteController quoteController;
    @Inject private DatabaseDao databaseDao;
    @Inject private WebDao webDao;
    @Inject private SeriesBean seriesBean;
    @Inject private TabController tabController;

    public List<SeriesRecord> getSeriesRecords() {
        return webDao.getSeriesRecords(seriesBean.getShowDisabledSeries());
    }

    public void connect() {
        ibController.connect();
    }
    
    public void disconnect() {
        ibController.disconnect();
    }

    public boolean isConnected() {
        return ibController.isConnected();
    }

    public void backfillManual() {
        quoteController.backfillManual(seriesBean.getSelectedSeriesRecord().getSeries());
    }
    
    public String show() {
        if (seriesBean.getTabStrategyId() == null) {
            return "series";
        }
        Strategy tabStrategy = databaseDao.findStrategy(seriesBean.getTabStrategyId());
        return tabController.show(tabStrategy);
    }

    public void showChart() {
        seriesBean.setChartParams(new ChartParams(seriesBean.getSelectedSeriesRecord().getSeries()));
        seriesBean.getChartParams().setEma1Period(20);
        seriesBean.getChartParams().setEma2Period(50);
        seriesBean.getChartParams().setNumBars(500);
    }

    public String backtest() {
        if (seriesBean.getTabStrategyId() == null) {
            return "series";
        }
        Strategy tabStrategy = databaseDao.findStrategy(seriesBean.getTabStrategyId());
        if (seriesBean.getBacktestAll()) {
            return tabController.backtest(tabStrategy, null, null);
        } else {
            Calendar calStart = LinUtil.getCalendar();
            calStart.setTimeInMillis(seriesBean.getBacktestStartDate().getTime());
            Calendar calEnd = LinUtil.getCalendar();
            calEnd.setTimeInMillis(seriesBean.getBacktestEndDate().getTime());
            return tabController.backtest(tabStrategy, calStart, calEnd);
        }
    }

    public void onSeriesSelect(SelectEvent event) {
        seriesBean.setTabStrategyId(seriesBean.getSelectedSeriesRecord().getSeries().getActiveStrategy().getId());
    }

    public void toggleRealtimeData() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return;
        }
        quoteController.toggleRealtimeData(seriesBean.getSelectedSeriesRecord().getSeries());
    }

    public void toggleEnableSeries() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return;
        }
        Series selectedSeries = seriesBean.getSelectedSeriesRecord().getSeries();
        selectedSeries.setIsEnabled(!selectedSeries.getIsEnabled());
        if (!selectedSeries.getIsEnabled() && webDao.isRealtimeDataEnabled(selectedSeries)) {
            quoteController.toggleRealtimeData(selectedSeries);
        }
        databaseDao.updateSeries(selectedSeries);
        strategyController.swapStrategyLogic(selectedSeries);
    }

    public void moveSeriesUp() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return;
        }
        Series prevSeries = null;
        Series selectedSeries = seriesBean.getSelectedSeriesRecord().getSeries();
        for (Series s : databaseDao.getAllSeries(seriesBean.getShowDisabledSeries())) {
            if (s.getId().equals(selectedSeries.getId())) {
                if (prevSeries == null) {
                    break;
                } else {
                    Integer prevDisplayOrder = prevSeries.getDisplayOrder();
                    prevSeries.setDisplayOrder(selectedSeries.getDisplayOrder());
                    selectedSeries.setDisplayOrder(prevDisplayOrder);
                    databaseDao.updateSeries(prevSeries);
                    databaseDao.updateSeries(selectedSeries);
                    break;
                }
            }
            prevSeries = s;
        }
    }

    public void moveSeriesDown() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return;
        }
        List<Series> reversedSeries = databaseDao.getAllSeries(seriesBean.getShowDisabledSeries());
        Collections.reverse(reversedSeries);
        Series nextSeries = null;
        Series selectedSeries = seriesBean.getSelectedSeriesRecord().getSeries();
        for (Series s : reversedSeries) {
            if (s.getId().equals(selectedSeries.getId())) {
                if (nextSeries == null) {
                    break;
                } else {
                    Integer prevDisplayOrder = nextSeries.getDisplayOrder();
                    nextSeries.setDisplayOrder(selectedSeries.getDisplayOrder());
                    selectedSeries.setDisplayOrder(prevDisplayOrder);
                    databaseDao.updateSeries(nextSeries);
                    databaseDao.updateSeries(selectedSeries);
                    break;
                }
            }
            nextSeries = s;
        }
    }

    public void deleteSeries() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return;
        }
        Series selectedSeries = seriesBean.getSelectedSeriesRecord().getSeries();
        if (selectedSeries.getIsEnabled()) {
            toggleEnableSeries();
        }
        Integer deletedDisplayOrder = selectedSeries.getDisplayOrder();
        databaseDao.deleteSeries(selectedSeries);
        // reindex display order for all series below the deleted one
        List<Series> allSeries = databaseDao.getAllSeries(true);
        for (Series s : allSeries) {
            if (s.getDisplayOrder() > deletedDisplayOrder) {
                s.setDisplayOrder(s.getDisplayOrder() - 1);
            }
            databaseDao.updateSeries(s);
        }
        List<SeriesRecord> seriesRecords = getSeriesRecords();
        seriesBean.setSelectedSeriesRecord(seriesRecords.size() > 0 ? seriesRecords.get(0) : null);
    }

    public List<Order> getRecentOrders() {
        return databaseDao.getRecentOrders();
    }

    public String getOrderTriggerDescShort(Order order) {
        if (order == null) {
            return "";
        }
        String triggerDesc[] = order.getTriggerDesc().split(":");
        return triggerDesc[0];
    }

    public String getTimezone() {
        return LinSettings.TIMEZONE;
    }

    public Boolean getAllowManual() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return false;
        }
        return webDao.getAllowManual(seriesBean.getSelectedSeriesRecord().getSeries());
    }

    public Integer getHearbeatCount(Order order) {
        return webDao.getHearbeatCount(order);
    }

    public List<RealtimeData> getRealtimeDataList() {
        return webDao.getRealtimeDataList();
    }

    public Boolean isSelectedSeriesEnabled() {
        if (seriesBean.getSelectedSeriesRecord() == null) {
            return false;
        }
        return (seriesBean.getSelectedSeriesRecord().getSeries().getIsEnabled());
    }

    public Boolean isSeriesSelected() {
        return seriesBean.getSelectedSeriesRecord() != null;
    }
}
