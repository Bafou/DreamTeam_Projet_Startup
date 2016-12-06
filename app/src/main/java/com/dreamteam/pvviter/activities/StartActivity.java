package com.dreamteam.pvviter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.dreamteam.pvviter.R;
import com.dreamteam.pvviter.services.FileIO;
import com.dreamteam.pvviter.services.Locator;
import com.dreamteam.pvviter.utils.Data_Storage;

import org.osmdroid.util.GeoPoint;

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_start);

        if (FileIO.does_file_exist(getApplicationContext(), FileIO.PARKING_END_TIME)) {
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

        Locator locator = new Locator(this) {
            @Override
            public void onLocationChanged(Location location) {
                updateGPSCoordinates();

                Data_Storage.set_car_location(StartActivity.this, new GeoPoint(location.getLatitude(), location.getLongitude()));
                Data_Storage.set_user_location(StartActivity.this, new GeoPoint(location.getLatitude(), location.getLongitude()));

                Intent intent = new Intent(StartActivity.this, TimeStampActivity.class);
                StartActivity.this.startActivity(intent);
                this.stopUsingGPS();
            }
        };


        findViewById(R.id.buttonSavePosition).setVisibility(View.INVISIBLE);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);

        if (locator.getIsGPSTrackingEnabled()) {
            locator.getLocation();
            locator.updateGPSCoordinates();
        } else {
            locator.showSettingsAlert();
        }

    }

    /**
     * Display a pop-up to help the user with explanation messages.
     */
    public void displayHelpPopUp(View view) {
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