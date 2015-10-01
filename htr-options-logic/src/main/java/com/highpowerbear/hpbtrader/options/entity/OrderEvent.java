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
@Table(name = "opt_orderevent")
public class OrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="opt_orderevent")
    @Id
    @GeneratedValue(generator="opt_orderevent")
    private Long id;
    @Enumerated(EnumType.STRING)
    private OptEnums.OrderStatus orderStatus;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @ManyToOne
    OptionOrder optionOrder;
    
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

    public OptionOrder getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(OptionOrder optionOrder) {
        this.optionOrder = optionOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderEvent)) return false;

        OrderEvent that = (OrderEvent) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
