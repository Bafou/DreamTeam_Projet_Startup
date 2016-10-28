package utils;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.dreamteam.pvviter.R;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

/**
 * Functions to manipulate a given map's components, like adding points and tracing routes.
 */
public class MapFunctions {

    /**
     * Overload method. See addPoint(MapView map, GeoPoint point).
     *
     * @param map
     * @param latitude The latitude of the point to draw
     * @param longitude The longitude of the point to draw
     * @return The marker that has been pinned to the map
     */
    public static Marker addPoint(MapView map, double latitude, double longitude) {
        return addPoint(map, new GeoPoint(latitude, longitude));
    }

    /**
     * Draw a point on the given map
     *
     * @param map The map where the point must be drawn
     * @param point The location of the point to draw
     * @return The marker that has been pinned to the map
     */
    public static Marker addPoint(MapView map, GeoPoint point) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ContextCompat.getDrawable(map.getContext(), R.drawable.marker));

        map.getOverlays().add(marker);

        refreshMap(map);

        return marker;
    }

    /**
     * Overload method. See drawRoute(MapView map, ArrayList<GeoPoint> wayPoints).
     *
     * @param map The map receiving the route drawing
     * @param startPoint The point where the route starts
     * @param endPoint The point where the route ends
     * @return The drawn route
     */
    public static Polyline drawRoute(MapView map, GeoPoint startPoint, GeoPoint endPoint) {
        ArrayList<GeoPoint> wayPoints = new ArrayList<>();
        wayPoints.add(startPoint);
        wayPoints.add(endPoint);
        return drawRoute(map, wayPoints);
    }

    /**
     * Draw a route between givens points.
     * It calls an asynchronous task to calculate the road's path finding.
     * Needs an internet connection.
     *
     * @param map The map receiving the route drawing
     * @param wayPoints Points where the route must go through. Must contains at least two points.
     * @return The drawn route
     */
    public static Polyline drawRoute(MapView map, ArrayList<GeoPoint> wayPoints) {
        RoadManager roadManager = new MapQuestRoadManager("VdihCkmLUABBjWu0LgVvCK7Bwi6tSmUS"); //Free key from Map Quest
        //roadManager.addRequestOption("routeType=pedestrian"); //Doesn't work

        RouteData routeData = new RouteData(map, wayPoints, roadManager);
        Road road = null;

        try {
            road = new AsynchronousRouting().execute(routeData).get();
        } catch(Exception e) {
            e.printStackTrace();
        }

        //if (road == null) return new Polyline();

        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.BLUE, 10);

        map.getOverlays().add(roadOverlay);

        refreshMap(map);

        return roadOverlay;
    }

    /**
     * Refresh the map to show the newly drawn elements.
     *
     * @param map The map to refresh
     */
    private static void refreshMap(MapView map) {
        map.invalidate();
    }

    /**
     * Clear all drawn elements on the map
     *
     * @param map The map to clear
     */
    public static void clearMap(MapView map) {
        map.getOverlays().clear();
    }

    /**
     * Removes a drawn route.
     *
     * @param map The concerned map
     * @param route The route to remove
     */
    public static void removeRoute(MapView map, Polyline route) {
        map.getOverlays().remove(route);
        refreshMap(map);
    }

    /**
     * Removes a drawn point.
     *
     * @param map The concerned map
     * @param marker The point to remove
     */
    public static void removeMarker(MapView map, Marker marker) {
        map.getOverlays().remove(marker);
        refreshMap(map);
    }
}