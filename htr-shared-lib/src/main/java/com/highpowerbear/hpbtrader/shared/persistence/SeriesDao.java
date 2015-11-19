package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.Series;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface SeriesDao {
    void addSeries(Series series);
    List<Series> getAllSeries(boolean disabledToo);
    List<Series> getSeriesByInterval(HtrEnums.Interval interval);
    List<Series> getSeries(String symbol, HtrEnums.Interval interval);
    Series findSeries(Integer id);
    void updateSeries(Series series);
    Integer getHighestDisplayOrder();
    void deleteSeries(Series series);
}
