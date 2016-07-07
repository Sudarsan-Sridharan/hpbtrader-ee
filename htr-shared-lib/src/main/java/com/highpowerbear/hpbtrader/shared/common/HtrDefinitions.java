package com.highpowerbear.hpbtrader.shared.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by rkolar on 4/23/14.
 */
public class HtrDefinitions {
    // constants
    public static final String IB_ASK_LITERAL = "ASK";
    public static final String IB_TRADES_LITERAL = "TRADES";
    public static final String IB_TIMEZONE = "EST";
    public static final String FINISH = "finish";
    public static final String IB_DURATION_1_DAY = "1 D";
    public static final String IB_DURATION_2_DAY = "2 D";
    public static final String IB_DURATION_10_DAY = "10 D";
    public static final String IB_DURATION_1_WEEK = "1 W";
    public static final String IB_DURATION_1_MONTH = "1 M";
    public static final String IB_BAR_5_MIN = "5 mins";
    public static final String IB_BAR_1_HOUR = "1 hour";
    public static final int IB_ETH_TOO = 0;
    public static final int IB_RTH_ONLY = 1;
    public static final int IB_FORMAT_DATE_MILLIS = 2;
    public static final Double INVALID_PRICE = -1.0;
    public static final Integer INVALID_SIZE = -1;
    public static final Integer ONE_SECOND_MILLIS = 1000;
    public static final DateFormat DF = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
    public static final String MANUAL_ORDER = "Manual order";

    // settings
    public static final String LOGGER = "com.highpowerbear.hpbtrader";
    public static final String EMAIL_FROM = "hpbtrader@highpowerbear.com";
    public static final String EMAIL_TO = "info@highpowerbear.com";
    public static final String MKTDATA_TO_STRATEGY_QUEUE = "java:/jms/queue/MktDataToStrategyQ";
    public static final String STRATEGY_TO_EXEC_QUEUE = "java:/jms/queue/StrategyToExecQ";
    public static final String EXEC_TO_STRATEGY_QUEUE = "java:/jms/queue/ExecToStrategyQ";
    public static final int BARS_REQUIRED = 400;
    public static final int MAX_EMA_PERIOD = 200;
    public static final String TIMEZONE = "America/New_York";
    public static final Integer MAX_ORDER_HEARTBEAT_FAILS = 5;
    public static final Integer IB_REQUEST_MULT = 100;
    public static final Integer WEBSOCKET_ASYNC_SEND_TIMEOUT = 5 * 1000; // milliseconds
    public static final int JPA_MAX_RESULTS = 1000;
    public static final int BACKTEST_DEFAULT_MONTHS = 3;
    public static final int BLOCKING_QUEUE_CAPACITY = 5;
}