package com.ahhf.chen.distask.util;

import java.util.Date;

public class DateUtil {

    public static int getIntervalDay(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return getIntervalDay(startTime, endTime);
    }

    public static int getIntervalDay(long startdate, long enddate) {
        long interval = enddate - startdate;
        int intervalday = (int) interval / (1000 * 60 * 60 * 24);
        return intervalday;
    }

    public static int getIntervalHour(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return getIntervalHour(startTime, endTime);
    }

    public static int getIntervalHour(long startdate, long enddate) {
        long interval = enddate - startdate;
        int intervalHour = (int) interval / (1000 * 60 * 60);
        return intervalHour;
    }

    public static int getIntervalMinute(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return getIntervalMinute(startTime, endTime);
    }

    public static int getIntervalMinute(long startdate, long enddate) {
        long interval = enddate - startdate;
        int intervalMinute = (int) interval / (1000 * 60);
        return intervalMinute;
    }
}
