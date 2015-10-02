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
@Table(name = "opt_trade")
public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="opt_trade")
    @Id
    @GeneratedValue(generator="opt_trade")
    private Long id;
    @ManyToOne
    private ComboTrade comboTrade;
    @Enumerated(EnumType.STRING)
    private OptEnums.Underlying underlying;
    @ManyToOne
    private Strategy strategy;
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
        if (!OptEnums.TradeStatus.INIT_OPEN.equals(status) && !OptEnums.TradeStatus.INIT_CLOSE.equals(status)) {
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
    
    public void addEventByOrderFilled(Order order) {
        if (order == null || !order.getTrade().equals(this) || !OptEnums.OrderStatus.FILLED.equals(order.getOrderStatus())) {
            return;
        }
        if (OptEnums.TradeStatus.INIT_OPEN.equals(tradeStatus) && IbApiEnums.Action.BUY.equals(order.getAction())) {
            addEvent(OptEnums.TradeStatus.OPEN, currentPosition + order.getQuantity());
            SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseMade(this);
        } else if (OptEnums.TradeStatus.INIT_CLOSE.equals(tradeStatus) && IbApiEnums.Action.SELL.equals(order.getAction())) {
            addEvent(OptEnums.TradeStatus.CLOSED, currentPosition - order.getQuantity());
            SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseReleased(this);
        }
    }
    
    public void addEventByOrderCanceled(Order order) {
        if (order == null || !order.getTrade().equals(this) || !OptEnums.OrderStatus.EXT_CANCELED.equals(order.getOrderStatus())) {
            return;
        }
        addEvent(OptEnums.TradeStatus.INVALID, null);
        SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseReleased(this);
    }
    
    public void addEventByOrderUnknown(Order order) {
        if (order == null || !order.getTrade().equals(this) || !OptEnums.OrderStatus.UNKNOWN.equals(order.getOrderStatus())) {
            return;
        }
        addEvent(OptEnums.TradeStatus.INVALID, null);
        SingletonRepo.getInstance().getOptData().getUnderlyingDataMap().get(underlying).purchaseReleased(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComboTrade getComboTrade() {
        return comboTrade;
    }

    public void setComboTrade(ComboTrade comboTrade) {
        this.comboTrade = comboTrade;
    }

    public OptEnums.Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(OptEnums.Underlying underlying) {
        this.underlying = underlying;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
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
        return tradeStatus.getLabel() + ", " + optionSymbol;
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
