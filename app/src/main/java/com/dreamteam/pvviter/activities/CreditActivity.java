package com.dreamteam.pvviter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamteam.pvviter.R;

public class CreditActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_credit);
    }
}
