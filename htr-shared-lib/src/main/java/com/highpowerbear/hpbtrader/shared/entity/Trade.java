package com.highpowerbear.hpbtrader.shared.entity;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author robertk
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "trade", schema = "hpbtrader", catalog = "hpbtrader")
public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableGenerator(name="trade", table="sequence", schema = "hpbtrader", catalog = "hpbtrader", pkColumnName="seq_name", valueColumnName="seq_count")
    @Id
    @GeneratedValue(generator="trade")
    private Long id;
    @ManyToOne
    @XmlTransient
    private Strategy strategy;
    private Integer quantity;
    private Integer tradePosition = 0;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar initOpenDate;
    @Temporal(value=TemporalType.TIMESTAMP)
    private Calendar closeDate;
    private Double openPrice;
    private Double closePrice;
    private Double initialStop;
    private Double stopLoss;
    private Double profitTarget;
    private Double unrealizedPl = 0d;
    private Double realizedPl = 0d;
    @Enumerated(EnumType.STRING)
    private HtrEnums.TradeType tradeType;
    @Enumerated(EnumType.STRING)
    private HtrEnums.TradeStatus tradeStatus;
    @OneToMany(mappedBy = "trade", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    private List<TradeIbOrder> tradeIbOrders = new ArrayList<>();

    @XmlElement
    public Integer getStrategyId() {
        return strategy.getId();
    }
    
    public void addTradeOrder(IbOrder ibOrder) {
        TradeIbOrder tradeIbOrder = new TradeIbOrder();
        tradeIbOrder.setIbOrder(ibOrder);
        tradeIbOrder.setTrade(this);
        tradeIbOrder.setQuantity(ibOrder.isReversalOrder() ? ibOrder.getQuantity()/2 : ibOrder.getQuantity());
        tradeIbOrders.add(tradeIbOrder);
    }
    
    public Trade initOpen(IbOrder ibOrder, Double initialStop, Double profitTarget) {
        this.strategy = ibOrder.getStrategy();
        this.initOpenDate = ibOrder.getCreatedDate();
        this.tradeStatus = HtrEnums.TradeStatus.INIT_OPEN;
        this.tradeType = (ibOrder.isBuyOrder() ? HtrEnums.TradeType.LONG : HtrEnums.TradeType.SHORT);
        this.quantity = strategy.getTradingQuantity();
        this.initialStop = initialStop;
        this.profitTarget = profitTarget;
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
        this.closeDate = date;
        this.realizedPl = (isLong() ? HtrUtil.round5((this.closePrice - this.openPrice) * quantity) : HtrUtil.round5((this.openPrice - this.closePrice) * quantity));
        if (HtrEnums.SecType.FUT.equals(this.strategy.getTradeInstrument().getSecType())) {
            this.realizedPl *= HtrEnums.FutureMultiplier.getMultiplierBySymbol(this.strategy.getTradeInstrument().getSymbol());
        }
        if (HtrEnums.SecType.OPT.equals(this.strategy.getTradeInstrument().getSecType())) {
            this.realizedPl *= (HtrEnums.MiniOption.isMiniOption(this.strategy.getTradeInstrument().getSymbol()) ? 10 : 100);
        }
        this.unrealizedPl = 0d;
    }

    public void errClose() {
        this.closeDate = HtrUtil.getCalendar();
        this.tradeStatus = HtrEnums.TradeStatus.ERR_CLOSED;
    }

    public void cncClose() {
        this.closeDate = HtrUtil.getCalendar();
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
    
    public Trade deepCopyTo(Trade other) {
        other.setId(id);
        other.setStrategy(strategy);
        other.setQuantity(quantity);
        other.setTradePosition(tradePosition);
        other.setInitOpenDate(initOpenDate);
        other.setCloseDate(closeDate);
        other.setOpenPrice(openPrice);
        other.setClosePrice(closePrice);
        other.setInitialStop(initialStop);
        other.setStopLoss(stopLoss);
        other.setProfitTarget(profitTarget);
        other.setUnrealizedPl(unrealizedPl);
        other.setRealizedPl(realizedPl);
        other.setTradeType(tradeType);
        other.setTradeStatus(tradeStatus);
        other.setTradeIbOrders(tradeIbOrders);
        return other;
    }
    
    public boolean valuesEqual(Trade other) {
        if (other == null) {
            return false;
        }
        if (    HtrUtil.equalsWithNulls(stopLoss, other.stopLoss) &&
                HtrUtil.equalsWithNulls(initialStop, other.initialStop) &&
                HtrUtil.equalsWithNulls(profitTarget, other.profitTarget) &&
                HtrUtil.equalsWithNulls(unrealizedPl, other.unrealizedPl) &&
                HtrUtil.equalsWithNulls(realizedPl, other.realizedPl) &&
                HtrUtil.equalsWithNulls(tradeType, other.tradeType) &&
                HtrUtil.equalsWithNulls(tradeStatus, other.tradeStatus)
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

    public Calendar getInitOpenDate() {
        return initOpenDate;
    }

    public void setInitOpenDate(Calendar dateInitOpen) {
        this.initOpenDate = dateInitOpen;
    }

    public Calendar getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Calendar dateClosed) {
        this.closeDate = dateClosed;
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

    public List<TradeIbOrder> getTradeIbOrders() {
        return tradeIbOrders;
    }

    public void setTradeIbOrders(List<TradeIbOrder> tradeIbOrders) {
        this.tradeIbOrders = tradeIbOrders;
    }
}
