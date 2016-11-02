package utils;

/**
 * Created by remi on 02/11/2016.
 */

public class Convertion {

    /**
     * Convert the time in string
     * @param time time in hours
     * @return the string represent the time
     */
    public static String timeInString(double time){
        int hours = (int) time;
        double dMin= (time-hours)*60;
        int min = (int) dMin;
        double minArrond = (dMin-min);
        if(minArrond>=0.5){
            min=min+1;
        }

        String sHours =  hours+" h ";
        String sMin=  min+" min";
        if(hours==0){
            sHours="";
        }
        return sHours+sMin;
    }

    /**
     * Convert the length to string
     * @param length in km
     * @return the string represent the length
     */
    public static String lengthToString(double length){
        int iKm = (int) length;
        double dM= (length-iKm)*1000;
        int iM = (int) dM;

        String sKm =  iKm+" km ";
        String sM=  iM+" m";
        if(iKm==0){
            sKm="";
        }
        return sKm+sM;
    }

}
