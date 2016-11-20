package com.dreamteam.pvviter;

import junit.framework.Assert;

import org.junit.Test;
import org.osmdroid.util.GeoPoint;


import dalvik.annotation.TestTargetClass;
import utils.StringConversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by remikruczek on 04/11/2016.
 */

public class StringConversionTest {

    @Test
    public void lengthToStringTest() {
        double length = 0.750;
        assertTrue(StringConversion.lengthToString(length).equals("750 m"));
        length = 1.51;
        assertTrue(StringConversion.lengthToString(length).equals("1 km 510 m"));
    }

    @Test
    public void geoPointToStringTest(){
        GeoPoint geoPoint = new GeoPoint(9.3, 3.11);
        assertEquals("9.3;3.11", StringConversion.geoPointToString(geoPoint));
    }

    @Test
    public void stringToGeoPoint_WithCorrectStringTest(){
        final double DELTA = 1e-15;

        GeoPoint geoPoint = StringConversion.stringToGeoPoint("9.3;3.11");
        assertEquals(9.3, geoPoint.getLatitude(), DELTA);
        assertEquals(3.11, geoPoint.getLongitude(), DELTA);
    }

    @Test
         public void stringToGeoPoint_WithIncorrectStringSizeTest(){
        try
        {
            GeoPoint geoPoint = StringConversion.stringToGeoPoint("9.3");
            Assert.fail("Should have thrown ArrayIndexOutOfBoundsException");
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            //success
        }
    }

    @Test
    public void stringToGeoPoint_WithIncorrectStringFormatTest(){
        try
        {
            GeoPoint geoPoint = StringConversion.stringToGeoPoint("Truite;Lampadaire");
            Assert.fail("Should have thrown NumberFormatException");
        }
        catch(NumberFormatException e)
        {
            //success
        }
    }
}
