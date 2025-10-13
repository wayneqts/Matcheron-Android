package com.appmonarchy.matcheron.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class FmTimeAgo {
    public String covertTimeToText(String dataDate) {
        String convTime = null;
        String suffix = "ago";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);;
            dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
            Date pasTime = dateFormat.parse(dataDate);
            Date nowTime = new Date();
            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (second < 60) {
                convTime = "a few seconds " + suffix;
            } else if (minute < 60) {
                if (minute == 1) {
                    convTime = minute + " minute " + suffix;
                } else {
                    convTime = minute + " minutes " + suffix;
                }
            } else if (hour < 24) {
                if (hour == 1) {
                    convTime = hour + " hour " + suffix;
                } else {
                    convTime = hour + " hours " + suffix;
                }
            } else if (day >= 7) {
                if (day < 30) {
                    switch ((int) day) {
                        case 7:
                            convTime = "1 week " + suffix;
                            break;
                        case 14:
                            convTime = "2 weeks " + suffix;
                            break;
                        case 21:
                            convTime = "3 weeks " + suffix;
                            break;
                        case 28:
                            convTime = "4 weeks " + suffix;
                            break;
                        default:
                            convTime = day + " days " + suffix;
                            break;
                    }
                } else if (day >= 360) {
                    if (day / 360 == 1) {
                        convTime = (day / 360) + " year " + suffix;
                    } else {
                        convTime = (day / 360) + " years " + suffix;
                    }
                } else {
                    if (day / 30 == 1) {
                        convTime = (day / 30) + " month " + suffix;
                    } else {
                        convTime = (day / 30) + " months " + suffix;
                    }
                }
            } else {
                convTime = day + " days " + suffix;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("ConvTimeE: " + e.getMessage());
        }
        return convTime;
    }
}
