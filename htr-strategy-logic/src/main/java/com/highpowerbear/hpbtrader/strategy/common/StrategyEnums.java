package com.highpowerbear.hpbtrader.strategy.common;

/**
 *
 * @author rkolar
 */
public class StrategyEnums {

    public enum OptRequestIdOffset {
        CHAIN_CALL_THIS_MONTH(1),
        CHAIN_CALL_NEXT_MONTH(2),
        CHAIN_PUT_THIS_MONTH(11),
        CHAIN_PUT_NEXT_MONTH(12),
        MKTDATA_UNDERLYING(10),
        MKTDATA_CALL_ACTIVE(20),
        MKTDATA_PUT_ACTIVE(30);

        private Integer value;

        OptRequestIdOffset(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public enum OptExpiryDistance {
        FRONT_WEEK(0),
        ONE_WEEK_OUT(1),
        TWO_WEEK_OUT(2),
        THREE_WEEK_OUT(3);

        private Integer week;

        OptExpiryDistance(Integer number) {
            this.week = number;
        }

        public Integer getWeek() {
            return week;
        }
    }
}