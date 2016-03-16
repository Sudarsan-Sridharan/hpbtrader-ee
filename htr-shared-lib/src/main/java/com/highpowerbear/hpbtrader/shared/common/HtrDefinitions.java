package com.highpowerbear.hpbtrader.shared.common;

/**
 * Created by rkolar on 4/23/14.
 */
public class HtrDefinitions {
    // constants
    public static final int ONE_SECOND = 1000; // milliseconds
    public static final String IB_BAR_TYPE_ASK = "ASK";
    public static final String IB_BAR_TYPE_TRADES = "TRADES";
    public static final String IB_TIMEZONE = "EST";

    // settings
    public static final String LOGGER = "com.highpowerbear.hpbtrader.shared";

    public static final int IB_CONNECT_CLIENT_ID = 7;
    public static final String EMAIL_FROM = "hpbtrader@highpowerbear.com";
    public static final String EMAIL_TO = "info@highpowerbear.com";
    public static final int BARS_REQUIRED = 400;
    public static final int MAX_EMA_PERIOD = 200;

    public static final String TIMEZONE = "America/New_York";

    public static final Integer MAX_ORDER_HEARTBEAT_FAILS = 5;
    public static final Integer IB_REQUEST_MULT = 100;

    public static final Integer WEBSOCKET_ASYNC_SEND_TIMEOUT = 5 * 1000; // milliseconds
    public static final int JPA_MAX_RESULTS = 1000;
}