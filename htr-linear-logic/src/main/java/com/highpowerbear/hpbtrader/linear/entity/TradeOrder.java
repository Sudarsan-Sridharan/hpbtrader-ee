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
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TradeOrder)) {
            return false;
        }
        TradeOrder other = (TradeOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
