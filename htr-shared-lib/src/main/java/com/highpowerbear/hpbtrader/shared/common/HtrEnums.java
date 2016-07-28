package com.highpowerbear.hpbtrader.shared.common;

import java.util.Arrays;

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

    public enum BarType {
        MIN_5(300000),
        MIN_60(3600000);
        
        private long millis;
        BarType(long millis) {
            this.millis = millis;
        }
        public long getMillis() {
            return millis;
        }
    }
    
    public enum SecType {
        STK(HtrDefinitions.IB_TRADES_LITERAL),
        OPT(HtrDefinitions.IB_TRADES_LITERAL),
        FUT(HtrDefinitions.IB_TRADES_LITERAL),
        FOP(HtrDefinitions.IB_TRADES_LITERAL),
        CASH(HtrDefinitions.IB_ASK_LITERAL);

        private String ibWhatToShow;

        SecType(String ibWhatToShow) {
            this.ibWhatToShow = ibWhatToShow;
        }

        public String getIbWhatToShow() {
            return ibWhatToShow;
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
        NEW("new", DisplayColor.MAGENTA),
        NEW_RETRY("newRetry", DisplayColor.MAGENTA),
        SUBMIT_REQ("submitReq", DisplayColor.DODGER_BLUE),
        PRESUBMITTED ("preSubmitted", DisplayColor.BLUE),
        SUBMITTED("submitted", DisplayColor.BLUE),
        FILLED("filled", DisplayColor.GREEN),
        CANCEL_REQ("cancelReq", DisplayColor.MAGENTA),
        CANCELLED("cancelled", DisplayColor.RED),
        UNKNOWN("unknown", DisplayColor.BROWN);

        private String displayText;
        private DisplayColor displayColor;
        
        IbOrderStatus(String displayText, DisplayColor displayColor) {
            this.displayText = displayText;
            this.displayColor = displayColor;
        }

        public String getDisplayText() {
            return displayText;
        }

        public DisplayColor getDisplayColor() {
            return displayColor;
        }
    }
    
    public enum SubmitType {
        AUTO,
        MANUAL
    }
    
    public enum StrategyType {
        LUXOR("20, 50, 1.3d, 2.2d, 4, 6"), // emaShort, emaLong, stopPct, targetPct, startHourEst, durationHours
        MACD_CROSS("50, 50"), // stochOversold, stochOverbougth
        TEST("1.3d, 2.2d"); // stopPct, targetPct

        private String defaultParams;

        StrategyType(String defaultParams) {
            this.defaultParams = defaultParams;
        }

        public String getDefaultParams() {
            return defaultParams;
        }
    }
    
    public enum StrategyMode {
        IB(DisplayColor.ORANGE_BG),
        SIM(null),
        BTEST(null);

        private DisplayColor displayColor;

        StrategyMode(DisplayColor displayColor) {
            this.displayColor = displayColor;
        }

        public String getDisplayColor() {
            return displayColor.name().toLowerCase();
        }
    }

    public enum TradeType {
        LONG("L", DisplayColor.BLUE),
        SHORT("S", DisplayColor.BROWN);

        private String displayText;
        private DisplayColor displayColor;

        TradeType(String displayText, DisplayColor displayColor) {
            this.displayText = displayText;
            this.displayColor = displayColor;
        }
        public String getDisplayText() {
            return displayText;
        }

        public String getDisplayColor() {
            return displayColor.name().toLowerCase();
        }
    }
    
    public enum TradeStatus {
        INIT_OPEN("initOpen", DisplayColor.BLUE),
        OPEN("open", DisplayColor.GREEN),
        INIT_CLOSE("initClose", DisplayColor.ORANGE_BG),
        CLOSED("closed", DisplayColor.BROWN),
        CNC_CLOSED("cncClosed", DisplayColor.RED),
        ERR_CLOSED("errClosed", DisplayColor.RED);
        
        private String displayText;
        private DisplayColor displayColor;
        
        TradeStatus(String displayText, DisplayColor displayColor) {
            this.displayText = displayText;
            this.displayColor = displayColor;
        }
        public String getDisplayText() {
            return displayText;
        }

        public String getDisplayColor() {
            return displayColor.name().toLowerCase();
        }
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

    public enum RealtimeFieldName {
        BID,
        ASK,
        LAST,
        CLOSE,
        BID_SIZE,
        ASK_SIZE,
        LAST_SIZE,
        VOLUME,
        CHANGE_PCT
    }

    public enum RealtimeStatus {
        UPTICK(DisplayColor.LIME),
        DOWNTICK(DisplayColor.ORANGE),
        UNCHANGED(DisplayColor.YELLOW),
        POSITIVE(DisplayColor.BLUE),
        NEGATIVE(DisplayColor.RED);

        private DisplayColor displayColor;

        RealtimeStatus(DisplayColor displayColor) {
            this.displayColor = displayColor;
        }

        public String getDisplayColor() {
            return displayColor.name().toLowerCase();
        }
    }

    public enum Exchange {
        SMART,
        IDEALPRO,
        GLOBEX,
        ECBOT,
        NYMEX
    }

    public enum DisplayColor {
        MAGENTA,
        BLUE,
        DARK_BLUE,
        GREEN,
        DARK_GREEN,
        RED,
        DARK_RED,
        ORANGE,
        ORANGE_BG,
        DARK_ORANGE,
        DARK_CYAN,
        LIME,
        YELLOW,
        BROWN,
        DODGER_BLUE,
    }

    public enum IbConnectionType {
        MKTDATA,
        EXEC
    }

    public enum MessageType {
        DATABARS_CREATED,
        IBORDER_CREATED,
        IBORDER_UPDATED
    }

    public enum BacktestStatus {
        AVAILABLE_INPROGRESS,
        INPROGRESS,
        AVAILABLE,
        NONE
    }

    public enum StrategyDataType {
        TRADING,
        BACKTEST
    }
}
