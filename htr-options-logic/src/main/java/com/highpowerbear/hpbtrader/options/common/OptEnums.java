package com.highpowerbear.hpbtrader.options.common;

/**
 *
 * @author rkolar
 */
public class OptEnums {

    public enum Underlying {
        IWM("IWM"),
        SPY("IWM");

        private String label;

        Underlying(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum ParamType {
        SENTIMENT_DELTA_MULT("sentimentDeltaMult"),
        DELTA_TRIGGER_STEP("deltaTriggerStep"),
        UNDL_MIN_QUANT_STEP("undlMinQuantStep"),
        EXPIRY_DISTANCE("expiryDistance"),
        CALL_QUANT("callQuant"),
        PUT_QUANT("putQuant"),
        INIT_OTM_POINTS("initOtmPoints"),
        ROLL_ITM_POINTS("rollItmPoints"),
        INIT_TIME_VALUE_PCT("initTimeValuePct");

        private String name;

        ParamType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum StrategyType {
        WEEKLY_WRITE_HEDGE("WeeklyWriteHedge");

        private String label;

        StrategyType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum SentimentType {
        STRONG_BEARISH(-100, "strongBearish"),
        BEARISH(-50, "bearish"),
        NEUTRAL(0, "neutral"),
        BULLISH(50, "bullish"),
        STRONG_BULLISH(100, "strongBullish");

        private Integer sentimentDelta;
        private String label;

        SentimentType(Integer sentimentDelta, String label) {
            this.sentimentDelta = sentimentDelta;
            this.label = label;
        }

        public Integer getSentimentDelta() {
            return sentimentDelta;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum DataChangeEvent {
        STRATEGY,
        MKT_DATA,
        OPT_ORDER,
        OPT_CONTRACT
    }

    public enum CssColor {
        MAGENTA("Magenta"),
        BLUE("Blue"),
        DARK_BLUE("DarkBlue"),
        DARK_GREEN("DarkGreen"),
        RED("Red"),
        DARK_RED("DarkRed"),
        ORANGE("Orange"),
        DARK_ORANGE("DarkOrange"),
        DARK_CYAN("DarkCyan");

        private String name;

        CssColor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum OrderStatus {
        NEW("New", CssColor.MAGENTA.getName()),
        NEW_RETRY("NewRetry", CssColor.MAGENTA.getName()),
        SUBMIT_REQ("SubmitReq", CssColor.BLUE.getName()),
        SUBMITTED("Submitted", CssColor.DARK_BLUE.getName()),
        FILLED("Filled", CssColor.DARK_GREEN.getName()),
        EXT_CANCELED("ExtCanceled", CssColor.DARK_RED.getName()),
        UNKNOWN("Unknown", CssColor.RED.getName());
        
        private String label;
        private String colorName;
        
        OrderStatus(String label, String colorName) {
            this.label = label;
            this.colorName = colorName;
        }

        public String getLabel() {
            return label;
        }

        public String getColorName() {
            return colorName;
        }
    }

    public enum TradeStatus {
        INIT_OPEN("InitOpen", CssColor.BLUE.getName()),
        OPEN("Open", CssColor.DARK_GREEN.getName()),
        INIT_CLOSE("InitClose", CssColor.ORANGE.getName()),
        CLOSED("Closed", CssColor.DARK_ORANGE.getName()),
        INVALID("Invalid", CssColor.RED.getName());

        private String label;
        private String colorName;

        TradeStatus(String label, String colorName) {
            this.label = label;
            this.colorName = colorName;
        }

        public String getLabel() {
            return label;
        }

        public String getColorName() {
            return colorName;
        }
    }

    public enum SentimentOrigin {
        HTR_LINEAR("htr_linear"),
        MANUAL("manual");
        
        private String label;
        
        SentimentOrigin(String displayName) {
            this.label = displayName;
        }

        public String getLabel() {
            return label;
        }
    }
    
    public enum ValueStatus {
        UPTICK,
        DOWNTICK,
        UNCHANGED
    }
    
    public enum ExpiryDistance {
        FRONT_WEEK(0),
        ONE_WEEK_OUT(1),
        TWO_WEEK_OUT(2),
        THREE_WEEK_OUT(3);
        
        private Integer week;

        ExpiryDistance(Integer number) {
            this.week = number;
        }

        public Integer getWeek() {
            return week;
        }
    }
    
    public enum RequestIdOffset {
        CHAIN_CALL_THIS_MONTH(1),
        CHAIN_CALL_NEXT_MONTH(2),
        CHAIN_PUT_THIS_MONTH(11),
        CHAIN_PUT_NEXT_MONTH(12),
        MKTDATA_UNDERLYING(10),
        MKTDATA_CALL_ACTIVE(20),
        MKTDATA_PUT_ACTIVE(30);

        private Integer value;

        RequestIdOffset(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
