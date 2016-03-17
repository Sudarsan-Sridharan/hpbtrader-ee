package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.entity.Instrument;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface DataSeriesDao {
    void createSeries(DataSeries dataSeries);
    List<DataSeries> getAllSeries(boolean inactiveToo);
    List<DataSeries> getSeriesByInterval(HtrEnums.Interval interval);
    List<DataSeries> getSeries(Instrument instrument, HtrEnums.Interval interval);
    DataSeries getSeriesByAlias(String alias);
    DataSeries findSeries(Integer id);
    void updateSeries(DataSeries dataSeries);
    Integer getHighestDisplayOrder();
    void deleteSeries(DataSeries dataSeries);

    void createBars(DataSeries dataSeries, List<DataBar> dataBars);
    List<DataBar> getBars(DataSeries dataSeries, Integer numBars);
    List<DataBar> getPagedBars(DataSeries dataSeries, Integer start, Integer limit);
    DataBar getLastBar(DataSeries dataSeries);
    Long getNumBars(DataSeries dataSeries);
}
