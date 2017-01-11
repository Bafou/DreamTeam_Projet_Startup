package com.dreamteam.pvviter.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by FlorianDoublet on 30/10/2016.
 */
public class DateManipulation {

    public static final Integer ELAPSED_DAYS = 1;
    public static final Integer ELAPSED_HOURS = 2;
    public static final Integer ELAPSED_MINUTES = 3;

    /**
     * Check if the Calendar given in param is tomorrow from the current date
     *
     * @param cal
     * @return True or False
     */
    public static Boolean isTomorrow(Calendar cal) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return cal.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH) && cal.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR);
    }

    public static Boolean isAfterTomorrow(Calendar cal) {

        Calendar afterTomorrow = Calendar.getInstance();
        afterTomorrow.add(Calendar.DAY_OF_MONTH, 2);
        diffBetweenTwoDate(afterTomorrow, cal);

        return cal.get(Calendar.DAY_OF_MONTH) >= afterTomorrow.get(Calendar.DAY_OF_MONTH) && cal.get(Calendar.MONTH) >= afterTomorrow.get(Calendar.MONTH) && cal.get(Calendar.YEAR) >= afterTomorrow.get(Calendar.YEAR);
    }

    /**
     * Concert a date into a string (only for hours and minutes)
     *
     * @param date
     * @return hh:mm string format
     */
    public static String dateHourMinuteToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.FRANCE);
        String date_s = null;
        try {
            date_s = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }

    /**
     * Gives a date to a string. Day number and month name
     *
     * @param date
     * @return The string format of a date.
     */
    public static String dayAndMonthToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM", Locale.FRANCE);
        String date_s = null;
        try {
            date_s = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }

    /**
     * Convert a hour to a string
     *
     * @param hour in double type
     * @return the string represent hours
     */
    public static String hourToStringHour(double hour) {
        Calendar calendar = hourToCalendar(hour);
        return dateHourMinuteToString(calendar.getTime());
    }

    /**
     * Transform an hour number into a Calendar object
     *
     * @param hour The hour number
     * @return The Calendar object
     */
    public static Calendar hourToCalendar(double hour) {
        int iHour = (int) hour;
        double dMin = (hour - iHour) * 60;
        int iMin = (int) dMin;
        double second = (dMin - iMin);
        if (second > 0.0) {
            iMin = iMin + 1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, iHour);
        calendar.set(Calendar.MINUTE, iMin);
        return calendar;
    }

    /**
     * Method to get the difference between two calendars.
     * The difference order is the first Cal minus the second Cal
     *
     * @param cal1 usually the older calendar
     * @param cal2 the other calendar to compare
     * @return a hashTable of the result
     */
    public static Hashtable<Integer, Integer> diffBetweenTwoDate(Calendar cal1, Calendar cal2) {

        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        Hashtable<Integer, Integer> hashTable = new Hashtable<>();
        Date cal1Date = cal1.getTime();
        Date cal2Date = cal2.getTime();

        //milliseconds
        long different = cal1Date.getTime() - cal2Date.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        int elapsedDays = (int) (different / daysInMilli);
        different = different % daysInMilli;

        int elapsedHours = (int) (different / hoursInMilli);
        different = different % hoursInMilli;

        int elapsedMinutes = (int) (different / minutesInMilli);
        different = different % minutesInMilli;

        hashTable.put(ELAPSED_DAYS, elapsedDays);
        hashTable.put(ELAPSED_HOURS, elapsedHours);
        hashTable.put(ELAPSED_MINUTES, elapsedMinutes);

        return hashTable;
    }

    public static String diffBetweenTwoDateTimeInString(Calendar cal1, Calendar cal2) {
        Boolean isNegativ = false;
        String timeDiff = "";
        Hashtable<Integer, Integer> res;
        if(cal1.before(cal2)){
            isNegativ = true;
            timeDiff += "-";
            res = DateManipulation.diffBetweenTwoDate(cal2, cal1);
        } else {
            res = DateManipulation.diffBetweenTwoDate(cal1, cal2);
        }

        int elapsedHours = res.get(DateManipulation.ELAPSED_HOURS);
        int elapsedMinutes = res.get(DateManipulation.ELAPSED_MINUTES);

        if (elapsedMinutes < 0 && elapsedHours <= 0) {
            elapsedHours -= 1;
        }

        Calendar calArrivingTime = Calendar.getInstance();
        //we set the millisecond and second to 0 for the next comparing of date
        calArrivingTime.set(Calendar.MILLISECOND, 0);
        calArrivingTime.set(Calendar.SECOND, 0);
        calArrivingTime.set(Calendar.HOUR_OF_DAY, elapsedHours);
        calArrivingTime.set(Calendar.MINUTE, elapsedMinutes);

        timeDiff += dateHourMinuteToString(calArrivingTime.getTime());

        return timeDiff;
    }
}
