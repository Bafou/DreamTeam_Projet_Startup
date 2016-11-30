package com.dreamteam.pvviter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.dreamteam.pvviter.R;

public class CreditActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_credit);

        TextView github_link = (TextView) findViewById(R.id.github_link);
        github_link.setMovementMethod(LinkMovementMethod.getInstance());

        TextView github_link_lucas = (TextView) findViewById(R.id.github_link_lucas);
        github_link_lucas.setMovementMethod(LinkMovementMethod.getInstance());

        TextView github_link_quentin = (TextView) findViewById(R.id.github_link_quentin);
        github_link_quentin.setMovementMethod(LinkMovementMethod.getInstance());

        TextView github_link_florian = (TextView) findViewById(R.id.github_link_florian);
        github_link_florian.setMovementMethod(LinkMovementMethod.getInstance());

        TextView github_link_geoffrey = (TextView) findViewById(R.id.github_link_geoffrey);
        github_link_geoffrey.setMovementMethod(LinkMovementMethod.getInstance());

        TextView github_link_remi = (TextView) findViewById(R.id.github_link_remi);
        github_link_remi.setMovementMethod(LinkMovementMethod.getInstance());

        TextView github_link_antoine = (TextView) findViewById(R.id.github_link_antoine);
        github_link_antoine.setMovementMethod(LinkMovementMethod.getInstance());

        TextView flaticon_1 = (TextView) findViewById(R.id.flaticon_1);
        flaticon_1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView flaticon_2 = (TextView) findViewById(R.id.flaticon_2);
        flaticon_2.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
