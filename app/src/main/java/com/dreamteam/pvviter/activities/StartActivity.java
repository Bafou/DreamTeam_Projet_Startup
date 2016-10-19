package com.dreamteam.pvviter.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dreamteam.pvviter.R;

import services.Locator;

import static services.Locator.Method.NETWORK_THEN_GPS;

public class StartActivity extends AppCompatActivity implements Locator.Listener {

    private double latitude;
    private double longitude;

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveLocation(View view){
        Locator locator = new Locator(this.getApplicationContext());
        locator.getLocation(NETWORK_THEN_GPS, this);
    }

    @Override
    public void onLocationFound(Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();


        Context context = getApplicationContext();
        CharSequence text = "Position : latitude : " + latitude + " longitude : "  + longitude ;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onLocationNotFound(){
        Context context = getApplicationContext();
        CharSequence text = "Impossible de récupérer la position.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
