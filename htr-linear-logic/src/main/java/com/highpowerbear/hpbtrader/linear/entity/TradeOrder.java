package com.highpowerbear.hpbtrader.linear.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author rkolar
 */
@Entity
@Table(name = "lin_tradeorder")
public class TradeOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="lin_tradeorder", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_tradeorder")
    private Long id;
    private Integer quantity;
    @ManyToOne
    private Order order;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }
}
