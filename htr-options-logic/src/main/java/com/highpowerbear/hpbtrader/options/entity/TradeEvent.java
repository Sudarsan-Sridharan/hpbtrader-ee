package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@Entity
public class TradeEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="tradeEvent")
    @Id
    @GeneratedValue(generator="tradeEvent")
    private Long id;
    private Integer currentPosition;
    @Enumerated(EnumType.STRING)
    private OptEnums.TradeStatus tradeStatus;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @ManyToOne
    Trade trade;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public OptEnums.TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(OptEnums.TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeEvent)) return false;

        TradeEvent that = (TradeEvent) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
