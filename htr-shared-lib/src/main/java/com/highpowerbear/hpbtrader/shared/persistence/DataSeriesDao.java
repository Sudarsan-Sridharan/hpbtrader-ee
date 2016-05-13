package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.DataBar;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.entity.Instrument;

import java.util.Calendar;
import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface DataSeriesDao {
    List<DataSeries> getAllDataSeries();
    List<DataSeries> getDataSeriesByInterval(HtrEnums.Interval interval);
    DataSeries getDataSeriesByAlias(String alias);
    DataSeries findDataSeries(Integer id);

    void createDataBars(DataSeries dataSeries, List<DataBar> dataBars);
    List<DataBar> getLastDataBars(DataSeries dataSeries, int numBars, int offsetFromLast);
    List<DataBar> getDataBars(DataSeries dataSeries, int numBars, Calendar lastDate);
    List<DataBar> getPagedDataBars(DataSeries dataSeries, int start, int limit);
    Long getNumDataBars(DataSeries dataSeries);
}
