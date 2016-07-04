package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;

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
@Table(name = "orderevent", schema = "hpbtrader", catalog = "hpbtrader")
public class IbOrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableGenerator(name="orderevent", table="sequence", schema = "hpbtrader", catalog = "hpbtrader", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="orderevent")
    private Long id;
    @Enumerated(EnumType.STRING)
    private HtrEnums.IbOrderStatus status;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar eventDate;
    @ManyToOne
    private IbOrder ibOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IbOrderEvent that = (IbOrderEvent) o;

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

    public HtrEnums.IbOrderStatus getStatus() {
        return status;
    }

    public void setStatus(HtrEnums.IbOrderStatus ibOrderStatus) {
        this.status = ibOrderStatus;
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
