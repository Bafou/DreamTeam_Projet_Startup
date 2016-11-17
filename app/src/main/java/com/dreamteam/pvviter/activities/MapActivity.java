package com.dreamteam.pvviter.activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.pvviter.BuildConfig;
import com.dreamteam.pvviter.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import services.Compass;
import services.File_IO;
import services.Locator;
import services.PointOfNoReturnNotification;
import utils.Data_Storage;
import utils.DateManipulation;
import utils.StringConversion;
import utils.MapFunctions;
import utils.MathCalcul;
import utils.Settings;

public class MapActivity extends AppCompatActivity implements Locator.Listener{

    private MapView map;
    private Compass compass;
    private GeoPoint userLocation ;
    private GeoPoint carLocation ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_map);

        initMap();
        setupCompass();
        setupUserPositionHandler();

    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateUserLocation();
            handler.postDelayed(this, 10000);
        }
    };

    private void setupUserPositionHandler(){
        //Create an handler to update the user location regularly.
        handler.postDelayed(runnable, 10000);
    }



    /**
     * Add informations on the map view
     * @param distance the distance of the route
     * @param time the time for travel the route
     */
    private void addInfosOnMap(String timeLeft, String distance, String time){
        TextView time_car = (TextView) findViewById(R.id.time_car);
        time_car.setText(timeLeft);

        TextView distance_route = (TextView) findViewById(R.id.distance_route);
        distance_route.setText(distance);

        TextView time_route = (TextView) findViewById(R.id.time_route);
        time_route.setText(time);
    }

    /**
     * Initialize the map to it's default behavior
     */
    private void initMap() {
        /*
         * The user ID is set to prevent getting banned from the osm servers
         * It must be set to a unique application ID and not a user ID
         */
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(19);
        map.setClickable(false);

        IMapController mapController = map.getController();
        mapController.setZoom(17);

        //Starting position
        carLocation = Data_Storage.get_car_location(getApplicationContext());
        userLocation = Data_Storage.get_user_location(getApplicationContext());


        mapController.setCenter(userLocation);

        this.map = map;
        updateMapCursors();
    }

    /**
     * Clear the map and add the car, user location and then trace a route between then.
     * Route data are re-calculated too
     */
    private void updateMapCursors(){
        MapFunctions.clearMap(map);
        MapFunctions.addCurrentPositionPoint(map, userLocation);
        MapFunctions.addCarPoint(map, carLocation);

        //MapFunctions.drawRoute(map, startPoint, endPoint);
        Road road = MapFunctions.getRoad(map, userLocation, carLocation);
        Double distance = road.mLength;
        Double time = MathCalcul.getTime(distance, Settings.SPEED);
        MapFunctions.drawRoute(map, road);

        long ms = Data_Storage.get_parking_end_time_in_milliseconds(getApplicationContext());
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(ms);
        Hashtable<Integer, Integer> dateDiff =  DateManipulation.diffBetweenTwoDate(calendarEnd, Calendar.getInstance());
        String timeLeft = String.format("%02d", dateDiff.get(DateManipulation.ELAPSED_HOURS))+"h"+String.format("%02d", dateDiff.get(DateManipulation.ELAPSED_MINUTES));

        this.addInfosOnMap(timeLeft, StringConversion.lengthToString(distance), DateManipulation.hourToString(time));
    }

    /**
     * Ask the locator for the new position of the user
     */
    private void updateUserLocation(){
        Locator loc = new Locator(this);
        loc.getLocation(Locator.Method.GPS, this);
        checkPointOfNoReturn();
    }

    /**
     * Temporary method to check if the point of no return was reached
     * if yes, raise a notification
     */
    private void checkPointOfNoReturn(){
        Road road = MapFunctions.getRoad(map, userLocation, carLocation);
        Double distance = road.mLength;
        Double time = MathCalcul.getTime(distance, Settings.SPEED);
        Calendar calendarDistanceTime = DateManipulation.hourToCalendar(time);


        long ms = Data_Storage.get_parking_end_time_in_milliseconds(getApplicationContext());
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(ms);
        //we set the millisecond and second to 0 for the next comparing of date
        calendarEnd.set(Calendar.MILLISECOND, 0);
        calendarEnd.set(Calendar.SECOND, 0);


        int distanceTimeMin = calendarDistanceTime.get(Calendar.MINUTE);
        int distanceTimeHour = calendarDistanceTime.get(Calendar.HOUR_OF_DAY);

        Calendar calArrivingTime = Calendar.getInstance();
        //we set the millisecond and second to 0 for the next comparing of date
        calArrivingTime.set(Calendar.MILLISECOND, 0);
        calArrivingTime.set(Calendar.SECOND, 0);
        calArrivingTime.add(Calendar.HOUR_OF_DAY, distanceTimeHour);
        calArrivingTime.add(Calendar.MINUTE, distanceTimeMin);

        //if it's <= 0 it mean than calArrivingTime is higher or equals to the calendarEnd
        if(calendarEnd.getTime().compareTo(calArrivingTime.getTime()) == 0){
            new PointOfNoReturnNotification(getApplicationContext());
        }

    }

    /**
     * Update the user Location with the new position and update the map informations.
     *
     * @param location
     */
    @Override
    public void onLocationFound(Location location) {
        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        Data_Storage.set_user_location(getApplicationContext(), userLocation);

        updateMapCursors();
    }

    /**
     * the lcoation gps can't be found, a message is show
     */
    @Override
    public void onLocationNotFound() {
        Toast.makeText(this, R.string.user_location_not_found, Toast.LENGTH_SHORT);
    }


    /**
     * Initialize the orientation listener needed by the map to point in the director of the phone
     */
    private void setupCompass() {
        compass = new Compass(map);
        compass.startListeningToSensors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_reset) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.action_reset_title);
            alertDialogBuilder.setMessage(R.string.action_reset_message);
            alertDialogBuilder.setPositiveButton(R.string.action_reset_yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File_IO.delete_all_files(getApplicationContext());
                    handler.removeCallbacks(runnable);

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });

            alertDialogBuilder.setNegativeButton(R.string.action_reset_no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            alertDialogBuilder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
