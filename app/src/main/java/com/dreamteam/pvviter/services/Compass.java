package com.dreamteam.pvviter.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.dreamteam.pvviter.utils.MapFunctions;

import org.osmdroid.views.MapView;

/**
 * A Compass that make the map point where the phone is pointing.
 */
public class Compass implements SensorEventListener {

    private MapView map;
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    public static float azimuth = 0f;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];

    /**
     * @param map The map to update
     */
    public Compass(MapView map) {
        this.map = map;

        sensorManager = (SensorManager) map.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    /**
     * Start listening to sensors to calculate the compass direction
     */
    public void startListeningToSensors() {
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Stop listening to sensors
     */
    public void stopListeningToSensors() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                MapFunctions.changeUserRotation(map, azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Nothing to do
    }
}