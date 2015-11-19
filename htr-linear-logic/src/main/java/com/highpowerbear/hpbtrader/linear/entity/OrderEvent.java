package com.highpowerbear.hpbtrader.linear.entity;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "lin_orderevent", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "orderStatus"}))
public class OrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="lin_orderevent", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_orderevent")
    private Long id;
    @Enumerated(EnumType.STRING)
    private LinEnums.IbOrderStatus status;
    private Double fillPrice;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @ManyToOne
    IbOrder ibOrder;

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

    public LinEnums.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LinEnums.IbOrderStatus ibOrderStatus) {
        this.status = ibOrderStatus;
    }

    public Double getFillPrice() {
        return fillPrice;
    }

    public void setFillPrice(Double fillPrice) {
        this.fillPrice = fillPrice;
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
}
