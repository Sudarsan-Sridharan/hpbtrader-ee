package com.highpowerbear.hpbtrader.shared.common;

import com.highpowerbear.hpbtrader.shared.ibclient.IbApiEnums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rkolar
 */
public class HtrEnums {
    public enum Interval {
        INT_5_MIN(300000, "5"),
        INT_60_MIN(3600000, "60");
        
        private long millis;
        private String displayName;
        Interval(long millis, String displayName) {
            this.millis = millis;
            this.displayName = displayName;
        }
        public long getMillis() {
            return millis;
        }
        public String getDisplayName() {
            return displayName;
        }
        public static Interval getByDisplayName(String displayName) {
            return ("5".equals(displayName) ? INT_5_MIN : ("60".equals(displayName) ? INT_60_MIN : null));
        }
    }
    
    public enum SecType {
        STK("STK", 100, HtrConstants.IB_BAR_TYPE_TRADES),
        OPT("OPT", 1, HtrConstants.IB_BAR_TYPE_TRADES),
        FUT("FUT", 1, HtrConstants.IB_BAR_TYPE_TRADES),
        FOP("FOP", 1, HtrConstants.IB_BAR_TYPE_TRADES),
        CASH("CSH", 100000, HtrConstants.IB_BAR_TYPE_ASK);

        private String displayName;
        private int defaultTradingQuantity;
        private String ibBarType;

        SecType(String displayName, int defaultTradingQuantity, String ibBarType) {
            this.displayName = displayName;
            this.defaultTradingQuantity = defaultTradingQuantity;
            this.ibBarType = ibBarType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDefaultTradingQuantity() {
            return defaultTradingQuantity;
        }

        public String getIbBarType() {
            return ibBarType;
        }

        public static String getIbSecType(SecType secType) {
            String ibSecType = null;
            switch (secType) {
                case STK: ibSecType = IbApiEnums.SecType.STK.getName(); break;
                case OPT: ibSecType = IbApiEnums.SecType.OPT.getName(); break;
                case FUT: ibSecType = IbApiEnums.SecType.FUT.getName(); break;
                case FOP: ibSecType = IbApiEnums.SecType.FOP.getName(); break;
                case CASH: ibSecType = IbApiEnums.SecType.CASH.getName(); break;
            }
            return ibSecType;
        }
    }
    
    public enum Currency {
        USD,
        EUR,
        AUD,
        GBP,
        CHF,
        CAD,
        JPY
    }
    
    public enum OrderAction {
        BTO,
        BTC,
        STO,
        STC,
        BREV,
        SREV
    }
    
    public enum OrderType {
        MKT,
        LMT,
        STP;
        
        public static String getIbOrderType(OrderType orderType) {
            String ibOrderType = null;
            switch (orderType) {
                case MKT: ibOrderType = IbApiEnums.OrderType.MKT.getName(); break;
                case LMT: ibOrderType = IbApiEnums.OrderType.LMT.getName(); break;
                case STP: ibOrderType = IbApiEnums.OrderType.STP.getName(); break;
            }
            return ibOrderType;
        }
    }
    
    public enum IbOrderStatus {
        NEW("new", "col-magenta-bck"),
        NEW_RETRY("newRetry", "col-magenta-bck"),
        SUBMIT_REQ("submitReq", "col-dodgerblue-bck"),
        SUBMITTED("submitted", "col-blue-bck"),
        FILLED("filled", "col-green-bck"),
        CANCEL_REQ("cancelReq", "col-magenta-bck"),
        CANCELED("canceled", "col-red-bck"),
        UNKNOWN("unknown", "col-brown-bck");
        
        private String displayName;
        private String colorClass;
        
        IbOrderStatus(String displayName, String colorClass) {
            this.displayName = displayName;
            this.colorClass = colorClass;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getColorClass() {
            return colorClass;
        }
    }
    
    public enum SubmitType {
        AUTO("auto"),
        MANUAL("manual");
        
        private String displayName;
        
