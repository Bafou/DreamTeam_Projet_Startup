package com.dreamteam.pvviter;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.dreamteam.pvviter.services.FileIO;
import com.dreamteam.pvviter.utils.Data_Storage;

import org.junit.Test;
import org.osmdroid.util.GeoPoint;

/**
 * @author Lucas Delvallet https://github.com/LucasDelvallet
 */
public class Data_Storage_Test extends ApplicationTestCase<Application> {
    public Data_Storage_Test() {
        super(Application.class);
    }

    @Test
    public void test_get_set_user_location() {
        final double DELTA = 1e-15;

        GeoPoint geoPoint = new GeoPoint(9.3, 3.11);
        Data_Storage.set_user_location(mContext, geoPoint);
        GeoPoint retrievedGeoPoint = Data_Storage.get_user_location(mContext);

        assertEquals(geoPoint.getLatitude(), retrievedGeoPoint.getLatitude(), DELTA);
        assertEquals(geoPoint.getLongitude(), retrievedGeoPoint.getLongitude(), DELTA);
    }

    @Test
    public void test_get_set_car_location() {
        final double DELTA = 1e-15;

        GeoPoint geoPoint = new GeoPoint(9.3, 3.11);
        Data_Storage.set_car_location(mContext, geoPoint);
        GeoPoint retrievedGeoPoint = Data_Storage.get_car_location(mContext);

        assertEquals(geoPoint.getLatitude(), retrievedGeoPoint.getLatitude(), DELTA);
        assertEquals(geoPoint.getLongitude(), retrievedGeoPoint.getLongitude(), DELTA);
    }

    @Test
    public void test_get_set_parking_end_time() {
        long endTime = 21654956195576l;
        Data_Storage.set_parking_end_time_in_milliseconds(mContext, endTime);
        long retrievedEndTime = Data_Storage.get_parking_end_time_in_milliseconds(mContext);

        assertEquals(endTime, retrievedEndTime);
    }

    @Test
    public void test_file_deletion() {
        Data_Storage.set_parking_end_time_in_milliseconds(mContext, 123);
        Data_Storage.set_car_location(mContext, new GeoPoint(9.3, 3.11));
        Data_Storage.set_user_location(mContext, new GeoPoint(9.3, 3.11));

        FileIO.delete_all_files(mContext);

        assertFalse(FileIO.does_file_exist(mContext, FileIO.PARKING_END_TIME));
        assertFalse(FileIO.does_file_exist(mContext, FileIO.CAR_LOCATION_FILE));
        assertFalse(FileIO.does_file_exist(mContext, FileIO.USER_LOCATION_FILE));
    }
}
