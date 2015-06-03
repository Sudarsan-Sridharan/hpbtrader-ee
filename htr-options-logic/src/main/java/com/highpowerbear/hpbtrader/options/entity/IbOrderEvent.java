package com.highpowerbear.hpbtrader.options.entity;

import com.highpowerbear.hpbtrader.options.common.OptEnums;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author rkolar
 */
@Entity
public class IbOrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="ibOrderEvent")
    @Id
    @GeneratedValue(generator="ibOrderEvent")
    private Long id;
    @Enumerated(EnumType.STRING)
    private OptEnums.OrderStatus orderStatus;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @ManyToOne
    IbOrder ibOrder;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OptEnums.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OptEnums.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public IbOrder getIbOrder() {
        return ibOrder;
    }

    public void setIbOrder(IbOrder ibOrder) {
        this.ibOrder = ibOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IbOrderEvent)) return false;

        IbOrderEvent that = (IbOrderEvent) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
