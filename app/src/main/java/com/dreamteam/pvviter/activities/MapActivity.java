package com.dreamteam.pvviter.activities;

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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Date;

import services.Compass;
import services.Locator;
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

    private void setupUserPositionHandler(){
        //Create an handler to update the user location regularly.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUserLocation();
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }



    /**
     * Add informations on the map view
     * @param distance the distance of the route
     * @param time the time for travel the route
     */
    private void addInfosOnMap(String distance, String time){
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

        //Default location
        userLocation = new GeoPoint(50.633333, 3.066667); //Lille, France
        carLocation = new GeoPoint(50.636333, 3.069647); //Still Lille, France

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
        Road road = MapFunctions.getRoad(map, userLocation, carLocation );
        Double distance = road.mLength;
        Double time = MathCalcul.getTime(distance, Settings.SPEED);
        MapFunctions.drawRoute(map, road);

        this.addInfosOnMap(StringConversion.lengthToString(distance), DateManipulation.hourToString(time));
    }

    /**
     * Ask the locator for the new position of the user
     */
    private void updateUserLocation(){
        Locator loc = new Locator(this);
        loc.getLocation(Locator.Method.GPS, this);
    }

    /**
     * Update the user Location with the new position and update the map informations.
     *
     * @param location
     */
    @Override
    public void onLocationFound(Location location) {
        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
