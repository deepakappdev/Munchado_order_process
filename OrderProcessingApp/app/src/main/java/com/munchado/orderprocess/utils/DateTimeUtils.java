package com.munchado.orderprocess.utils;

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


    static Date getCurrentNewYorkTime() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String formattedStr = sdf.format(calendar.getTime());
            return new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss").parse(formattedStr);
        } catch(Exception x){
            return null;
        }
    }

    public static String getTimeAgo(String dateStr, SimpleDateFormat format) {
        try {
            long milliSeconds = format.parse(dateStr).getTime();
            long currentMilliSeconds = getCurrentNewYorkTime().getTime();
            long timeago = TimeUnit.MILLISECONDS.toMinutes(currentMilliSeconds - milliSeconds);
            if (timeago > 0 && timeago < 60)
                return timeago + " minutes ago";
            timeago = TimeUnit.MILLISECONDS.toSeconds(currentMilliSeconds - milliSeconds);
            if (timeago > 0 && timeago < 60)
                return timeago + " seconds ago";
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
}
