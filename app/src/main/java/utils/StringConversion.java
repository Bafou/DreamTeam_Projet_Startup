package utils;


/**
 * Created by remi on 02/11/2016.
 */

public class StringConversion {


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
