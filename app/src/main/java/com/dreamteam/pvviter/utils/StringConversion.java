package com.dreamteam.pvviter.utils;


import org.osmdroid.util.GeoPoint;

/**
 * Created by remi on 02/11/2016.
 */

public class StringConversion {


    /**
     * Convert the length to string
     *
     * @param length in km
     * @return the string represent the length
     */
    public static String lengthToString(double length) {
        int iKm = (int) length;
        double dM = (length - iKm) * 1000;
        int iM = (int) dM;

        String sKm = iKm + " km ";
        String sM = iM + " m";
        if (iKm == 0) {
            sKm = "";
        }
        return sKm + sM;
    }

    /**
     * Convert a geoPoint into a String.
     *
     * @param geoPoint
     * @return if string with the format "latitude;longitude"
     */
    public static String geoPointToString(GeoPoint geoPoint) {
        return geoPoint.getLatitude() + ";" + geoPoint.getLongitude();
    }

    /**
     * Convert a String into a geoPoint
     *
     * @param data A string of the format "latidude;longitude"
     * @return
     */
    public static GeoPoint stringToGeoPoint(String data) {
        double latitude = Double.parseDouble(data.split(";")[0]);
        double longitude = Double.parseDouble(data.split(";")[1]);
        return new GeoPoint(latitude, longitude);
    }

}
