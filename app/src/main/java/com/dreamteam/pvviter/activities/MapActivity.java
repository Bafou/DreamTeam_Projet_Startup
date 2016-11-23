package com.dreamteam.pvviter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class MapActivity extends AppCompatActivity implements Locator.Listener {

    private MapView map;
    private Compass compass;
    private GeoPoint userLocation;
    private GeoPoint carLocation;

    //Needed to update the polyline
    private Thread updateThread = null;
    private Polyline pathOverlay = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_map);

        initMap();
        setupCompass();
        setupUserPositionHandler();

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

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateUserLocation();
            handler.postDelayed(this, 10000);
        }
    };

    private void setupUserPositionHandler() {
        //Create an handler to update the user location regularly.
        handler.postDelayed(runnable, 10000);
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
        Double time = MathCalcul.getTime(distance, Settings.SPEED);
        MapFunctions.drawRoute(map, road);

        long ms = Data_Storage.get_parking_end_time_in_milliseconds(getApplicationContext());
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(ms);
        Hashtable<Integer, Integer> dateDiff = DateManipulation.diffBetweenTwoDate(calendarEnd, Calendar.getInstance());
        String timeLeft = String.format("%02d", dateDiff.get(DateManipulation.ELAPSED_HOURS)) + "h" + String.format("%02d", dateDiff.get(DateManipulation.ELAPSED_MINUTES));

        this.addInfoOnMap(timeLeft, StringConversion.lengthToString(distance), DateManipulation.hourToString(time));
    }

    /**
     * Ask the locator for the new position of the user
     */
    private void updateUserLocation() {
        Locator loc = new Locator(this);
        loc.getLocation(Locator.Method.GPS, this);
        checkPointOfNoReturn();
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
     * Update the user Location with the new position and update the map information.
     *
     * @param location The new location
     */
    @Override
    public void onLocationFound(Location location) {
        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        Data_Storage.set_user_location(getApplicationContext(), userLocation);

        updateMapCursors();
    }

    /**
     * the location gps can't be found, a message is show
     */
    @Override
    public void onLocationNotFound() {
        Toast.makeText(this, R.string.user_location_not_found, Toast.LENGTH_SHORT).show();
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
        //user want change car park time
        if (id == R.id.action_change_time) {
            Intent intent = new Intent(this, TimeStampActivity.class);
            intent.putExtra("changeTimeMode", true);
            //use startActivityForResult for call onActivityResult when TimeStampActivity finish
            startActivityForResult(intent,1);
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
        moveTaskToBack(true);
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
}
