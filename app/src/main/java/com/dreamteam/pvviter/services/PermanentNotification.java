package com.dreamteam.pvviter.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.dreamteam.pvviter.R;
import com.dreamteam.pvviter.activities.MapActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Florian on 06/01/2017.
 */

public class PermanentNotification {

    // Sets an ID for the notification
    public static int mNotificationId = 002;
    public static String CLOSE_ACTION = "close_action";

    /**
     * Build and show the notification
     *
     * @param context is te context of the app
     */
    public PermanentNotification(Context context) {

        int icon = R.drawable.notificon;
        long when = System.currentTimeMillis();

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);




        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.permanent_notification);
        contentView.setTextViewText(R.id.resting_time, "Durée restante : 00:32");
        contentView.setTextViewText(R.id.distance, "Distance : 1500 mètres");
        contentView.setTextViewText(R.id.point_of_no_return, "point de non retour dans 15 minutes");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContent(contentView)
                .setContentTitle("Custom Notification")
                .setWhen(when);
        // make this notification permanent
        notificationBuilder.setOngoing(true);



        //Close button intent
        Intent closeButton = new Intent(CLOSE_ACTION);
        closeButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, closeButton, 0);

        contentView.setOnClickPendingIntent(R.id.close_button, pendingSwitchIntent);

        mNotificationManager.notify(mNotificationId, notificationBuilder.build());
    }


}
