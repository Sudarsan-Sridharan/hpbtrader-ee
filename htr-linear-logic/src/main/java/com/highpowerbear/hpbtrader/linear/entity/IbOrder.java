package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.ibclient.IbApiEnums;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author rkolar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "lin_iborder")
public class IbOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="lin_order", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_order")
    private Long id;
    @XmlTransient
    @ManyToOne
    private IbAccount ibAccount;
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
    private LinEnums.IbOrderStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;
    @OneToMany(mappedBy = "ibOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate ASC")
    private List<OrderEvent> events = new ArrayList<>();
    
    public void addEvent(LinEnums.IbOrderStatus status, Calendar date, Double fillPrice) {
        this.status = status;
        OrderEvent event = new OrderEvent();
        event.setIbOrder(this);
        event.setEventDate(date);
        event.setStatus(status);
        event.setFillPrice(fillPrice);
        events.add(event);
        if (LinEnums.IbOrderStatus.NEW.equals(event.getStatus())) {
            this.setDateCreated(event.getEventDate());
        }
    }
    
    public Calendar getEventDate(LinEnums.IbOrderStatus ibOrderStatus) {
        Calendar eventDate = null;
        for (OrderEvent oe : events) {
            if (ibOrderStatus.equals(oe.getStatus())) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IbOrder ibOrder = (IbOrder) o;

        return !(id != null ? !id.equals(ibOrder.id) : ibOrder.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IbAccount getIbAccount() {
        return ibAccount;
    }

    public void setIbAccount(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
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

    public LinEnums.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LinEnums.IbOrderStatus ibOrderStatus) {
        this.status = ibOrderStatus;
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
}
