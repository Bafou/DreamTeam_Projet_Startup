package utils;

import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * Structure that contains all the information the AsynchronousRouting needs to calculate the road
 */
public class RouteData{
    public MapView map;
    public ArrayList<GeoPoint> wayPoints;
    public RoadManager roadManager;

    public RouteData(MapView map, ArrayList<GeoPoint> wayPoints, RoadManager roadManager) {
        this.map = map;
        this.wayPoints = wayPoints;
        this.roadManager = roadManager;
    }
}
