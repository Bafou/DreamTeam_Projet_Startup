package com.dreamteam.pvviter.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    public static Boolean wasClosed = false;

    /**
     * Build and show the notification
     *
     * @param context is te context of the app
     */
    public PermanentNotification(Context context, String timeLeft, String distance, String timeBeforeNoReturn) {
        //if the notification was closed, we don't want to create a new one or update it.
        if(wasClosed){
            return;
        }


        timeBeforeNoReturn = timeBeforeNoReturn.replace(':','h');



        int icon = R.drawable.pvviter;
        long when = System.currentTimeMillis();

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.permanent_notification);


        contentView.setTextViewText(R.id.resting_time_mess, context.getString(R.string.time_car_label) + " : ");
        contentView.setTextViewText(R.id.point_of_no_return_mess, context.getString(R.string.point_of_no_return_notif) + " : ");
        contentView.setTextViewText(R.id.distance_mess, context.getString(R.string.distance_route_label) + " : ");
        contentView.setTextViewText(R.id.distance, distance);
        contentView.setTextColor(R.id.distance, Color.WHITE);

        if(timeLeft.contains("-")) {
            contentView.setTextColor(R.id.resting_time, Color.RED);
            contentView.setTextViewText(R.id.resting_time, "Temps expiré");
        } else {
            contentView.setTextColor(R.id.resting_time, Color.WHITE);
            contentView.setTextViewText(R.id.resting_time, timeLeft);
        }

        if(timeLeft.contains("-")) {
            contentView.setTextColor(R.id.point_of_no_return, Color.RED);
            contentView.setTextViewText(R.id.point_of_no_return, "Temps expiré");
        } else {
            contentView.setTextColor(R.id.point_of_no_return, Color.WHITE);
            contentView.setTextViewText(R.id.point_of_no_return, timeBeforeNoReturn);
        }




        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContent(contentView)
                .setContentTitle("PVviter")
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

    public static void removeNotification(Context context){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //close the notification
        mNotificationManager.cancel(PermanentNotification.mNotificationId);
        PermanentNotification.wasClosed = true;
    }


}