        SubmitType(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum StrategyType {
        LUXOR("luxor", "20, 50, 1.3d, 2.2d, 4, 6"), // emaShort, emaLong, stopPct, targetPct, startHourEst, durationHours
        MACD_CROSS("macdCross", "50, 50"), // stochOversold, stochOverbougth
        TEST("test", "1.3d, 2.2d"); // stopPct, targetPct
        
        private String displayName;
        private String defaultParams;

        StrategyType(String displayName, String defaultParams) {
            this.displayName = displayName;
            this.defaultParams = defaultParams;
        }
        public String getDisplayName() {
            return displayName;
        }

        public String getDefaultParams() {
            return defaultParams;
        }
    }
    
    public enum StrategyMode {
        IB("col-orange-bck"),
        SIM(""),
        BTEST("");

        private String colorClass;

        StrategyMode(String colorClass) {
            this.colorClass = colorClass;
        }
        public String getColorClass() {
            return colorClass;
        }
    }
    
    public static final List<StrategyMode> selectableStrategyModes = new ArrayList<>();
    static {
        selectableStrategyModes.add(StrategyMode.IB);
        selectableStrategyModes.add(StrategyMode.SIM);
    }
    
    public enum TradeType {
        LONG("L", "col-blue-bck"),
        SHORT("S", "col-brown-bck");

        private String displayName;
        private String colorClass;

        TradeType(String displayName, String colorClass) {
            this.displayName = displayName;
            this.colorClass = colorClass;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getColorClass() {
            return colorClass;
        }
    }
    
    public enum TradeStatus {
        INIT_OPEN("initOpen", "col-blue-bck"),
        OPEN("open", "col-green-bck"),
        INIT_CLOSE("initClose", "col-orange-bck"),
        CLOSED("closed", "col-brown-bck"),
        CNC_CLOSED("cncClosed", "col-red-bck"),
        ERR_CLOSED("errClosed", "col-red-bck");
        
        private String displayName;
        private String colorClass;
        
        TradeStatus(String displayName, String colorClass) {
            this.displayName = displayName;
            this.colorClass = colorClass;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getColorClass() {
            return colorClass;
        }
    }
    
    public static final List<Integer> tradingQuantities = new ArrayList<>();
    static {
        tradingQuantities.add(1);
        tradingQuantities.add(10);
        tradingQuantities.add(100);
        tradingQuantities.add(1000);
        tradingQuantities.add(10000);
        tradingQuantities.add(100000);
    }
    
    public enum FutureMultiplier {
        ES(50),
        NQ(20),
        YM(5),
        ZB(1000),
        GC(100),
        CL(1000);

        private Integer multiplier;
        
        FutureMultiplier(Integer multiplier) {
            this.multiplier = multiplier;
        }
        public static Integer getMultiplierBySymbol(String futSymbol) {
            Integer mult = 1;
            if (futSymbol != null) {
                for (FutureMultiplier fm : FutureMultiplier.values()) {
                    if (futSymbol.startsWith(fm.toString())) {
                        mult = fm.multiplier;
                        break;
                    }
                }
            }
            return mult;
        }
    }
    
    public enum MiniOption {
        AMZN7, AAPL7, GOOG7,  GLD7, SPY7;
        public static boolean isMiniOption(String optionSymbol) {
            boolean isMiniOption = false;
            if (optionSymbol != null) {
                for (MiniOption mo : MiniOption.values()) {
                    if (optionSymbol.startsWith(mo.toString())) {
                        isMiniOption = true;
                        break;
                    }
                }
            }
            return isMiniOption;
        }
    }

    public enum DataChangeEvent {
        BAR_UPDATE,
        STRATEGY_UPDATE
    }

    public enum RealtimeStatus {
        UPTICK("col-lime"),
        DOWNTICK("col-orange"),
        UNCHANGED("col-yellow"),
        POSITIVE("col-blue-bck"),
        NEGATIVE("col-red-bck");

        private String colorClass;

        RealtimeStatus(String colorClass) {
            this.colorClass = colorClass;
        }

        public String getColorClass() {
            return colorClass;
        }
    }

    public enum Exchange {
        SMART("smart"),
        IDEALPRO("idealpro"),
        GLOBEX("globex"),
        ECBOT("ecbot"),
        NYMEX("nymex");

        private String displayName;

        Exchange(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
    }
}
