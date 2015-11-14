package com.highpowerbear.hpbtrader.linear.persistence;

import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.persistence.model.SeriesRecord;
import com.highpowerbear.hpbtrader.linear.mktdata.model.RealtimeData;
import java.util.List;


/**
 * Created by robertk on 4/28/15.
 */
public interface WebDao {
    List<SeriesRecord> getSeriesRecords(boolean disabledToo);
    Boolean getAllowManual(Series series);
    Integer getHearbeatCount(Order order);
    List<RealtimeData> getRealtimeDataList();
    Boolean isRealtimeDataEnabled(Series series);
}
