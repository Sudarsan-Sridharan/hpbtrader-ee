package com.highpowerbear.hpbtrader.shared.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rkolar
 */
public class HtrUtil {
    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static DateFormat expiryFormatFull = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat expiryFormatShort = new SimpleDateFormat("yyyyMM");

    public static void waitMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            // Ignore
        }
    }
    
    public static String getFormattedDate(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return df.format(cal.getTime());
    }

    public static boolean equalsWithNulls(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
    
    public static double round(double number, int decimalPlaces) {
	double modifier = Math.pow(10.0, decimalPlaces);
	return Math.round(number * modifier) / modifier;
    }
    
    public static double round5(double number) {
	return round(number, 5);
    }
    
    public static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(HtrDefinitions.TIMEZONE));
    }
    
    public static Calendar getCalendarMonthsOffset(int offset) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(HtrDefinitions.TIMEZONE));
        cal.add(Calendar.MONTH, offset);
        return cal;
    }

    public static String printIbContract(com.ib.client.Contract contract) {
        return  contract.m_localSymbol + ", " + contract.m_symbol + ", " + contract.m_secType + ", " + contract.m_expiry + ", " + contract.m_right + ", " +
                contract.m_exchange + ", " + contract.m_currency + ", " + contract.m_multiplier + ", " +  contract.m_includeExpired;
    }

    public static String removeSpace(String source) {
        return source.replaceAll("\\b\\s+\\b", "");
    }

    public static String removeDot(String source) {
        return source.replaceAll("[\\s.]", "");
    }

    public static Calendar getNowCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(HtrDefinitions.TIMEZONE));
    }

    public static Calendar getYesterdayCalendar() {
        Calendar cal = getNowCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal;
    }

    public static Double roundDownToHalf(Double d) {
        return Math.floor(d * 2) / 2;
    }

    public static Double roundUpToHalf(Double d) {
        return Math.ceil(d * 2) / 2;
    }

    public static String constructHypotheticOccSymbol(String underlying, Calendar expiry, String right, Double strike) {
        DateFormat expiryFormat = new SimpleDateFormat("yyMMdd");
        if (underlying == null || underlying.length() > 6 || underlying.length() < 1 || expiry == null || right == null || (!right.equals("P") && !right.equals("C")) || strike == null || strike <= 0.5) {
            return null;
        }
        String underlyingString = underlying;
        // pad with spaces
        for (int i = underlying.length(); i < 6; i++) {
            underlyingString = underlyingString + " ";
        }
        String expiryString = expiryFormat.format(expiry.getTime());
        String strikeParts[] = String.valueOf(strike).split("\\.");
        String dollarStrike = strikeParts[0];
        String decimalStrike = strikeParts[1];
        // pad left with zeros
        for (int i = strikeParts[0].length(); i < 5; i++) {
            dollarStrike = "0" + dollarStrike;
        }
        // pad right with zeros
        for (int i = strikeParts[1].length(); i < 3; i++) {
            decimalStrike = decimalStrike + "0";
        }
        return underlyingString + expiryString + right + dollarStrike + decimalStrike;
    }

    public static boolean isOptionSymbol(String symbol) {
        return (symbol != null && symbol.length() == 21);
    }

    public static Calendar expiryFullToCalendar(String expiry) {
        Calendar cal = HtrUtil.getNowCalendar();
        try {
            Date date = expiryFormatFull.parse(expiry);
            cal.setTime(date);
        } catch (ParseException pe) {
            return null;
        }
        return cal;
    }

    public static Calendar getTodayMidnightCalendar() {
        Calendar cal = HtrUtil.getNowCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static String toExpiryStringFull(Calendar expiry) {
        return expiryFormatFull.format(expiry.getTime());
    }

    public static String toExpiryStringShort(Calendar expiry) {
        return expiryFormatShort.format(expiry.getTime());
    }

    public static com.ib.client.Contract constructIbContract(String localSymbol) {
        com.ib.client.Contract ibContract = new com.ib.client.Contract();
        ibContract.m_localSymbol = localSymbol;
        ibContract.m_symbol = (isOptionSymbol(localSymbol) ? null : localSymbol);
        ibContract.m_secType = (isOptionSymbol(localSymbol) ? HtrEnums.SecType.OPT.name() : HtrEnums.SecType.STK.name());
        ibContract.m_exchange = HtrEnums.Exchange.SMART.name();
        ibContract.m_currency = HtrEnums.Currency.USD.name();
        return ibContract;
    }

    public static Double abs (Double number) {
        return (number != null ? Math.abs(number) : null);
    }
}