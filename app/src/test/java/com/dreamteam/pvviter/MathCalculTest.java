package com.dreamteam.pvviter;

import com.dreamteam.pvviter.utils.MathCalcul;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by RemiKruczek on 04/11/2016.
 */

public class MathCalculTest {

    @Test

    public void getTimeTest() {
        double speed = 10.0;
        double distance = 50.0;
        double resultat = MathCalcul.getTime(distance, speed);
        assertTrue(resultat == 5.0);
    }

}
