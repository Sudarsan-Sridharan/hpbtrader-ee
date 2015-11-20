package com.highpowerbear.hpbtrader.shared.persistence;

import com.highpowerbear.hpbtrader.shared.entity.Bar;
import com.highpowerbear.hpbtrader.shared.entity.Series;

import java.util.List;

/**
 * Created by robertk on 19.11.2015.
 */
public interface BarDao {
    void createBars(List<Bar> bars);
    List<Bar> getBars(Integer seriesId, Integer numBars);
    Bar getLastBar(Series series);
    Long getNumBars(Series series);
}
