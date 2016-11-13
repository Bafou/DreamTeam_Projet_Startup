package com.dreamteam.pvviter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dreamteam.pvviter.R;

import org.osmdroid.util.GeoPoint;

import services.File_IO;
import services.Locator;
import utils.Data_Storage;

import static services.Locator.Method.GPS;

public class StartActivity extends Activity implements Locator.Listener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_start);

        if(File_IO.does_file_exist(getApplicationContext(), File_IO.PARKING_END_TIME)){
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Used to save the location
     *
     * @param view
     */
    public void saveLocation(View view) {
        Locator locator = new Locator(this.getApplicationContext());
        findViewById(R.id.buttonSavePosition).setVisibility(View.INVISIBLE);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);

        if (locator.isEnableGPS()){
            //Log.d("saveLocation","gps active" );

            //getLocation is used to get the location into our implemented Listener methods onLocationFound - onLocationNotFound
            locator.getLocation(GPS, this);

        }else{
            GPSDisabledAlert();
        }



    }

    /**
     * directly save the location in our private variable latitude and longitude
     *
     * @param location
     */
    @Override
    public void onLocationFound(Location location) {
        Data_Storage.set_car_location(getApplicationContext(), new GeoPoint(location.getLatitude(), location.getLongitude()));
        Data_Storage.set_user_location(getApplicationContext(), new GeoPoint(location.getLatitude(), location.getLongitude()));

        Intent intent = new Intent(this, TimeStampActivity.class);
        startActivity(intent);
    }

    /**
     * the lcoation gps can't be found, a message is show
     */
    @Override
    public void onLocationNotFound() {
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.gps_not_functionnal);
        int duration = Toast.LENGTH_SHORT;

        findViewById(R.id.buttonSavePosition).setVisibility(View.VISIBLE);
        findViewById(R.id.loading).setVisibility(View.INVISIBLE);

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * show message for enable gps
     */
    private void GPSDisabledAlert(){
        findViewById(R.id.buttonSavePosition).setVisibility(View.VISIBLE);
        findViewById(R.id.loading).setVisibility(View.INVISIBLE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.gps_activation_authorization)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Quitter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Display a pop-up to help the user with explanation messages.
     */
    public void displayHelpPopUp(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.start_page_text_1) + "\r\n\n" + getString(R.string.start_page_text_2))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}