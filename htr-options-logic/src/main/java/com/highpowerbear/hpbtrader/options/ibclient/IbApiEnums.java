package com.highpowerbear.hpbtrader.options.ibclient;

import java.awt.*;

/**
 *
 * @author Robert
 */
public class IbApiEnums {
    public enum Action {
        BUY ("BUY", Color.BLUE),
        SELL ("SELL", Color.RED.darker()),
        SSHORT ("SSHORT", Color.RED.darker());

        private String name;
        private Color color;

        Action(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }
        public Color getColor() {
            return color;
        }
        
        public static Action getEnumFromName(String name) {
            if (name.equals(BUY.getName()))
                return BUY;
            if (name.equals(SELL.getName()))
                return SELL;
            if (name.equals(SSHORT.getName()))
                return SSHORT;
            return null;
        }
    }

    public enum OrderStatus {
        PENDINGSUBMIT ("PendingSubmit"),
        PENDINGCANCEL ("PendingCancel"),
        PRESUBMITTED ("PreSubmitted"),
        SUBMITTED ("Submitted"),
        CANCELLED ("Cancelled"),
        FILLED ("Filled"),
        INACTIVE ("Inactive");

        private String name;

        OrderStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum OrderType {
        MKT ("MKT"),
        MKTCLS ("MKTCLS"),
        LMT ("LMT"),
        LMTCLS ("LMTCLS"),
        PEGMKT ("PEGMKT"),
        SCALE ("SCALE"),
        STP ("STP"),
        STPLMT ("STPLMT"),
        TRAIL ("TRAIL"),
        REL ("REL"),
        VWAP ("VWAP"),
        TRAILLIMIT ("TRAILLIMIT");

        private String name;

        OrderType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SecType {
        STK ("STK"),
        OPT ("OPT"),
        FUT ("FUT"),
        IND ("IND"),
        FOP ("FOP"),
        CASH ("CASH"),
        BAG ("BAG");

        private String name;

        SecType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
        
        public static SecType getEnumFromName(String name) {
            if (name.equals(STK.getName()))
                return STK;
            if (name.equals(OPT.getName()))
                return OPT;
            if (name.equals(FUT.getName()))
                return FUT;
            if (name.equals(IND.getName()))
                return IND;
            if (name.equals(FOP.getName()))
                return FOP;
            if (name.equals(CASH.getName()))
                return CASH;
            if (name.equals(BAG.getName()))
                return BAG;
            return null;
        }
    }

    public enum Tif {
        DAY ("DAY"),
        GTC ("GTC"),
        IOC ("IOC"),
        GTD ("GTD");

        private String name;

        Tif(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    
    public enum OptionType {
        CALL("C", "Call"), PUT("P", "Put");
        
        private String name;
        private String displayName;
        
        OptionType(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }
        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }
        public static OptionType getByName(String name) {
            if (name.equals(CALL.getName())) 
                return CALL; 
            else if (name.equals(PUT.getName())) 
                return PUT;
            else return null;
        }
        
    }
    
    public enum Exchange {
        SMART("SMART");
        
        private String name;
        
        Exchange(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    
    public enum Currency {
        USD("USD");
        
        private String name;
        
        Currency(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    
    public enum Multiplier {
        M_10("10"),
        M_100("100"),
        M_1000("1000");
        
        private String name;

        Multiplier(String multiplier) {
            this.name = multiplier;
        }

        public String getName() {
            return name;
        }
    }
    
    public enum ErrorCode {
        NO_SECURITY_DEFINTION(200);
        
        private Integer value;

        ErrorCode(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
