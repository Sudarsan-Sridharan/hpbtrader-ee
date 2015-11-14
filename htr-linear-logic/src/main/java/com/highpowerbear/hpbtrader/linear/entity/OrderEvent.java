package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@Entity
@Table(name = "lin_orderevent", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "orderStatus"}))
public class OrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="lin_orderevent", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_orderevent")
    private Long id;
    @Enumerated(EnumType.STRING)
    private LinEnums.OrderStatus orderStatus;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @ManyToOne
    Order order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderEvent that = (OrderEvent) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

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

    public LinEnums.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(LinEnums.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
