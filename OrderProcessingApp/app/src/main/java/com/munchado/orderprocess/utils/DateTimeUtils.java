package com.munchado.orderprocess.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by munchado on 23/2/17.
 */
public class DateTimeUtils {
    public static final String FORMAT_YYYY_MM_DD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_MMM_DD_YYY_AT_HHMM_A = "MMMM dd, yyy at hh:mm a";
    public static final String FORMAT_MMM_DD_YYYY = "MMM dd, yyyy";
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_HH_MM_A = "hh:mm a";


    static Date getCurrentNewYorkTime() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String formattedStr = sdf.format(calendar.getTime());
            return new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS).parse(formattedStr);
        } catch (Exception x) {
            return null;
        }
    }

    public static String getTimeAgo(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
            long milliSeconds = sdf.parse(dateStr).getTime();
            long currentMilliSeconds = getCurrentNewYorkTime().getTime();

            long timeago = TimeUnit.MILLISECONDS.toSeconds(currentMilliSeconds - milliSeconds);
            if (timeago > 0 && timeago < 60)
                return timeago + " seconds ago";

            timeago = TimeUnit.MILLISECONDS.toMinutes(currentMilliSeconds - milliSeconds);
            if (timeago > 0 && timeago < 60)
                return timeago + " minutes ago";

            timeago = TimeUnit.MILLISECONDS.toHours(currentMilliSeconds - milliSeconds);
            if (timeago > 0 && timeago < 24)
                return timeago == 1 ? (timeago + " hour ago") : (timeago + " hours ago");

            timeago = TimeUnit.MILLISECONDS.toDays(currentMilliSeconds - milliSeconds);
            if (timeago > 0 && timeago < 7)
                return timeago == 1 ? (timeago + " day ago") : (timeago + " days ago");
            if (timeago > 6 && timeago < 30)
                return timeago / 7 == 1 ? (timeago / 7 + " week ago") : (timeago / 7 + " weeks ago");
            if (timeago > 29 && timeago < 364)
                return timeago / 30 == 1 ? (timeago / 30 + " month ago") : (timeago / 30 + " months ago");
            if (timeago > 365)
                return timeago / 365 == 1 ? (timeago / 365 + " year ago") : (timeago / 365 + " years ago");
        } catch (Exception j) {
            j.printStackTrace();
        }
        return "";
    }

    public static String getFormattedDate(String dateStr, String format) {
        String dt = "";  // Start date

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(dateStr));
            SimpleDateFormat nwsdf = new SimpleDateFormat(format);
            dt = nwsdf.format(c.getTime());
            return dt;
        } catch (Exception e) {
            e.printStackTrace();
            return dt;
        }
    }

    public static boolean isFutureDateWithMinutes(String datestr,int minutes) {
        try {
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat format = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
            Date date = format.parse(datestr);
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, minutes);
            Date date1 = calendar.getTime();
            Date current_date_time = new Date();
            current_date_time = format.parse(format.format(current_date_time));
            if (date1.after(current_date_time))
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isFutureDate(String datestr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
            Date date = format.parse(datestr);
            Date current_date_time = new Date();
            current_date_time = format.parse(format.format(current_date_time));
            if (date.after(current_date_time))
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isToday(String datestr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
            Date date = format.parse(datestr);
            Date current_date_time = new Date();
            current_date_time = format.parse(format.format(current_date_time));
            if (DateUtils.isSameDay(date,current_date_time))
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
