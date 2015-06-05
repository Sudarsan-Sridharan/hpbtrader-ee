package com.highpowerbear.hpbtrader.persistence;

import com.highpowerbear.hpbtrader.linear.entity.Order;
import com.highpowerbear.hpbtrader.linear.entity.Series;
import com.highpowerbear.hpbtrader.linear.quote.model.RealtimeData;
import com.highpowerbear.hpbtrader.persistence.model.SeriesRecord;
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
