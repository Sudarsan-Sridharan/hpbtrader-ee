package com.highpowerbear.hpbtrader.shared.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rkolar
 */
public class HtrEnums {
    public enum Action {
        BUY,
        SELL,
        SSHORT
    }

    public enum OrderStatus {
        PENDINGSUBMIT("PendingSubmit", Css.DODGER_BLUE_BG),
        PENDINGCANCEL("PendingCancel", Css.ORANGE_BG),
        PRESUBMITTED("PreSubmitted", Css.DODGER_BLUE_BG),
        SUBMITTED("Submitted", Css.BLUE_BG),
        CANCELLED("Cancelled", Css.RED_BG),
        FILLED("Filled", Css.GREEN_BG),
        INACTIVE("Inactive", Css.BROWN_BG);

        private String displayName;
        private Css css;

        OrderStatus(String displayName, Css css) {
            this.displayName = displayName;
            this.css = css;
        }

        public String getDisplayName() {
            return displayName;
        }
        public String getCssClass() {
            return css.getCssClass();
        }
    }

    public enum OrderType {
        MKT,
        MKTCLS,
        LMT,
        LMTCLS,
        PEGMKT,
        SCALE,
        STP,
        STPLMT,
        TRAIL,
        REL,
        VWAP,
        TRAILLIMIT
    }

    public enum Tif {
        DAY,
        GTC,
        IOC,
        GTD
    }

    public enum OptionType {
        CALL("C"),
        PUT("P");

        private String code;

        OptionType(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    public enum Multiplier {
        M_100("100");

        String value;

        Multiplier(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Interval {
        MIN5(300000),
        MIN60(3600000);
        
        private long millis;
        Interval(long millis) {
            this.millis = millis;
        }
        public long getMillis() {
            return millis;
        }
    }
    
    public enum SecType {
        STK("STK", 100, HtrDefinitions.IB_BAR_TYPE_TRADES),
        OPT("OPT", 1, HtrDefinitions.IB_BAR_TYPE_TRADES),
        FUT("FUT", 1, HtrDefinitions.IB_BAR_TYPE_TRADES),
        FOP("FOP", 1, HtrDefinitions.IB_BAR_TYPE_TRADES),
        CASH("CSH", 100000, HtrDefinitions.IB_BAR_TYPE_ASK);

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
    
    public enum IbOrderStatus {
        NEW("new", Css.MAGENTA_BG),
        NEW_RETRY("newRetry", Css.MAGENTA_BG),
        SUBMIT_REQ("submitReq", Css.DODGER_BLUE_BG),
        SUBMITTED("submitted", Css.BLUE_BG),
        FILLED("filled", Css.GREEN_BG),
        CANCEL_REQ("cancelReq", Css.MAGENTA_BG),
        CANCELED("canceled", Css.RED_BG),
        UNKNOWN("unknown", Css.BROWN_BG);
        
        private String displayName;
        private Css css;
        
        IbOrderStatus(String displayName, Css css) {
            this.displayName = displayName;
            this.css = css;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getCssClass() {
            return css.getCssClass();
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
        IB(Css.ORANGE_BG),
        SIM(null),
        BTEST(null);

        private Css css;

        StrategyMode(Css css) {
            this.css = css;
        }
        public String getCssClass() {
            return (css != null ? css.getCssClass() : "");
        }
    }
    
    public static final List<StrategyMode> selectableStrategyModes = new ArrayList<>();
    static {
        selectableStrategyModes.add(StrategyMode.IB);
        selectableStrategyModes.add(StrategyMode.SIM);
    }
    
    public enum TradeType {
        LONG("L", Css.BLUE_BG),
        SHORT("S", Css.BROWN_BG);

        private String displayName;
        private Css css;

        TradeType(String displayName, Css css) {
            this.displayName = displayName;
            this.css = css;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getCssClass() {
            return css.getCssClass();
        }
    }
    
    public enum TradeStatus {
        INIT_OPEN("initOpen", Css.BLUE_BG),
        OPEN("open", Css.GREEN_BG),
        INIT_CLOSE("initClose", Css.ORANGE_BG),
        CLOSED("closed", Css.BROWN_BG),
        CNC_CLOSED("cncClosed", Css.RED_BG),
        ERR_CLOSED("errClosed", Css.RED_BG);
        
        private String displayName;
        private Css css;
        
        TradeStatus(String displayName, Css css) {
            this.displayName = displayName;
            this.css = css;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getCssClass() {
            return css.getCssClass();
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
            return (futSymbol != null ? Arrays.asList(FutureMultiplier.values()).stream().filter(fm -> futSymbol.startsWith(fm.toString())).findAny().get().multiplier : 1);
        }
    }
    
    public enum MiniOption {
        AMZN7, AAPL7, GOOG7,  GLD7, SPY7;
        public static boolean isMiniOption(String optionSymbol) {
            return optionSymbol != null && Arrays.asList(MiniOption.values()).stream().filter(mo -> optionSymbol.startsWith(mo.name())).findAny().isPresent();
        }
    }

    public enum DataChangeEvent {
        BAR_UPDATE,
        STRATEGY_UPDATE
    }

    public enum RealtimeStatus {
        UPTICK(Css.LIME),
        DOWNTICK(Css.ORANGE),
        UNCHANGED(Css.YELLOW),
        POSITIVE(Css.BLUE),
        NEGATIVE(Css.RED);

        private Css css;

        RealtimeStatus(Css css) {
            this.css = css;
        }

        public String getCssClass() {
            return css.getCssClass();
        }
    }

    public enum Exchange {
        SMART,
        IDEALPRO,
        GLOBEX,
        ECBOT,
        NYMEX
    }

    public enum ValueStatus {
        UPTICK,
        DOWNTICK,
        UNCHANGED
    }

    public enum Css {
        MAGENTA("htr-magenta"),
        MAGENTA_BG("htr-magenta-bg"),
        BLUE("htr-blue"),
        BLUE_BG("htr-blue-bg"),
        DARK_BLUE("htr-dark-blue"),
        DARK_BLUE_BG("htr-dark-blue-bg"),
        GREEN("htr-green"),
        GREEN_BG("htr-green-bg"),
        DARK_GREEN("htr-dark-green"),
        DARK_GREEN_BG("htr-dark-green-bg"),
        RED("htr-red"),
        RED_BG("htr-red-bg"),
        DARK_RED("htr-dark-red"),
        DARK_RED_BG("htr-dark-red-bg"),
        ORANGE("htr-orange"),
        ORANGE_BG("htr-orange-bg"),
        DARK_ORANGE("htr-dark-orange"),
        DARK_ORANGE_BG("htr-dark-orange-bg"),
        DARK_CYAN("htr-dark-cyan"),
        DARK_CYAN_BG("htr-dark-cyan-bg"),
        LIME("htr-lime"),
        LIME_BG("htr-lime-bg"),
        YELLOW("htr-yellow"),
        YELLOW_BG("htr-yellow-bg"),
        BROWN("htr-brown"),
        BROWN_BG("htr-brown-bg"),
        DODGER_BLUE("htr-dodger-blue"),
        DODGER_BLUE_BG("htr-dodger-blue-bg");

        private String cssClass;

        Css(String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }
    }
}
