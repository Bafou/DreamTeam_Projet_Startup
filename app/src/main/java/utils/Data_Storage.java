package utils;

import android.content.Context;

import org.osmdroid.util.GeoPoint;

import services.File_IO;

/**
 * This class is for saving and loading data in/from private file.
 * @author Lucas Delvallet https://github.com/LucasDelvallet
 */
public class Data_Storage {

    /**
     * Load the user location from the private file that it have been save to.
     * @param context
     * @return
     */
    public static GeoPoint get_user_location(Context context){
        return StringConversion.stringToGeoPoint(File_IO.load_from_file(context, File_IO.USER_LOCATION_FILE));
    }

    /**
     * Save the user location in a specific private file.
     * @param context
     * @param geoPoint
     */
    public static void set_user_location(Context context, GeoPoint geoPoint){
        File_IO.save_to_file(context, File_IO.USER_LOCATION_FILE, StringConversion.geoPointToString(geoPoint));
    }

    /**
     * Load the car location from the private file that it have been save to.
     * @param context
     * @return
     */
    public static GeoPoint get_car_location(Context context){
        return StringConversion.stringToGeoPoint(File_IO.load_from_file(context, File_IO.CAR_LOCATION_FILE));
    }

    /**
     * Save the car location in a specific private file.
     * @param context
     * @param geoPoint
     */
    public static void set_car_location(Context context, GeoPoint geoPoint){
        File_IO.save_to_file(context, File_IO.CAR_LOCATION_FILE, StringConversion.geoPointToString(geoPoint));
    }

    /**
     * Load the parking end time from the private file that it have been save to.
     * @param context
     * @return the parking end time in milliseconds
     */
    public static long get_parking_end_time_in_milliseconds(Context context){
        return Long.parseLong(File_IO.load_from_file(context, File_IO.PARKING_END_TIME));
    }

    /**
     * Save the parking end time in a specific private file, must be in milliseconds
     * @param context
     * @param time
     */
    public static void set_parking_end_time_in_milliseconds(Context context, long time){
        File_IO.save_to_file(context, File_IO.PARKING_END_TIME, String.valueOf(time));
    }
}
