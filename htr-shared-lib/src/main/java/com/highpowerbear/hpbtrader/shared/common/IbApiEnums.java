package com.highpowerbear.hpbtrader.shared.common;

/**
 *
 * @author Robert
 */
public class IbApiEnums {
    public enum Action {
        BUY ("BUY"),
        SELL ("SELL"),
        SSHORT ("SSHORT");

        private String name;

        Action(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
        PENDINGSUBMIT ("PendingSubmit", "col-dodgerblue-bck"),
        PENDINGCANCEL ("PendingCancel", "col-orange-bck"),
        PRESUBMITTED ("PreSubmitted", "col-dodgerblue-bck"),
        SUBMITTED ("Submitted", "col-blue-bck"),
        CANCELLED ("Cancelled", "col-red-bck"),
        FILLED ("Filled", "col-green-bck"),
        INACTIVE ("Inactive", "col-brown-bck");

        private String name;
        private String colorClass;

        OrderStatus(String name, String colorClass) {
            this.name = name;
            this.colorClass = colorClass;
        }

        public String getName() {
            return name;
        }
        public String getColorClass() {
            return colorClass;
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
}
