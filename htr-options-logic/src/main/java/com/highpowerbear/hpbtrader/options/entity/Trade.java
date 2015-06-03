package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.common.SingletonRepo;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author robertk
 */
@Entity
public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="trade")
    @Id
    @GeneratedValue(generator="trade")
    private Long id;
    private String underlying;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateInitOpen;
    @Enumerated(EnumType.STRING)
    private IbApiEnums.OptionType optionType;
    private String optionSymbol;
    private Integer tradeQuantity;
    private Integer currentPosition = 0;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateClosed;
    @Enumerated(EnumType.STRING)
    private OptEnums.TradeStatus tradeStatus;
    @OneToMany(mappedBy = "trade", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate ASC, id ASC")
    private List<TradeEvent> events;
    
    public void addInitEvent(OptEnums.TradeStatus status) {
        if (!OptEnums.TradeStatus.INIT_OPEN.equals(status) && !OptEnums.TradeStatus.INIT_FIRST_EXIT.equals(status) && !OptEnums.TradeStatus.INIT_CLOSE.equals(status)) {
            return;
        }
        addEvent(status, (currentPosition != null ? currentPosition : 0));
    }
    
    private void addEvent(OptEnums.TradeStatus status, Integer newPosition) {
        this.tradeStatus = status;
        TradeEvent event = new TradeEvent();
        event.setTrade(this);
        event.setEventDate(OptUtil.getNowCalendar());
        event.setTradeStatus(tradeStatus);
        event.setCurrentPosition(newPosition);
        events.add(event);
        this.setCurrentPosition(event.getCurrentPosition());
        if (OptEnums.TradeStatus.INIT_OPEN.equals(event.getTradeStatus())) {
            this.setDateInitOpen(event.getEventDate());
        } else if (OptEnums.TradeStatus.CLOSED.equals(event.getTradeStatus())) {
            this.setDateClosed(event.getEventDate());
        }
    }
    
    public void addEventByOrderFilled(IbOrder ibOrder) {
        if (ibOrder == null || !ibOrder.getTrade().equals(this) || !OptEnums.OrderStatus.FILLED.equals(ibOrder.getOrderStatus())) {
            return;
        }
        if (OptEnums.TradeStatus.INIT_OPEN.equals(tradeStatus) && IbApiEnums.Action.BUY.equals(ibOrder.getAction())) {
            addEvent(OptEnums.TradeStatus.OPEN, currentPosition + ibOrder.getQuantity());
            SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseMade(this);
        } else if (OptEnums.TradeStatus.INIT_FIRST_EXIT.equals(tradeStatus) && IbApiEnums.Action.SELL.equals(ibOrder.getAction())) {
            addEvent(OptEnums.TradeStatus.FIRST_EXITED, currentPosition - ibOrder.getQuantity());
        } else if (OptEnums.TradeStatus.INIT_CLOSE.equals(tradeStatus) && IbApiEnums.Action.SELL.equals(ibOrder.getAction())) {
            addEvent(OptEnums.TradeStatus.CLOSED, currentPosition - ibOrder.getQuantity());
            SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseReleased(this);
        }
    }
    
    public void addEventByOrderCanceled(IbOrder ibOrder) {
        if (ibOrder == null || !ibOrder.getTrade().equals(this) || !OptEnums.OrderStatus.EXT_CANCELED.equals(ibOrder.getOrderStatus())) {
            return;
        }
        addEvent(OptEnums.TradeStatus.INVALID, null);
        SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseReleased(this);
    }
    
    public void addEventByOrderUnknown(IbOrder ibOrder) {
        if (ibOrder == null || !ibOrder.getTrade().equals(this) || !OptEnums.OrderStatus.UNKNOWN.equals(ibOrder.getOrderStatus())) {
            return;
        }
        addEvent(OptEnums.TradeStatus.INVALID, null);
        SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseReleased(this);
    }
    
    public boolean canAcceptSignal(OptEnums.SignalAction signalAction) {
        if (OptEnums.TradeStatus.INIT_OPEN.equals(tradeStatus) || OptEnums.TradeStatus.INIT_FIRST_EXIT.equals(tradeStatus) || OptEnums.TradeStatus.INIT_CLOSE.equals(tradeStatus)) {
            return false;
        } else if (OptEnums.TradeStatus.OPEN.equals(tradeStatus) || OptEnums.TradeStatus.FIRST_EXITED.equals(tradeStatus)) {
            return (OptEnums.SignalAction.CLOSE.equals(signalAction) || OptEnums.SignalAction.REVERSE.equals(signalAction));
        }
        return false;
    }
    
    public boolean lotReadyToClose(OptEnums.Lot lot) {
        if (OptEnums.Lot.FIRST.equals(lot)) {
            return (OptEnums.TradeStatus.OPEN.equals(tradeStatus));
        } else {
            return (OptEnums.TradeStatus.FIRST_EXITED.equals(tradeStatus));
        }
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }
    
    public Calendar getDateInitOpen() {
        return dateInitOpen;
    }

    public void setDateInitOpen(Calendar dateInitOpen) {
        this.dateInitOpen = dateInitOpen;
    }

    public IbApiEnums.OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(IbApiEnums.OptionType optionType) {
        this.optionType = optionType;
    }

    public String getOptionSymbol() {
        return optionSymbol;
    }

    public void setOptionSymbol(String optionSymbol) {
        this.optionSymbol = optionSymbol;
    }

    public Integer getTradeQuantity() {
        return tradeQuantity;
    }

    public void setTradeQuantity(Integer tradeQuantity) {
        this.tradeQuantity = tradeQuantity;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Calendar getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Calendar dateClosed) {
        this.dateClosed = dateClosed;
    }

    public OptEnums.TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(OptEnums.TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public List<TradeEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TradeEvent> events) {
        this.events = events;
    }
    
    public String print() {
        return tradeStatus.getName() + ", " + optionSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trade)) return false;

        Trade trade = (Trade) o;

        return !(id != null ? !id.equals(trade.id) : trade.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
