package com.highpowerbear.hpbtrader.strategy.common;

/**
 * Created by robertk on 17.3.2016.
 */
public class StrategyDefinitions {
    // settings
    public static final String LOGGER = "com.highpowerbear.hpbtrader";

    // constants
    public static final Integer ONE_SECOND_MILLIS = 1000;
    public static final Double INVALID_PRICE = -1.0;
    public static final Integer INVALID_SIZE = -1;
    public static final String NOT_AVAILABLE = "NA";
    public static final String NONE = "NONE";

    // option settings
    public static final String IB_HOST = "localhost";
    public static final Integer IB_PORT = 4002;
    public static final Integer IB_CLIENT_ID = 5;
    public static final Boolean PROCESS_ENABLED = true;
    public static final Boolean CONSTRAINTS_ENABLED = true;
    public static final Integer REQUEST_ID_MULTIPLIER = 1000;
    public static final Integer JPA_MAX_RESULTS = 1000;
    public static final Integer MAX_ORDER_HEARTBEAT_FAILS = 5;
    public static final Integer CONTRACT_CHANGE_MIN_INTERVAL = 60; // seconds
    public static final int NUM_CONTRACT_PROPS = 13;

    // option contract properties precautionary limits
    public static final Double MIN_maxSpread = 0.01; // 1
    public static final Double MIN_maxValidSpread = 0.05; // 2
    public static final Integer MIN_minVolume = 0; // 3
    public static final Integer MIN_minOpenInterest = 0; // 4
    public static final Double MIN_callStrikeDiff = 0.5; // 5
    public static final Double MIN_putStrikeDiff = 0.01; // 6
    // autoLimit // 7
    public static final Double MAX_ABS_bidPriceOffset = 0.99; // 8, 9
    public static final Integer MAX_tradingQuantCall = 200; // 10
    public static final Integer MAX_tradingQuantPut = 200; // 11
    // tradingStartTime, tradingStopTime // 12, 13
}
