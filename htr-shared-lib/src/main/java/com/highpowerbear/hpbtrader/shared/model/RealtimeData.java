package com.highpowerbear.hpbtrader.shared.model;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;
import com.highpowerbear.hpbtrader.shared.entity.Instrument;
import com.ib.client.TickType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by rkolar on 5/23/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RealtimeData {
    @XmlTransient
    private DataSeries dataSeries;
    @XmlTransient
    private int ibRequestId;

    // price fields
    private RealtimeField<Double> bid;
    private RealtimeField<Double> ask;
    private RealtimeField<Double> last;
    private RealtimeField<Double> close;
    private RealtimeField<Double> changePct;

    // size fields
    private RealtimeField<Integer> bidSize;
    private RealtimeField<Integer> askSize;
    private RealtimeField<Integer> lastSize;

    private RealtimeField<Integer> volume;

    public RealtimeData(DataSeries dataSeries) {
        this.dataSeries = dataSeries;
        this.ibRequestId = dataSeries.getId() * HtrDefinitions.IB_REQUEST_MULT + 10;
        initFields();
    }

    @XmlElement
    public Integer getDataSeriesId() {
        return dataSeries.getId();
    }

    @XmlElement
    public Instrument getInstrument() {
        return dataSeries.getInstrument();
    }

    @XmlElement
    public String getChangePctStr() {
        return changePct.getValue() == null ? "N/A" : HtrUtil.round(changePct.getValue(), 2) + "%";
    }

    private void initFields() {
        bid = new RealtimeField<>(HtrDefinitions.INVALID_PRICE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.BID);
        ask = new RealtimeField<>(HtrDefinitions.INVALID_PRICE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.ASK);
        last = new RealtimeField<>(HtrDefinitions.INVALID_PRICE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.LAST);
        close = new RealtimeField<>(HtrDefinitions.INVALID_PRICE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.CLOSE);
        changePct = new RealtimeField<>(null, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.CHANGE_PCT);
        bidSize = new RealtimeField<>(HtrDefinitions.INVALID_SIZE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.BID_SIZE);
        askSize = new RealtimeField<>(HtrDefinitions.INVALID_SIZE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.ASK_SIZE);
        lastSize = new RealtimeField<>(HtrDefinitions.INVALID_SIZE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.LAST_SIZE);
        volume = new RealtimeField<>(HtrDefinitions.INVALID_SIZE, HtrEnums.RealtimeStatus.UNCHANGED, HtrEnums.RealtimeFieldName.VOLUME);
    }

    public String createUpdateMessage(int field, double price) {
        StringBuilder sb = new StringBuilder();
        sb.append("rt,").append(getDataSeriesId()).append(",").append(dataSeries.getInstrument().getSymbol()).append(",");
        switch(field) {
            case TickType.BID:
                setValueStatus(bid, price);
                sb.append(bid.getFieldName()).append(",").append(bid.getValue()).append(",").append(bid.getStatus());
                break;
            case TickType.ASK:
                setValueStatus(ask, price);
                sb.append(ask.getFieldName()).append(",").append(ask.getValue()).append(",").append(ask.getStatus());
                break;
            case TickType.LAST:
                setValueStatus(last, price);
                sb.append(last.getFieldName()).append(",").append(last.getValue()).append(",").append(last.getStatus());
                break;
            case TickType.CLOSE:
                setValueStatus(close, price);
                sb.append(close.getFieldName()).append(",").append(close.getValue()).append(",").append(close.getStatus());
                break;

            default: return null;
        }
        return sb.toString();
    }

    public String createUpdateMessage(int field, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("rt,").append(getDataSeriesId()).append(",").append(dataSeries.getInstrument().getSymbol()).append(",");
        switch(field) {
            case TickType.BID_SIZE:
                setValueStatus(bidSize, size);
                sb.append(bidSize.getFieldName()).append(",").append(bidSize.getValue()).append(",").append(bidSize.getStatus());
                break;
            case TickType.ASK_SIZE:
                setValueStatus(askSize, size);
                sb.append(askSize.getFieldName()).append(",").append(askSize.getValue()).append(",").append(askSize.getStatus());
                break;
            case TickType.LAST_SIZE:
                setValueStatus(lastSize, size);
                sb.append(lastSize.getFieldName()).append(",").append(lastSize.getValue()).append(",").append(lastSize.getStatus());
                break;
            case TickType.VOLUME:
                setValueStatus(volume, size);
                sb.append(volume.getFieldName()).append(",").append(volume.getValue()).append(",").append(volume.getStatus());
                break;

            default: return null;
        }
        return sb.toString();
    }

    public String createChangePctUpdateMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append("rt,").append(getDataSeriesId()).append(",").append(dataSeries.getInstrument().getSymbol()).append(",");
        if (!HtrEnums.SecType.CASH.equals(dataSeries.getInstrument().getSecType()) && !HtrDefinitions.INVALID_PRICE.equals(last.getValue()) && !HtrDefinitions.INVALID_PRICE.equals(close.getValue())) {
            double price = ((last.getValue() - close.getValue()) / close.getValue()) * 100d;
            setValueStatusChangePct(changePct, price);

        } else if (HtrEnums.SecType.CASH.equals(dataSeries.getInstrument().getSecType()) && !HtrDefinitions.INVALID_PRICE.equals(ask.getValue()) && !HtrDefinitions.INVALID_PRICE.equals(close.getValue())) {
            double price = ((ask.getValue() - close.getValue()) / close.getValue()) * 100d;
            setValueStatusChangePct(changePct, price);
        }
        sb.append(changePct.getFieldName()).append(",").append(getChangePctStr()).append(",").append(changePct.getStatus());
        return sb.toString();
    }

    private void setValueStatus(RealtimeField<Double> v, Double price) {
        v.setValueStatus(price > v.getValue() ? HtrEnums.RealtimeStatus.UPTICK : (price < v.getValue() ? HtrEnums.RealtimeStatus.DOWNTICK : HtrEnums.RealtimeStatus.UNCHANGED));
        v.setValue(price);
    }

    private void setValueStatus(RealtimeField<Integer> v, Integer size) {
        v.setValueStatus(size > v.getValue() ? HtrEnums.RealtimeStatus.UPTICK : (size < v.getValue() ? HtrEnums.RealtimeStatus.DOWNTICK : HtrEnums.RealtimeStatus.UNCHANGED));
        v.setValue(size);
    }

    private void setValueStatusChangePct(RealtimeField<Double> v, Double changePct) {
        if (changePct == null || changePct == 0d) {
            v.setValueStatus(HtrEnums.RealtimeStatus.UNCHANGED);
        } else if (changePct > 0d) {
            v.setValueStatus(HtrEnums.RealtimeStatus.POSITIVE);
        } else {
            v.setValueStatus(HtrEnums.RealtimeStatus.NEGATIVE);
        }
        v.setValue(changePct);
    }

    public DataSeries getDataSeries() {
        return dataSeries;
    }

    public int getIbRequestId() {
        return ibRequestId;
    }

    public RealtimeField<Double> getBid() {
        return bid;
    }

    public RealtimeField<Double> getAsk() {
        return ask;
    }

    public RealtimeField<Double> getLast() {
        return last;
    }

    public RealtimeField<Integer> getBidSize() {
        return bidSize;
    }

    public RealtimeField<Integer> getAskSize() {
        return askSize;
    }

    public RealtimeField<Integer> getLastSize() {
        return lastSize;
    }

    public RealtimeField<Integer> getVolume() {
        return volume;
    }

    public RealtimeField<Double> getClose() {
        return close;
    }

    public RealtimeField<Double> getChangePct() {
        return changePct;
    }
}
