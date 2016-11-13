package com.dreamteam.pvviter;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Hashtable;

import utils.DateManipulation;

/**
 * Created by FlorianDoublet on 30/10/2016.
 */
public class DateManipulationTest {

    @Test
    public void isTomorrowTest() throws Exception{
        //create a calendar to current date and add 1 day
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        //then check
        assertTrue(DateManipulation.isTomorrow(calendar));
    }

    @Test
    public void dateHourMinuteToString() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 2);
        String expectedString = "14:02";
        assertEquals(expectedString, DateManipulation.dateHourMinuteToString(calendar.getTime()));
    }

    @Test
    public void hourToCalendar() {
        double hours = 0.5; //30 min in hour
        assertTrue(DateManipulation.hourToCalendar(hours).get(Calendar.MINUTE) == 30);
        hours = 1.2;
        assertTrue(DateManipulation.hourToCalendar(hours).get(Calendar.HOUR_OF_DAY) == 1 &&
                DateManipulation.hourToCalendar(hours).get(Calendar.MINUTE) == 12);
        hours = 0.83; //49.8 min have to return  50 min
        assertTrue(DateManipulation.hourToCalendar(hours).get(Calendar.MINUTE) == 50);
        hours = 0.79; //47.4 min function have to return 48 min
        assertTrue(DateManipulation.hourToCalendar(hours).get(Calendar.MINUTE) == 48);
    }


    @Test
    public void diffBetweenTwoDate() throws Exception{
        Calendar oldCal = Calendar.getInstance();
        oldCal.set(Calendar.DAY_OF_MONTH, 2);
        oldCal.set(Calendar.HOUR_OF_DAY, 2);
        oldCal.set(Calendar.MINUTE, 2);

        //set a call with the new parameters for hours and minutes
        Calendar newCal = Calendar.getInstance();
        newCal.set(Calendar.DAY_OF_MONTH, 5);
        newCal.set(Calendar.HOUR_OF_DAY, 6);
        newCal.set(Calendar.MINUTE, 7);

        //calculus the difference and set the result into the variable above
        Hashtable<Integer, Integer> res = DateManipulation.diffBetweenTwoDate(newCal, oldCal);

        int elapsedDays = res.get(DateManipulation.ELAPSED_DAYS);
        int elapsedHours = res.get(DateManipulation.ELAPSED_HOURS);
        int elapsedMinutes = res.get(DateManipulation.ELAPSED_MINUTES);

        int[] expectedValues = {3, 4, 5};
        int[] obtainedValue = {elapsedDays, elapsedHours, elapsedMinutes};

        assertArrayEquals(expectedValues, obtainedValue);

    }
}
