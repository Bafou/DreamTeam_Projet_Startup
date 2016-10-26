package com.dreamteam.pvviter.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dreamteam.pvviter.R;

import services.Locator;

import static services.Locator.Method.GPS;

public class StartActivity extends AppCompatActivity implements Locator.Listener {

    private Double latitude = null;
    private Double longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_getMap:
                //Temporary menu to test the map
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Used to save the location
     *
     * @param view
     */
    public void saveLocation(View view) {
        Locator locator = new Locator(this.getApplicationContext());

        if (locator.isEnableGPS()){
            //Log.d("saveLocation","gps active" );

            //getLocation is used to get the location into our implemented Listener methods onLocationFound - onLocationNotFound
            locator.getLocation(GPS, this);

        }else{
            GPSDisabledAlert();
        }


        if (this.latitude != null && this.longitude != null) {
            //TODO: send latitude and longitude to the right activity (map?)
            Log.d("saveLocation", this.latitude+";"+this.longitude);
        }
    }

    /**
     * directly save the location in our private variable latitude and longitude
     *
     * @param location
     */
    @Override
    public void onLocationFound(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        Context context = getApplicationContext();
        CharSequence text = "Position : latitude : " + latitude + " longitude : " + longitude;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * the lcoation gps can't be found, a message is show
     */
    @Override
    public void onLocationNotFound() {
        Context context = getApplicationContext();
        CharSequence text = "Impossible de récupérer la position.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * show message for enable gps
     */
    private void GPSDisabledAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Le GPS est désactivé. Voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Quitter",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}