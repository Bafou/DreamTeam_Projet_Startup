package utils;

import android.os.AsyncTask;
import android.util.Log;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * Asynchronous thread to calculate the road's path finding in the background
 */
public class AsynchronousRouting extends AsyncTask<RouteData, Void, Road> {

    private MapView map;
    private ArrayList<GeoPoint> wayPoints;
    private RoadManager roadManager;

    /**
     * Background process to calculate the road's path finding
     *
     * @param params The RouteData object containing everything needed to execute the path finding
     * @return The calculated road
     */
    @Override
    protected Road doInBackground(RouteData... params) {
        map = params[0].map;
        wayPoints = params[0].wayPoints;
        roadManager = params[0].roadManager;

        Road road = roadManager.getRoad(wayPoints);

        return road;
    }
}