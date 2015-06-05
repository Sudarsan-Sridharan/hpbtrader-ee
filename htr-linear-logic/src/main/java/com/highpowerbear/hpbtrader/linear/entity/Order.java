package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.ibclient.IbApiEnums;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author rkolar
 */
@Entity
@Table(name = "lin_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="lin_order", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_order")
    private Long id;
    private Integer ibPermId; // not null only for IB order
    private Integer ibOrderId; // not null only for IB orders
    @ManyToOne
    private Strategy strategy;
    @Enumerated(EnumType.STRING)
    // must be present in trade order, since it can change during the existence of strategy
    private LinEnums.StrategyMode strategyMode;
    private String triggerDesc;
    private LinEnums.SubmitType submitType;
    @Enumerated(EnumType.STRING)
    private LinEnums.OrderAction orderAction;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private LinEnums.OrderType orderType;
    private Double limitPrice;
    private Double stopPrice;
    private Double fillPrice;
    @Enumerated(EnumType.STRING)
    private LinEnums.OrderStatus orderStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate ASC")
    private List<OrderEvent> events = new ArrayList<>();
    
    public void addEvent(LinEnums.OrderStatus orderStatus, Calendar date) {
        this.orderStatus = orderStatus;
        OrderEvent event = new OrderEvent();
        event.setOrder(this);
        event.setEventDate(date);
        event.setOrderStatus(orderStatus);
        events.add(event);
        if (LinEnums.OrderStatus.NEW.equals(event.getOrderStatus())) {
            this.setDateCreated(event.getEventDate());
        }
    }
    
    public Calendar getEventDate(LinEnums.OrderStatus orderStatus) {
        Calendar eventDate = null;
        for (OrderEvent oe : events) {
            if (orderStatus.equals(oe.getOrderStatus())) {
                eventDate = oe.getEventDate();
                break;
            }
        }
        return eventDate;
    }
    
    public boolean isOpeningOrder() {
        return orderAction != null && (LinEnums.OrderAction.BTO.equals(orderAction) || LinEnums.OrderAction.STO.equals(orderAction));
    }
    
    public boolean isClosingOrder() {
        return orderAction != null && (LinEnums.OrderAction.BTC.equals(orderAction) || LinEnums.OrderAction.STC.equals(orderAction) || LinEnums.OrderAction.BREV.equals(orderAction) || LinEnums.OrderAction.SREV.equals(orderAction));
    }
    
    public boolean isBuyOrder() {
        return orderAction != null && (LinEnums.OrderAction.BTO.equals(orderAction) || LinEnums.OrderAction.BTC.equals(orderAction) || LinEnums.OrderAction.BREV.equals(orderAction));
    }
    
    public boolean isSellOrder() {
        return orderAction != null && (LinEnums.OrderAction.STO.equals(orderAction) || LinEnums.OrderAction.STC.equals(orderAction) || LinEnums.OrderAction.SREV.equals(orderAction));
    }
    
    public boolean isReversalOrder() {
        if (orderAction == null) {
            return false;
        }
        return (LinEnums.OrderAction.BREV.equals(orderAction) || LinEnums.OrderAction.SREV.equals(orderAction));
    }
    
    public String getDescription() {
        Series series = strategy.getSeries();
        return series.getSymbol() + ", " + series.getInterval().getDisplayName() + ", " +  strategy.getStrategyType().getDisplayName() + ": " + orderAction.toString();
    }
    
    public com.ib.client.Order createIbOrder() {
        com.ib.client.Order ibOrder = new com.ib.client.Order();
        ibOrder.m_action = (this.isBuyOrder() ? IbApiEnums.Action.BUY.getName() : IbApiEnums.Action.SELL.getName());
        ibOrder.m_orderType = LinEnums.OrderType.getIbOrderType(this.getOrderType());
        ibOrder.m_auxPrice = (this.stopPrice != null ? this.stopPrice : 0d);
        ibOrder.m_lmtPrice = (this.limitPrice!= null ? this.limitPrice : 0d);
        ibOrder.m_totalQuantity = this.quantity;
        ibOrder.m_tif = IbApiEnums.Tif.GTC.getName();
        return ibOrder;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getIbPermId() {
        return ibPermId;
    }

    public void setIbPermId(Integer ibPermId) {
        this.ibPermId = ibPermId;
    }
    
    public Integer getIbOrderId() {
        return ibOrderId;
    }

    public void setIbOrderId(Integer ibOrderId) {
        this.ibOrderId = ibOrderId;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public LinEnums.StrategyMode getStrategyMode() {
        return strategyMode;
    }

    public void setStrategyMode(LinEnums.StrategyMode strategyMode) {
        this.strategyMode = strategyMode;
    }

    public LinEnums.SubmitType getSubmitType() {
        return submitType;
    }

    public void setSubmitType(LinEnums.SubmitType submitType) {
        this.submitType = submitType;
    }

    public LinEnums.OrderAction getOrderAction() {
        return orderAction;
    }

    public void setOrderAction(LinEnums.OrderAction orderAction) {
        this.orderAction = orderAction;
    }

    public String getTriggerDesc() {
        return triggerDesc;
    }

    public void setTriggerDesc(String triggerDesc) {
        this.triggerDesc = triggerDesc;
    }

    public LinEnums.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(LinEnums.OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(Double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
    }

    public LinEnums.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(LinEnums.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<OrderEvent> getEvents() {
        return events;
    }

    public void setEvents(List<OrderEvent> events) {
        this.events = events;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Order)) {
            return false;
        }
        Order other = (Order) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
