package com.dreamteam.pvviter.activities;


import android.app.Activity;
import android.os.Bundle;

import fragments.SettingsFragment;

/**
 * Created by Geoffrey on 12/11/2016.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}