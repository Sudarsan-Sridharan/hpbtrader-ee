package com.highpowerbear.hpbtrader.linear.definitions;

/**
 * Created by robertk on 6/2/15.
 */
public class LinSettings {
    // settings
    public static final String LOGGER = "com.highpowerbear.hpbtrader";
    public static final String HOST = "localhost";
    public static final int PORT = 4002;
    public static final int CLIENT_ID = 7;
    public static final String EMAIL_FROM = "hpbtrader@highpowerbear.com";
    public static final String EMAIL_TO = "info@highpowerbear.com";
    public static final int BARS_REQUIRED = 400;
    public static final int MAX_EMA_PERIOD = 200;

    public static final String TIMEZONE = "America/New_York";
    public static final Integer RECENT_ORDER_DAYS = 2;

    public static final Integer MAX_ORDER_HEARTBEAT_FAILS = 5;
    public static final Integer IB_REQUEST_MULT = 100;
}
