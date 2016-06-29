package com.highpowerbear.hpbtrader.shared.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rkolar
 */
public class HtrUtil {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);
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
        return a == b || !(a == null || b == null) && a.equals(b);
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

    public static String constructMessage(HtrEnums.MessageType type, String content) {
        return type.name() + ": " + content;
    }

    public static HtrEnums.MessageType parseMessageType(String msg) {
        HtrEnums.MessageType type = null;
        String[] parts = msg.split(":");
        if (parts.length == 2) {
            try {
                type = HtrEnums.MessageType.valueOf(parts[0].trim());
            } catch (Exception e) {
                l.warning(e.getMessage());
            }
        }
        return type;
    }

    public static String parseMessageContent(String msg) {
        String content = null;
        String[] parts = msg.split(":");
        if (parts.length == 2) {
            content = parts[1].trim();
        }
        return content;
    }
}