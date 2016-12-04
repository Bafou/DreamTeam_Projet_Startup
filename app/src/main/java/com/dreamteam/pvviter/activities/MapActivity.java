package com.dreamteam.pvviter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.pvviter.BuildConfig;
import com.dreamteam.pvviter.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;

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

public class MapActivity extends AppCompatActivity {

    private MapView map;
    private Compass compass;
    private GeoPoint userLocation;
    private GeoPoint carLocation;
    private Locator locator;

    //Needed to update the polyline
    private Thread updateThread = null;
    private Polyline pathOverlay = null;

    private Thread threadUI = null;
    private boolean closingActivity = false;

    private String previousActivityName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_map);

        setTitle(R.string.activity_map_title);

        Intent intent = getIntent();
        previousActivityName = intent.getStringExtra("activity");


        initMap();
        setupCompass();
        setupLocator();
        initUpdateUIThread();

        /* Path disappearing when zoomed in correction */
        map.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                drawOptimizedRoute(MapActivity.this);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                drawOptimizedRoute(MapActivity.this);
                return false;
            }
        });
    }

    /**
     * Setup the locator, so the use location is updated regurlarly
     */
    public void setupLocator(){
        locator = new Locator(this){
            @Override
            public void onLocationChanged(Location location) {
                updateGPSCoordinates();
                Data_Storage.set_user_location(MapActivity.this, new GeoPoint(location.getLatitude(), location.getLongitude()));
                userLocation = Data_Storage.get_user_location(MapActivity.this);
                updateMapCursors();
            }
        };
    }

    /**
     * Add information on the map view
     *
     * @param distance the distance of the route
     * @param time     the time for travel the route
     */
    private void addInfoOnMap(String timeLeft, String distance, String time) {
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
        TextView osm_copyright_text = (TextView) findViewById(R.id.OSM_Copyright_Text);
        osm_copyright_text.setMovementMethod(LinkMovementMethod.getInstance());

        /*
         * The user ID is set to prevent getting banned from the osm servers
         * It must be set to a unique application ID and not a user ID
         */
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(18);
        map.setMinZoomLevel(6);
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
    private void updateMapCursors() {
        MapFunctions.clearMap(map);
        MapFunctions.addCurrentPositionPoint(map, userLocation);
        MapFunctions.addCarPoint(map, carLocation);

        //MapFunctions.drawRoute(map, startPoint, endPoint);
        Road road = MapFunctions.getRoad(map, userLocation, carLocation);
        Double distance = road.mLength;
        if(distance<=Settings.CAR_FOUND_DISTANCE){
            this.showFAB(true);
        }else{
            this.showFAB(false);
        }
        Double time = MathCalcul.getTime(distance, Settings.SPEED);
        MapFunctions.drawRoute(map, road);

        long ms = Data_Storage.get_parking_end_time_in_milliseconds(getApplicationContext());
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(ms);
        Hashtable<Integer, Integer> dateDiff = DateManipulation.diffBetweenTwoDate(calendarEnd, Calendar.getInstance());
        String timeLeft =  String.format("%02d", dateDiff.get(DateManipulation.ELAPSED_HOURS)) + "h" + String.format("%02d", dateDiff.get(DateManipulation.ELAPSED_MINUTES));

        String routeTime =  DateManipulation.hourToStringHour(time);
        routeTime = routeTime.replace(':', 'h');
        if (time > 24)
            routeTime = (int)(time/24) + "j" + routeTime;
        if (dateDiff.get(DateManipulation.ELAPSED_DAYS) > 0)  //Adds the days left when it's a very long walk
            timeLeft = dateDiff.get(DateManipulation.ELAPSED_DAYS) + "j" + timeLeft;

        this.addInfoOnMap(timeLeft, StringConversion.lengthToString(distance), routeTime);
    }

    /**
     * Temporary method to check if the point of no return was reached
     * if yes, raise a notification
     */
    private void checkPointOfNoReturn() {
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
        if (calendarEnd.getTime().compareTo(calArrivingTime.getTime()) == 0) {
            new PointOfNoReturnNotification(getApplicationContext());
        }

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
        if (id == R.id.action_credit) {
            Intent credit = new Intent(this, CreditActivity.class);
            startActivity(credit);
        //user want change car park time
        }
        if (id == R.id.action_change_time) {
            Intent intent = new Intent(this, TimeStampActivity.class);
            intent.putExtra("changeTimeMode", true);
            //use startActivityForResult for call onActivityResult when TimeStampActivity finish
            intent.putExtra("activity",getString(R.string.title_activity_map));
            startActivityForResult(intent,1);
        }
        if (id == R.id.action_reset) {

            String title = getString(R.string.action_reset_title);
            String message = getString(R.string.action_reset_message);
            String positiveButton = getString(R.string.positive_button_alert_dialog);
            String negativeButton = getString(R.string.negative_button_alert_dialog);
            AlertDialog.Builder alertDialog = resetOfDataAlertDialog(title, message, positiveButton, negativeButton);
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //update the map when TimeStampActivity finish
                updateMapCursors();
            }
        }
    }

    @Override
    public void onBackPressed() {
        String title = getString(R.string.title_alert_dialog_exit_application);
        String message = getString(R.string.message_alert_dialog_exit_application);
        String positiveButton = getString(R.string.positive_button_alert_dialog);
        String negativeButton = getString(R.string.negative_button_alert_dialog);
        AlertDialog.Builder alertDialog;

        if(previousActivityName != null){
            if(previousActivityName.equals(getString(R.string.title_activity_time_stamp))){
                title = getString(R.string.title_alert_dialog_back_to_timestamp);
                message = getString(R.string.message_alert_dialog_back_to_timestamp);
            }
            alertDialog = previousActivityAlertDialog(title,message,positiveButton,negativeButton);
        } else {
            alertDialog = moveTaskToBackAlertDialog(title,message,positiveButton,negativeButton);
        }
        alertDialog.show();

    }

    /**
     * Build an AlertDialog when we want to come back to the previous activity
     * @param title of the alert
     * @param message of the alert
     * @param positiveButton of the alert
     * @param negativeButton of the alert
     * @return
     */
    private AlertDialog.Builder previousActivityAlertDialog(String title, String message, String positiveButton, String negativeButton){
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locator.stopUsingGPS();
                        closingActivity = true;
                        finish();
                    }
                })
                .setNegativeButton(negativeButton, null);
    }

    /**
     * Build an AlertDialog when we want to move the task back
     * @param title of the alert
     * @param message of the alert
     * @param positiveButton of the alert
     * @param negativeButton of the alert
     * @return
     */
    private AlertDialog.Builder moveTaskToBackAlertDialog(String title, String message, String positiveButton, String negativeButton){
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton(negativeButton, null);
    }

    /**
     * Build an AlertDialog when we want to reset the data and go back to the startActivity
     * @param title of the alert
     * @param message of the alert
     * @param positiveButton of the alert
     * @param negativeButton of the alert
     * @return
     */
    private AlertDialog.Builder resetOfDataAlertDialog(String title, String message, String positiveButton, String negativeButton){
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locator.stopUsingGPS();
                        closingActivity = true;
                        File_IO.delete_all_files(getApplicationContext());
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                })
                .setNegativeButton(negativeButton, null);
    }

    /**
     * Calculate and draw a new route to upgrade performance and solve rendering problems
     *
     * @param context The activity context
     */
    public void drawOptimizedRoute(final Context context) {
        if (updateThread == null || !updateThread.isAlive()) {
            updateRoute(context);
        }
    }

    /**
     * Create a Thread that update the UI every 10 seconds
     */
    private void initUpdateUIThread(){
        threadUI = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted() && !closingActivity) {
                        Thread.sleep(10000);
                        if(!closingActivity){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateMapCursors();
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        threadUI.start();
    }

    /**
     * Update the road on map with the new calculated polyline
     *
     * @param context The activity context
     */
    private void updateRoute(final Context context) {
        updateThread = new Thread(new Runnable() {
            public void run() {
                final ArrayList<GeoPoint> zoomedPoints = new ArrayList<>(((Polyline) map.getOverlays().get(0)).getPoints());

                //Remove points that are offscreen
                removeHiddenPoints(zoomedPoints);

                //Update the map on thread
                map.post(new Runnable() {
                    public void run() {
                        map.getOverlays().remove(pathOverlay);
                        pathOverlay = new Polyline(context);
                        pathOverlay.setPoints(zoomedPoints);
                        pathOverlay.setColor(MapFunctions.ROUTE_COLOR);
                        map.getOverlays().add(pathOverlay);
                        map.invalidate();
                    }
                });
            }
        });
        updateThread.start();
    }

    /**
     * This functions removes any point that is outside the visual bounds of the map view
     *
     * @param zoomedPoints The list of GeoPoints to process
     */
    private void removeHiddenPoints(ArrayList<GeoPoint> zoomedPoints) {
        BoundingBox bounds = map.getBoundingBox();

        for (Iterator<GeoPoint> iterator = zoomedPoints.iterator(); iterator.hasNext(); ) {
            GeoPoint point = iterator.next();

            boolean inLongitude = point.getLatitude() < (bounds.getLatNorth() + 0.005) && (point.getLatitude() + 0.005) > bounds.getLatSouth();
            boolean inLatitude = (point.getLongitude() + 0.005) > bounds.getLonWest() && point.getLongitude() < (bounds.getLonEast() + 0.005);
            if (!inLongitude || !inLatitude) {
                iterator.remove();
            }
        }
    }

    /**
     * show or hide a floating android button
     * @param show true for show the button, false for hide it
     */
    private void showFAB(boolean show){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(show){
            fab.show();
        }else{
            fab.hide();
        }
    }

    /**
     * show dialog for reset data
     * @param view
     */
    public void fabClicked(View view){
        String title = getString(R.string.action_reset_title2);
        String positiveButton = getString(R.string.positive_button_alert_dialog);
        String negativeButton = getString(R.string.negative_button_alert_dialog);
        AlertDialog.Builder alertDialog = resetOfDataAlertDialog(title, "", positiveButton, negativeButton);
        alertDialog.show();
    }
}
