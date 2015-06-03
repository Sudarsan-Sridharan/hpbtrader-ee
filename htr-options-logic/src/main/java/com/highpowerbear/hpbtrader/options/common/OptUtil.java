package com.highpowerbear.hpbtrader.options.common;

import com.highpowerbear.hpbtrader.options.entity.Trade;
import com.highpowerbear.hpbtrader.options.ibclient.IbApiEnums;

import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
public class OptUtil {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    private static DateFormat expiryFormatFull = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat expiryFormatShort = new SimpleDateFormat("yyyyMM");
    
    public static Calendar getNowCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(OptDefinitions.TIMEZONE));
    }
    
    public static Calendar getYesterdayCalendar() {
        Calendar cal = getNowCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal;
    }
    
    public static String getLocalIp() {
        String ip = "localhost";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress();
                    //Repository.getInstance().getLogger().info(iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            //Repository.getInstance().getLogger().error(e.getMessage());
        }
        return ip;
    }
    
    public static String printIbContract(com.ib.client.Contract contract) {
        return  contract.m_localSymbol + ", " + contract.m_symbol + ", " + contract.m_secType + ", " + contract.m_expiry + ", " + contract.m_right + ", " + 
                contract.m_exchange + ", " + contract.m_currency + ", " + contract.m_multiplier + ", " +  contract.m_includeExpired;
    }
    
    public static void waitMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            l.log(Level.SEVERE, "Error", e);
        }
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
        Calendar cal = getNowCalendar();
        try {
            Date date = expiryFormatFull.parse(expiry);
            cal.setTime(date);
        } catch (ParseException pe) {
            return null;
        }
        return cal;
    }
    
    public static Calendar getTodayMidnightCalendar() {
        Calendar cal = getNowCalendar();
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
    
    public static Color toDarkerColor(Color color) {
        if (color == null) {
            return null;
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int x = 10;
        return new Color((r >= x ? r - x : 0), (g >= x ? g - x : 0), (b >= 3*x ? b - 3*x : 0));
    }
    
     public static com.ib.client.Contract constructIbContract(String localSymbol) {
        com.ib.client.Contract ibContract = new com.ib.client.Contract();
        ibContract.m_localSymbol = localSymbol;
        ibContract.m_symbol = (isOptionSymbol(localSymbol) ? null : localSymbol);
        ibContract.m_secType = (isOptionSymbol(localSymbol) ? IbApiEnums.SecType.OPT.getName() : IbApiEnums.SecType.STK.getName());
        ibContract.m_exchange = IbApiEnums.Exchange.SMART.getName();
        ibContract.m_currency = IbApiEnums.Currency.USD.getName();
        return ibContract;
     }
     
    public static double round(double number, int decimalPlaces) {
	double modifier = Math.pow(10.0, decimalPlaces);
	return Math.round(number * modifier) / modifier;
    }
    
    public static double round5(double number) {
	return round(number, 5);
    }
    
    public static Double abs (Double number) {
        return (number != null ? Math.abs(number) : null);
    }
    
    public static String printTrades(List<Trade> trades) {
        if (trades == null || trades.isEmpty()) {
            return "active trade=" + OptDefinitions.NONE;
        }
        StringBuilder sb = new StringBuilder();
        for (Trade t : trades) {
            sb.append("active trade=").append(t.print()).append(",");
        }
        if (sb.lastIndexOf(",") > 0 ) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }
}
