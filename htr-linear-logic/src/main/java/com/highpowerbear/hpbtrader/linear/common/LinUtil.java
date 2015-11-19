package com.highpowerbear.hpbtrader.linear.common;

import com.highpowerbear.hpbtrader.linear.definitions.LinSettings;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rkolar
 */
public class LinUtil {
    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");

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
        return Calendar.getInstance(TimeZone.getTimeZone(LinSettings.TIMEZONE));
    }
    
    public static Calendar getCalendarMonthsOffset(int offset) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(LinSettings.TIMEZONE));
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
}