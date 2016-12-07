package com.dreamteam.pvviter.utils;

/**
 * Created by Remi on 02/11/2016.
 */

public class MathCalcul {

    /**
     * Return the time for travel the distance with the indicate speed
     *
     * @param distance in km
     * @param speed    in km/h
     * @return the time in hours
     */
    public static double getTime(double distance, double speed) {
        double time = distance / speed;
        return time;
    }
}
