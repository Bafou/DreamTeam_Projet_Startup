package com.dreamteam.pvviter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dreamteam.pvviter.R;
import com.dreamteam.pvviter.services.FileIO;
import com.dreamteam.pvviter.services.Locator;
import com.dreamteam.pvviter.utils.Data_Storage;

import org.osmdroid.util.GeoPoint;

public class StartActivity extends Activity {
    private boolean stopThread = false;
    public boolean displayToast = false;
    static public boolean display_PV_avoided = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_start);

        if (FileIO.does_file_exist(getApplicationContext(), FileIO.PARKING_END_TIME)) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }

        if(display_PV_avoided){
            display_pv_avoided_dialog();
        }
    }

    /**
     * Used to save the location
     *
     * @param view
     */
    public void saveLocation(View view) {

        final Locator locator = new Locator(this) {
            @Override
            public void onLocationChanged(Location location) {
                stopThread = true;
                updateGPSCoordinates();

                Data_Storage.set_car_location(StartActivity.this, new GeoPoint(location.getLatitude(), location.getLongitude()));
                Data_Storage.set_user_location(StartActivity.this, new GeoPoint(location.getLatitude(), location.getLongitude()));

                Intent intent = new Intent(StartActivity.this, TimeStampActivity.class);
                StartActivity.this.startActivity(intent);
                this.stopUsingGPS();
            }
        };



       Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                Thread.sleep(30000);
                    if(!stopThread) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                locator.stopUsingGPS();
                                findViewById(R.id.buttonSavePosition).setVisibility(View.VISIBLE);
                                findViewById(R.id.loading).setVisibility(View.GONE);
                                findViewById(R.id.gps_error).setVisibility(View.VISIBLE);
                                displayToast = true;
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
       };

        thread.start();

        findViewById(R.id.gps_error).setVisibility(View.INVISIBLE);
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

    public void display_pv_avoided_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.super_yes , null);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.pv_avoided, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                ImageView image = (ImageView) dialog.findViewById(R.id.pvavoidedImage);
                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.bravo);
                float imageWidthInPX = (float)image.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                image.setLayoutParams(layoutParams);


            }
        });
        display_PV_avoided = false;
    }
}