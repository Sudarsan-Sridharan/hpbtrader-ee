package com.highpowerbear.hpbtrader.linear.persistence;

import com.highpowerbear.hpbtrader.linear.entity.IbOrder;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.model.RealtimeData;
import com.highpowerbear.hpbtrader.linear.model.SeriesRecord;

import java.util.List;


/**
 * Created by robertk on 4/28/15.
 */
public interface WebDao {
    List<SeriesRecord> getSeriesRecords(boolean disabledToo);
    Boolean getAllowManual(Series series);
    Integer getHearbeatCount(IbOrder ibOrder);
    List<RealtimeData> getRealtimeDataList();
    Boolean isRealtimeDataEnabled(Series series);
}
