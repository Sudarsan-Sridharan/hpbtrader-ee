package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "lin_trade")
public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="lin_trade", table="sequence", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="lin_trade")
    private Long id;
    @ManyToOne
    private Strategy strategy;
    private Integer quantity;
    private Integer tradePosition = 0;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar dateInitOpen;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar dateClosed;
    private Double openPrice;
    private Double closePrice;
    private Double initialStop;
    private Double stopLoss;
    private Double profitTarget;
    private Double unrealizedPl = 0d;
    @XmlElement
    private Double realizedPl = 0d;
    @Enumerated(EnumType.STRING)
    private HtrEnums.TradeType tradeType;
    @Enumerated(EnumType.STRING)
    private HtrEnums.TradeStatus tradeStatus;
    @OneToMany(mappedBy = "trade", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy // by primary key
    private List<TradeOrder> tradeOrders = new ArrayList<>();
    
    @XmlElement
    public long getTimeInMillis() {
        return (dateClosed != null ? dateClosed.getTimeInMillis() : HtrUtil.getCalendar().getTimeInMillis());
    }
    
    public void addTradeOrder(IbOrder ibOrder) {
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setIbOrder(ibOrder);
        tradeOrder.setTrade(this);
        tradeOrder.setQuantity(ibOrder.isReversalOrder() ? ibOrder.getQuantity()/2 : ibOrder.getQuantity());
        tradeOrders.add(tradeOrder);
    }
    
    public Trade initOpen(IbOrder ibOrder) {
        this.strategy = ibOrder.getStrategy();
        this.dateInitOpen = ibOrder.getDateCreated();
        this.tradeStatus = HtrEnums.TradeStatus.INIT_OPEN;
        this.tradeType = (ibOrder.isBuyOrder() ? HtrEnums.TradeType.LONG : HtrEnums.TradeType.SHORT);
        this.quantity = strategy.getTradingQuantity();
        return this;
    }
    
    public void open(Double openPrice) {
        this.tradeStatus = HtrEnums.TradeStatus.OPEN;
        this.openPrice = openPrice;
        this.tradePosition = (isLong() ? quantity : -quantity);
    }
            
    public void initClose() {
        this.tradeStatus = HtrEnums.TradeStatus.INIT_CLOSE;
    }
    
    public void close(Calendar date, Double closePrice) {
        this.tradeStatus = HtrEnums.TradeStatus.CLOSED;
        this.closePrice = closePrice;
        this.dateClosed = date;
        this.realizedPl = (isLong() ? HtrUtil.round5((this.closePrice - this.openPrice) * quantity) : HtrUtil.round5((this.openPrice - this.closePrice) * quantity));
        if (HtrEnums.SecType.FUT.equals(this.strategy.getSeries().getSecType())) {
            this.realizedPl *= HtrEnums.FutureMultiplier.getMultiplierBySymbol(this.strategy.getSeries().getSymbol());
        }
        if (HtrEnums.SecType.OPT.equals(this.strategy.getSeries().getSecType())) {
            this.realizedPl *= (HtrEnums.MiniOption.isMiniOption(this.strategy.getSeries().getSymbol()) ? 10 : 100);
        }
        this.unrealizedPl = 0d;
    }

    public void errClose() {
        this.dateClosed = HtrUtil.getCalendar();
        this.tradeStatus = HtrEnums.TradeStatus.ERR_CLOSED;
    }

    public void cncClosed() {
        this.dateClosed = HtrUtil.getCalendar();
        this.tradeStatus = HtrEnums.TradeStatus.CNC_CLOSED;
    }

    public boolean isNew() {
        return (id == null);
    }
    
    public boolean isLong() {
        return HtrEnums.TradeType.LONG.equals(tradeType);
    }
    
    public boolean isShort() {
        return HtrEnums.TradeType.SHORT.equals(tradeType);
    }
    
    public boolean isInit() {
        return (HtrEnums.TradeStatus.INIT_OPEN.equals(tradeStatus) || HtrEnums.TradeStatus.INIT_CLOSE.equals(tradeStatus));
    }
    
    public boolean isOpen() {
        return (HtrEnums.TradeStatus.OPEN.equals(tradeStatus));
    }

    public TradeLog copyValues(TradeLog stl) {
        if (stl == null) {
            return null;
        }
        stl.setTradePosition(tradePosition);
        stl.setStopLoss(stopLoss);
        stl.setProfitTarget(profitTarget);
        stl.setUnrealizedPl(unrealizedPl);
        stl.setRealizedPl(realizedPl);
        stl.setTradeStatus(tradeStatus);
        return stl;
    }
    
    public Trade deepCopy(Trade otherTrade) {
        otherTrade.setId(id);
        otherTrade.setStrategy(strategy);
        otherTrade.setQuantity(quantity);
        otherTrade.setTradePosition(tradePosition);
        otherTrade.setDateInitOpen(dateInitOpen);
        otherTrade.setDateClosed(dateClosed);
        otherTrade.setOpenPrice(openPrice);
        otherTrade.setClosePrice(closePrice);
        otherTrade.setInitialStop(initialStop);
        otherTrade.setStopLoss(stopLoss);
        otherTrade.setProfitTarget(profitTarget);
        otherTrade.setUnrealizedPl(unrealizedPl);
        otherTrade.setRealizedPl(realizedPl);
        otherTrade.setTradeType(tradeType);
        otherTrade.setTradeStatus(tradeStatus);
        otherTrade.setTradeOrders(tradeOrders);
        return otherTrade;
    }
    
    public boolean valuesEqual(Trade otherTrade) {
        if (otherTrade == null) {
            return false;
        }
        if (    HtrUtil.equalsWithNulls(stopLoss, otherTrade.stopLoss) &&
                HtrUtil.equalsWithNulls(initialStop, otherTrade.initialStop) &&
                HtrUtil.equalsWithNulls(profitTarget, otherTrade.profitTarget) &&
                HtrUtil.equalsWithNulls(unrealizedPl, otherTrade.unrealizedPl) &&
                HtrUtil.equalsWithNulls(realizedPl, otherTrade.realizedPl) &&
                HtrUtil.equalsWithNulls(tradeType, otherTrade.tradeType) &&
                HtrUtil.equalsWithNulls(tradeStatus, otherTrade.tradeStatus)
           )
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        return !(id != null ? !id.equals(trade.id) : trade.id != null);

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

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTradePosition() {
        return tradePosition;
    }

    public void setTradePosition(Integer tradePosition) {
        this.tradePosition = tradePosition;
    }

    public Calendar getDateInitOpen() {
        return dateInitOpen;
    }

    public void setDateInitOpen(Calendar dateInitOpen) {
        this.dateInitOpen = dateInitOpen;
    }

    public Calendar getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Calendar dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public Double getInitialStop() {
        return initialStop;
    }

    public void setInitialStop(Double initialStop) {
        this.initialStop = initialStop;
    }

    public Double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(Double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public Double getProfitTarget() {
        return profitTarget;
    }

    public void setProfitTarget(Double profitTarget) {
        this.profitTarget = profitTarget;
    }

    public Double getUnrealizedPl() {
        return unrealizedPl;
    }

    public void setUnrealizedPl(Double unrealizedPl) {
        this.unrealizedPl = unrealizedPl;
    }

    public Double getRealizedPl() {
        return realizedPl;
    }

    public void setRealizedPl(Double realizedPl) {
        this.realizedPl = realizedPl;
    }

    public HtrEnums.TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(HtrEnums.TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public HtrEnums.TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(HtrEnums.TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public List<TradeOrder> getTradeOrders() {
        return tradeOrders;
    }

    public void setTradeOrders(List<TradeOrder> tradeOrders) {
        this.tradeOrders = tradeOrders;
    }
}
