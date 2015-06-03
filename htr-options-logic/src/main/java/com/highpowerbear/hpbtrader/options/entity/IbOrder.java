package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptUtil;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Robert
 */
@Entity
public class IbOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="ibOrder")
    @Id
    @GeneratedValue(generator="ibOrder")
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;
    private Integer ibPermId;
    private Integer ibOrderId;
    @Enumerated(EnumType.STRING)
    private IbApiEnums.Action action;
    private Integer quantity;
    private String optionSymbol;
    private IbApiEnums.OrderType orderType;
    private Double lmtPrice;
    private Double fillPrice;
    @Enumerated(EnumType.STRING)
    private OptEnums.OrderStatus orderStatus;
    @ManyToOne
    private InputSignal inputSignal;
    @ManyToOne
    private Trade trade;
    @OneToMany(mappedBy = "ibOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("eventDate ASC, id ASC")
    private List<IbOrderEvent> events = new ArrayList<>();
    
    public void addEvent(OptEnums.OrderStatus status) {
        this.orderStatus = status;
        IbOrderEvent event = new IbOrderEvent();
        event.setIbOrder(this);
        event.setEventDate(OptUtil.getNowCalendar());
        event.setOrderStatus(orderStatus);
        events.add(event);
        if (OptEnums.OrderStatus.NEW.equals(event.getOrderStatus())) {
            this.setDateCreated(event.getEventDate());
        }
    }
    
    public Calendar getEventDate(OptEnums.OrderStatus orderStatus) {
        Calendar eventDate = null;
        for (IbOrderEvent oe : events) {
            if (orderStatus.equals(oe.getOrderStatus())) {
                eventDate = oe.getEventDate();
                break;
            }
        }
        return eventDate;
    }
    
    public com.ib.client.Order createApiOrder() {
        com.ib.client.Order order = new com.ib.client.Order();
        order.m_action = action.getName();
        order.m_orderType = orderType.getName();
        order.m_lmtPrice = lmtPrice;
        order.m_totalQuantity = quantity;
        return order;
    }
    
    public com.ib.client.Contract createApiOptionContract() {
        com.ib.client.Contract contract = new com.ib.client.Contract();
        contract.m_localSymbol = optionSymbol;
        contract.m_secType = IbApiEnums.SecType.OPT.getName();
        contract.m_exchange = IbApiEnums.Exchange.SMART.getName();
        contract.m_currency = IbApiEnums.Currency.USD.getName();
        return contract;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
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

    public IbApiEnums.Action getAction() {
        return action;
    }

    public void setAction(IbApiEnums.Action action) {
        this.action = action;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOptionSymbol() {
        return optionSymbol;
    }

    public void setOptionSymbol(String optionSymbol) {
        this.optionSymbol = optionSymbol;
    }

    public IbApiEnums.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(IbApiEnums.OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getLmtPrice() {
        return lmtPrice;
    }

    public void setLmtPrice(Double lmtPrice) {
        this.lmtPrice = lmtPrice;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
    }

    public OptEnums.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OptEnums.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public InputSignal getInputSignal() {
        return inputSignal;
    }

    public void setInputSignal(InputSignal inputSignal) {
        this.inputSignal = inputSignal;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public List<IbOrderEvent> getEvents() {
        return events;
    }

    public void setEvents(List<IbOrderEvent> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IbOrder)) return false;

        IbOrder ibOrder = (IbOrder) o;

        return !(id != null ? !id.equals(ibOrder.id) : ibOrder.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String print() {
        return action.getName() + ", " + quantity + ", " + optionSymbol;
    }
}
