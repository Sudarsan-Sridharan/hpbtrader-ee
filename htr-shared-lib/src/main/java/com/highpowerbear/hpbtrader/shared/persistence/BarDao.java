package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface BarDao {
    void createBars(Series series, List<Bar> bars);
    List<Bar> getBars(Series series, Integer numBars);
    List<Bar> getPagedBars(Series series, Integer start, Integer limit);
    Bar getLastBar(Series series);
    Long getNumBars(Series series);
}
