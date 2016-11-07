package com.dreamteam.pvviter;

import org.junit.Test;


import utils.StringConversion;

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
}
