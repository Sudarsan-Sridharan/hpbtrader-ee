package com.highpowerbear.hpbtrader.linear.cdibean.series;

import com.highpowerbear.hpbtrader.linear.cdibean.tab.ChartParams;
import com.highpowerbear.hpbtrader.linear.common.LinUtil;
import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.persistence.model.SeriesRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rkolar on 4/10/14.
 */
@Named
@ApplicationScoped
public class SeriesBean implements Serializable {
    private SeriesRecord selectedSeriesRecord;
    private Order selectedOrder;
    private ChartParams chartParams;
    private Integer tabStrategyId;
    private Date backtestStartDate = LinUtil.getCalendarMonthsOffset(-6).getTime();
    private Date backtestEndDate = LinUtil.getCalendar().getTime();
    private Boolean backtestAll = true;
    private Boolean showDisabledSeries = false;

    public SeriesRecord getSelectedSeriesRecord() {
        return selectedSeriesRecord;
    }

    public void setSelectedSeriesRecord(SeriesRecord selectedSeriesRecord) {
        this.selectedSeriesRecord = selectedSeriesRecord;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public ChartParams getChartParams() {
        return chartParams;
    }

    public void setChartParams(ChartParams chartParams) {
        this.chartParams = chartParams;
    }

    public Integer getTabStrategyId() {
        return tabStrategyId;
    }

    public void setTabStrategyId(Integer tabStrategyId) {
        this.tabStrategyId = tabStrategyId;
    }

    public Date getBacktestStartDate() {
        return backtestStartDate;
    }

    public void setBacktestStartDate(Date backtestStartDate) {
        this.backtestStartDate = backtestStartDate;
    }

    public Date getBacktestEndDate() {
        return backtestEndDate;
    }

    public void setBacktestEndDate(Date backtestEndDate) {
        this.backtestEndDate = backtestEndDate;
    }

    public Boolean getBacktestAll() {
        return backtestAll;
    }

    public void setBacktestAll(Boolean backtestAll) {
        this.backtestAll = backtestAll;
    }

    public Boolean getShowDisabledSeries() {
        return showDisabledSeries;
    }

    public void setShowDisabledSeries(Boolean showDisabledSeries) {
        this.showDisabledSeries = showDisabledSeries;
    }
}
