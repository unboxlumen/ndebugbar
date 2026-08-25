package com.unboxlumen.ndebugbar.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    private static SimpleDateFormat defaultSdf;

    static {
        defaultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    public static String formatNow() {
        return defaultSdf.format(new Date());
    }

    public static String formatNow(String pattern) {
        return (new SimpleDateFormat(pattern, Locale.CHINA)).format(new Date());
    }

    public static String formatDate(Date date) {
        return defaultSdf.format(date);
    }

    public static String formatDate(long mills, String pattern) {
        return mills != 0L ? (new SimpleDateFormat(pattern, Locale.CHINA)).format(mills) : "";
    }

    public static int getYear() {
        Calendar startCalendar = Calendar.getInstance();
        return startCalendar.get(1);
    }

    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(2);
    }

    public static int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(5);
    }

    public static Date StringToDate(String dateTimeStr) {
        Date date = new Date();

        try {
            date = defaultSdf.parse(dateTimeStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public static Calendar StringToGregorianCalendar(String dateTimeStr) {
        Date date = StringToDate(dateTimeStr);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    public static long stringToMillis(String dateTime) {
        Calendar c = StringToGregorianCalendar(dateTime);
        return c.getTimeInMillis();
    }

    public static int compareDateTimeString(String str1, String str2) {
        Date d1 = StringToDate(str1);
        Date d2 = StringToDate(str2);
        if (d1.getTime() - d2.getTime() < 0L) {
            return -1;
        } else {
            return d1.getTime() - d2.getTime() > 0L ? 1 : 0;
        }
    }

    public static long GetLongSystime() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    public static String getHourDiffer(String startTime, String endTime) {
        double nh = (double) 3600000.0F;
        double diff = (double) (stringToCalendar(endTime).getTimeInMillis() - stringToCalendar(startTime).getTimeInMillis());
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(diff / nh);
    }

    public static Calendar stringToCalendar(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (Exception var4) {
            return Calendar.getInstance();
        }
    }

    public static long stringtoLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long millionSeconds = 0L;

        try {
            Date date = sdf.parse(time);
            millionSeconds = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return millionSeconds;
    }
}

