package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.IbApiEnums;

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
    private HtrEnums.StrategyMode strategyMode;
    private String triggerDesc;
    private HtrEnums.SubmitType submitType;
    @Enumerated(EnumType.STRING)
    private HtrEnums.OrderAction orderAction;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private HtrEnums.OrderType orderType;
    private Double limitPrice;
    private Double stopPrice;
    private Double fillPrice;
    @Enumerated(EnumType.STRING)
    private HtrEnums.IbOrderStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;
    @OneToMany(mappedBy = "ibOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate ASC")
    private List<OrderEvent> events = new ArrayList<>();

    public void addEvent(HtrEnums.IbOrderStatus status, Calendar date, Double fillPrice) {
        this.status = status;
        OrderEvent event = new OrderEvent();
        event.setIbOrder(this);
        event.setEventDate(date);
        event.setStatus(status);
        event.setFillPrice(fillPrice);
        events.add(event);
        if (HtrEnums.IbOrderStatus.NEW.equals(event.getStatus())) {
            this.setDateCreated(event.getEventDate());
        }
    }

    public Calendar getEventDate(HtrEnums.IbOrderStatus ibOrderStatus) {
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
        return orderAction != null && (HtrEnums.OrderAction.BTO.equals(orderAction) || HtrEnums.OrderAction.STO.equals(orderAction));
    }

    public boolean isClosingOrder() {
        return orderAction != null && (HtrEnums.OrderAction.BTC.equals(orderAction) || HtrEnums.OrderAction.STC.equals(orderAction) || HtrEnums.OrderAction.BREV.equals(orderAction) || HtrEnums.OrderAction.SREV.equals(orderAction));
    }

    public boolean isBuyOrder() {
        return orderAction != null && (HtrEnums.OrderAction.BTO.equals(orderAction) || HtrEnums.OrderAction.BTC.equals(orderAction) || HtrEnums.OrderAction.BREV.equals(orderAction));
    }

    public boolean isSellOrder() {
        return orderAction != null && (HtrEnums.OrderAction.STO.equals(orderAction) || HtrEnums.OrderAction.STC.equals(orderAction) || HtrEnums.OrderAction.SREV.equals(orderAction));
    }

    public boolean isReversalOrder() {
        if (orderAction == null) {
            return false;
        }
        return (HtrEnums.OrderAction.BREV.equals(orderAction) || HtrEnums.OrderAction.SREV.equals(orderAction));
    }

    public String getDescription() {
        Series series = strategy.getSeries();
        return series.getSymbol() + ", " + series.getInterval().getDisplayName() + ", " +  strategy.getStrategyType().getDisplayName() + ": " + orderAction.toString();
    }

    public com.ib.client.Order createIbOrder() {
        com.ib.client.Order ibOrder = new com.ib.client.Order();
        ibOrder.m_action = (this.isBuyOrder() ? IbApiEnums.Action.BUY.getName() : IbApiEnums.Action.SELL.getName());
        ibOrder.m_orderType = HtrEnums.OrderType.getIbOrderType(this.getOrderType());
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

    public HtrEnums.StrategyMode getStrategyMode() {
        return strategyMode;
    }

    public void setStrategyMode(HtrEnums.StrategyMode strategyMode) {
        this.strategyMode = strategyMode;
    }

    public HtrEnums.SubmitType getSubmitType() {
        return submitType;
    }

    public void setSubmitType(HtrEnums.SubmitType submitType) {
        this.submitType = submitType;
    }

    public HtrEnums.OrderAction getOrderAction() {
        return orderAction;
    }

    public void setOrderAction(HtrEnums.OrderAction orderAction) {
        this.orderAction = orderAction;
    }

    public String getTriggerDesc() {
        return triggerDesc;
    }

    public void setTriggerDesc(String triggerDesc) {
        this.triggerDesc = triggerDesc;
    }

    public HtrEnums.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(HtrEnums.OrderType orderType) {
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

    public HtrEnums.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(HtrEnums.IbOrderStatus ibOrderStatus) {
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
