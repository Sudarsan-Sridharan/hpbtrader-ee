package com.highpowerbear.hpbtrader.shared.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 *
 * @author rkolar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "tradeorder")
public class TradeOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="tradeorder", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="tradeorder")
    private Long id;
    private Integer quantity;
    @ManyToOne
    private IbOrder ibOrder;
    @ManyToOne
    private Trade trade;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeOrder that = (TradeOrder) o;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public IbOrder getIbOrder() {
        return ibOrder;
    }

    public void setIbOrder(IbOrder ibOrder) {
        this.ibOrder = ibOrder;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }
}
