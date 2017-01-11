package com.dreamteam.pvviter.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.dreamteam.pvviter.services.PermanentNotification;

/**
 * Created by Florian on 06/01/2017.
 */

public class PermanentNotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(PermanentNotification.CLOSE_ACTION.equals(action)) {
            PermanentNotification.removeNotification(context);
        }
    }

}
