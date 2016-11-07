package com.dreamteam.pvviter;

import org.junit.Test;

import utils.MathCalcul;

import static org.junit.Assert.*;

/**
 * Created by RemiKruczek on 04/11/2016.
 */

public class MathCalculTest {

    @Test

    public void getTimeTest() {
        double speed=10.0;
        double distance=50.0;
        double resultat = MathCalcul.getTime(distance, speed);
        assertTrue(resultat==5.0);
    }

}
