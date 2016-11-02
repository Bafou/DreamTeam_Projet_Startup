package com.dreamteam.pvviter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dreamteam.pvviter.BuildConfig;
import com.dreamteam.pvviter.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import services.Compass;
import utils.Convertion;
import utils.MapFunctions;
import utils.MathCalcul;
import utils.Settings;

public class MapActivity extends AppCompatActivity{

    private MapView map;
    private Compass compass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_map);

        initMap();
        setupCompass();
    }

    /**
     * Add informations on the map view
     * @param distance the distance of the route
     * @param time the time for travel the route
     */
    private void addInfosOnMap(String distance, String time){
        TextView infosMap = (TextView) findViewById(R.id.infosMap);
        infosMap.setText("Distance : " + distance +" , Temps : " + time);
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
        GeoPoint startPoint = new GeoPoint(50.633333, 3.066667); //Lille, France
        GeoPoint endPoint = new GeoPoint(50.636333, 3.069647); //Still Lille, France

        mapController.setCenter(startPoint);

        MapFunctions.addCurrentPositionPoint(map, startPoint);
        MapFunctions.addCarPoint(map, endPoint);

        //MapFunctions.drawRoute(map, startPoint, endPoint);
        Road road = MapFunctions.getRoad(map, startPoint, endPoint );
        Double distance = road.mLength;
        Double time = MathCalcul.getTime(distance, Settings.SPEED);
        MapFunctions.drawRoute(map, road );

        this.addInfosOnMap(Convertion.lengthToString(distance), Convertion.timeInString(time));

        this.map = map;


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
