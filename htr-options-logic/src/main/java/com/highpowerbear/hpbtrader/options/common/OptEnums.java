package com.highpowerbear.hpbtrader.options.common;

import java.awt.*;

/**
 *
 * @author rkolar
 */
public class OptEnums {
    public enum SignalAction {
        OPEN_LONG("OpenLong"),
        OPEN_SHORT("OpenShort"),
        CLOSE("Close"),
        REVERSE("Reverse");
        
        private String name;
        SignalAction(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    
    public enum SignalStatus {
        NEW("New", Color.BLUE),
        CONVERTED("Converted", Color.GREEN.darker()),
        NOT_ACCEPTED("NotAccepted", Color.RED.darker());
        
        private String name;
        private Color color;
        
        SignalStatus(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        public String getName() {
            return name;
        }
        public Color getColor() {
            return color;
        }
        public static SignalStatus getByName(String name) {
            if (CONVERTED.getName().equals(name)) return CONVERTED; else
            if (NOT_ACCEPTED.getName().equals(name)) return NOT_ACCEPTED; else
            return null;
        }
    }
    
    public enum OrderStatus {
        NEW("New", Color.MAGENTA),
        NEW_RETRY("NewRetry", Color.MAGENTA),
        SUBMIT_REQ("SubmitReq", Color.BLUE),
        SUBMITTED("Submitted", Color.BLUE.darker()),
        FILLED("Filled", Color.GREEN.darker()),
        EXT_CANCELED("ExtCanceled", Color.RED.darker()),
        UNKNOWN("Unknown", Color.RED);
        
        private String name;
        private Color color;
        
        OrderStatus(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        public String getName() {
            return name;
        }
        public Color getColor() {
            return color;
        }
        public static OrderStatus getByName(String name) {
            if (NEW.getName().equals(name)) return NEW; else
            if (NEW_RETRY.getName().equals(name)) return NEW_RETRY; else
            if (SUBMIT_REQ.getName().equals(name)) return SUBMIT_REQ; else
            if (SUBMITTED.getName().equals(name)) return SUBMITTED; else
            if (FILLED.getName().equals(name)) return FILLED; else
            if (EXT_CANCELED.getName().equals(name)) return EXT_CANCELED; else
            if (UNKNOWN.getName().equals(name)) return UNKNOWN; else
            return null;
        }
    }
    
    public enum SignalOrigin {
        API("API"),
        MANUAL("Manual");
        
        private String name;
        
        SignalOrigin(String displayName) {
            this.name = displayName;
        }
        public String getName() {
            return name;
        }
    }
    
    public enum TradeStatus {
        INIT_OPEN("InitOpen", Color.BLUE),
        OPEN("Open", Color.GREEN.darker()),
        INIT_FIRST_EXIT("InitFirstExit", Color.ORANGE),
        FIRST_EXITED("FirstExited" , Color.CYAN.darker()),
        INIT_CLOSE("InitClose", Color.ORANGE),
        CLOSED("Closed", Color.ORANGE.darker()),
        INVALID("Invalid", Color.RED);
        
        private String name;
        private Color color;
        
        TradeStatus(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        public String getName() {
            return name;
        }
        public Color getColor() {
            return color;
        }
        public static TradeStatus getByName(String name) {
            if (INIT_OPEN.getName().equals(name)) return INIT_OPEN; else
            if (OPEN.getName().equals(name)) return OPEN; else
            if (INIT_FIRST_EXIT.getName().equals(name)) return INIT_FIRST_EXIT; else
            if (FIRST_EXITED.getName().equals(name)) return FIRST_EXITED; else
            if (INIT_CLOSE.getName().equals(name)) return INIT_CLOSE; else
            if (CLOSED.getName().equals(name)) return CLOSED; else
            if (INVALID.getName().equals(name)) return INVALID; else
            return null;
        }
    }
    
    public enum DataChangeEvent {
        CONTRACT_LOG,
        CONTRACT_PROPERTIES,
        MARKET_DATA,
        OPTION_CONTRACT,
        ORDER,
        REST_API_LOG,
        SIGNAL,
        TRADE
    }
    
    public enum ValueStatus {
        UPTICK,
        DOWNTICK,
        UNCHANGED
    }
    
    public enum Lot {
        FIRST(1),
        SECOND(2);
        
        private Integer number;

        Lot(Integer number) {
            this.number = number;
        }
        public Integer getNumber() {
            return number;
        }
    }
    
    public enum ExpiryDistance {
        FRONT_WEEK(0), 
        NEXT_WEEK(1);
        
        private Integer week;

        ExpiryDistance(Integer number) {
            this.week = number;
        }
        public Integer getWeek() {
            return week;
        }
    }
    
    // rest methods
    public enum SignalErrorLongResponse {
        NOT_ACCEPTED(-1L),
        INVALID_REQUEST(-2L);
        
        private Long value;

        SignalErrorLongResponse(Long returnValue) {
            this.value = returnValue;
        }
        public Long getValue() {
            return value;
        }
    }
    
    public enum SignalErrorStringResponse {
        INVALID_SIGNAL_ID("INVALID_SIGNAL_ID");
        
        private String value;

        SignalErrorStringResponse(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    
    public enum UnderlyingErrorResponse {
        INVALID_UNDERLYING("INVALID_UNDERLYING"),
        NONE("NONE");
        
        private String value;

        UnderlyingErrorResponse(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    // end rest methods
    
    public enum RequestIdOffset {
        CHAIN_CALL_CURRENT_MONTH(1),
        CHAIN_CALL_NEXT_MONTH(2),
        CHAIN_PUT_CURRENT_MONTH(3),
        CHAIN_PUT_NEXT_MONTH(4),
        UNDERLYING(10),
        CALL_ACTIVE(20),
        CALL_FRONT_WEEK(21),
        CALL_NEXT_WEEK(22),
        PUT_ACTIVE(30),
        PUT_FRONT_WEEK(31),
        PUT_NEXT_WEEK(32);
        
        private Integer value;

        RequestIdOffset(Integer value) {
            this.value = value;
        }
        public Integer getValue() {
            return value;
        }
    }
    
    public enum ContractStatus {
        ACTIVE("Active", Color.MAGENTA.darker()),
        STANDBY("Standby", Color.CYAN),
        PURCHASED("Purchased", Color.GREEN.darker()),
        NONE("None", null);
        
        private String name;
        private Color color;
        
        ContractStatus(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        public String getName() {
            return name;
        }
        public Color getColor() {
            return color;
        }
    }
}
