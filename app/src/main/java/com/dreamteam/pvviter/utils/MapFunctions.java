package com.dreamteam.pvviter.utils;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.dreamteam.pvviter.R;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Functions to manipulate a given map's components, like adding points and tracing routes.
 */
public class MapFunctions {

    public static Marker userMarker;
    public final static int ROUTE_COLOR = Color.CYAN;

    /**
     * Overload method. See addCarPoint(MapView map, GeoPoint point).
     *
     * @param map       The map where the point must be drawn
     * @param latitude  The latitude of the point to draw
     * @param longitude The longitude of the point to draw
     * @return The marker that has been pinned to the map
     */
    public static Marker addCarPoint(MapView map, double latitude, double longitude) {
        return addCarPoint(map, new GeoPoint(latitude, longitude));
    }

    /**
     * Draw a point on the given map
     *
     * @param map   The map where the point must be drawn
     * @param point The location of the point to draw
     * @return The marker that has been pinned to the map
     */
    public static Marker addCarPoint(MapView map, GeoPoint point) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(ContextCompat.getDrawable(map.getContext(), R.drawable.car_marker));

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });

        map.getOverlays().add(marker);

        refreshMap(map);

        return marker;
    }

    /**
     * Overload method. See addCurrentPositionPoint(MapView map, GeoPoint point).
     *
     * @param map       The map to work with
     * @param latitude  The latitude of the point to draw
     * @param longitude The longitude of the point to draw
     * @return The marker that has been pinned to the map
     */
    public static Marker addCurrentPositionPoint(MapView map, double latitude, double longitude, float angle) {
        return addCurrentPositionPoint(map, new GeoPoint(latitude, longitude), angle);
    }

    /**
     * Draw a point on the given map
     *
     * @param map   The map where the point must be drawn
     * @param point The location of the point to draw
     * @return The marker that has been pinned to the map
     */
    public static Marker addCurrentPositionPoint(MapView map, GeoPoint point, float angle) {
        userMarker = new Marker(map);

        userMarker.setPosition(point);
        userMarker.setRotation(angle);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        userMarker.setIcon(ContextCompat.getDrawable(map.getContext(), R.drawable.user_marker));

        userMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });

        map.getOverlays().add(userMarker);

        refreshMap(map);

        return userMarker;
    }

    /**
     * Overload method. See drawRoute(MapView map, ArrayList<GeoPoint> wayPoints).
     *
     * @param map        The map receiving the route drawing
     * @param startPoint The point where the route starts
     * @param endPoint   The point where the route ends
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
     * @param map       The map receiving the route drawing
     * @param wayPoints Points where the route must go through. Must contains at least two points.
     * @return The drawn route
     */
    public static Polyline drawRoute(MapView map, ArrayList<GeoPoint> wayPoints) {
        RoadManager roadManager = new OSRMRoadManager(map.getContext());

        RouteData routeData = new RouteData(map, wayPoints, roadManager);
        Road road = null;

        try {
            road = new AsynchronousRouting().execute(routeData).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, ROUTE_COLOR, 10);

        map.getOverlays().add(0, roadOverlay);

        refreshMap(map);

        return roadOverlay;
    }

    /**
     * Draw a route with a Road object
     *
     * @param map  map where draw the route
     * @param road the route
     * @return the drawn route
     */
    public static Polyline drawRoute(MapView map, Road road) {
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, ROUTE_COLOR, 10);
        map.getOverlays().add(0, roadOverlay);
        refreshMap(map);
        return roadOverlay;
    }

    /**
     * Get a road with the start and the end point
     *
     * @param map        the map of the road
     * @param startPoint The starting point of the road
     * @param endPoint   The ending point of the road
     * @return the road
     */
    public static Road getRoad(MapView map, GeoPoint startPoint, GeoPoint endPoint) {
        ArrayList<GeoPoint> wayPoints = new ArrayList<>();
        wayPoints.add(startPoint);
        wayPoints.add(endPoint);

        RoadManager roadManager = new OSRMRoadManager(map.getContext());

        RouteData routeData = new RouteData(map, wayPoints, roadManager);
        Road road = null;

        try {
            road = new AsynchronousRouting().execute(routeData).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return road;
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
     * @param map   The concerned map
     * @param route The route to remove
     */
    public static void removeRoute(MapView map, Polyline route) {
        map.getOverlays().remove(route);
        refreshMap(map);
    }

    /**
     * Removes every route on the map
     *
     * @param map The concerned map
     */
    public static void removeAllRoutes(MapView map) {
        for (Overlay o : map.getOverlays()) {
            if (o instanceof Polyline) map.getOverlays().remove(o);
        }

        refreshMap(map);
    }

    /**
     * Removes a drawn point.
     *
     * @param map    The concerned map
     * @param marker The point to remove
     */
    public static void removeMarker(MapView map, Marker marker) {
        map.getOverlays().remove(marker);
        refreshMap(map);
    }

    /**
     * Removes every marker on the map
     *
     * @param map The concerned map
     */
    public static void removeAllMarkers(MapView map) {
        for (Overlay o : map.getOverlays()) {
            if (o instanceof Marker) map.getOverlays().remove(o);
        }

        refreshMap(map);
    }

    public static void changeUserRotation(MapView map, float angle) {
        List<Overlay> list = map.getOverlays();
        list.remove(userMarker);  //Put the marker in front of the road path

        userMarker.setRotation(angle);

        list.add(userMarker);

        refreshMap(map);
    }
}