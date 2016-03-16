package com.highpowerbear.hpbtrader.strategy.options.model;

/**
 *
 * @author rkolar
 */
public class ReadinessStatus  implements Comparable<ReadinessStatus>  {
    private String underlying;
    private boolean ready = false;
    private String description;
    private MarketData activeCallMarketDataSnapshot;
    private MarketData activePutMarketDataSnapshot;

    public ReadinessStatus(String underlying) {
        this.underlying = underlying;
    }
    
    @Override
    public int compareTo(ReadinessStatus other) {
        return (this.underlying.compareTo(other.underlying));
    }

    public String getUnderlying() {
        return underlying;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MarketData getActiveCallMarketDataSnapshot() {
        return activeCallMarketDataSnapshot;
    }

    public void setActiveCallMarketDataSnapshot(MarketData activeCallMarketDataSnapshot) {
        this.activeCallMarketDataSnapshot = activeCallMarketDataSnapshot;
    }

    public MarketData getActivePutMarketDataSnapshot() {
        return activePutMarketDataSnapshot;
    }

    public void setActivePutMarketDataSnapshot(MarketData activePutMarketDataSnapshot) {
        this.activePutMarketDataSnapshot = activePutMarketDataSnapshot;
    }
}
