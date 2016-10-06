package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
@Table(name = "iborder", schema = "hpbtrader", catalog = "hpbtrader")
public class IbOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="iborder", table="sequence", schema = "hpbtrader", catalog = "hpbtrader", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="iborder")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdDate;
    private Integer ibPermId;
    private Integer ibOrderId;
    @ManyToOne
    @XmlTransient
    private Strategy strategy;
    @ManyToOne
    @XmlTransient
    private IbAccount ibAccount;
    @Enumerated(EnumType.STRING)
    private HtrEnums.StrategyMode strategyMode;
    private String triggerDesc;
    @Enumerated(EnumType.STRING)
    private HtrEnums.SubmitType submitType;
    @Enumerated(EnumType.STRING)
    private HtrEnums.OrderAction orderAction;
    private Integer quantity;
    private String symbol;
    @Enumerated(EnumType.STRING)
    private HtrEnums.OrderType orderType;
    private Double orderPrice;
    private Double fillPrice;
    @Enumerated(EnumType.STRING)
    private HtrEnums.IbOrderStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar statusDate;
    @OneToMany(mappedBy = "ibOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate DESC, id DESC")
    private List<IbOrderEvent> ibOrderEvents = new ArrayList<>();
    @Transient
    private Integer heartbeatCount;

    @XmlElement
    public Integer getStrategyId() {
        return strategy.getId();
    }

    @XmlElement
    public String getIbAccountId() {
        return ibAccount.getAccountId();
    }

    @XmlElement
    public Instrument getInstrument() {
        return strategy.getTradeInstrument();
    }

    public void addEvent(HtrEnums.IbOrderStatus status, Calendar date) {
        this.status = status;
        this.statusDate = date;
        IbOrderEvent event = new IbOrderEvent();
        event.setIbOrder(this);
        event.setEventDate(this.statusDate);
        event.setStatus(this.status);
        if (HtrEnums.IbOrderStatus.NEW.equals(this.status)) {
            this.setCreatedDate(this.statusDate);
        }
        ibOrderEvents.add(event);
    }

    public Calendar getEventDate(HtrEnums.IbOrderStatus ibOrderStatus) {
        IbOrderEvent ibOrderEvent = ibOrderEvents.stream().filter(oe -> ibOrderStatus.equals(oe.getStatus())).findAny().orElse(null);
        return (ibOrderEvent != null ? ibOrderEvent.getEventDate() : null);
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
        return symbol + ", " +  strategy.getStrategyType().name().toLowerCase() + ", " + orderAction.name() + ", " + strategy.getInputSeriesAliases();
    }

    public com.ib.client.Order createOrder() {
        com.ib.client.Order ord = new com.ib.client.Order();
        ord.m_action = (this.isBuyOrder() ? HtrEnums.Action.BUY.name() : HtrEnums.Action.SELL.name());
        ord.m_orderType = getOrderType().name();
        if (this.orderPrice != null) {
            if (HtrEnums.OrderType.LMT.equals(this.orderType)) {
                ord.m_lmtPrice = this.orderPrice;
            } else if (HtrEnums.OrderType.STP.equals(this.orderType)) {
                ord.m_auxPrice = this.orderPrice;
            }
        }
        ord.m_totalQuantity = this.quantity;
        ord.m_tif = HtrEnums.Tif.GTC.name();
        return ord;
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

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar dateCreated) {
        this.createdDate = dateCreated;
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

    public IbAccount getIbAccount() {
        return ibAccount;
    }

    public void setIbAccount(IbAccount ibAccount) {
        this.ibAccount = ibAccount;
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

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public Calendar getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Calendar statusDate) {
        this.statusDate = statusDate;
    }

    public List<IbOrderEvent> getIbOrderEvents() {
        return ibOrderEvents;
    }

    public void setIbOrderEvents(List<IbOrderEvent> ibOrderEvents) {
        this.ibOrderEvents = ibOrderEvents;
    }

    public Integer getHeartbeatCount() {
        return heartbeatCount;
    }

    public void setHeartbeatCount(Integer heartbeatCount) {
        this.heartbeatCount = heartbeatCount;
    }
}
