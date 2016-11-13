package services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.dreamteam.pvviter.R;
import com.dreamteam.pvviter.activities.MapActivity;


/**
 * Create a show the notification of the return
 *
 * Created by FlorianDoublet on 13/11/2016.
 */
public class PointOfNoReturnNotification {


    // Sets an ID for the notification
    public int mNotificationId = 001;

    /**
     * Build and show the notification
     * @param context is te context of the app
     */
    public PointOfNoReturnNotification(Context context) {

        String message = context.getResources().getString(R.string.point_of_no_return_message_notification);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notificon)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(message);


        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);


        mBuilder.setContentIntent(createPendintIntentToAnAcitivity(context, MapActivity.class));


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    /**
     * Create a PendingIntent to the given classActivity
     * @param context is the context of the app
     * @param classActvity is the activity we want to be redirected
     * @return
     */
    private PendingIntent createPendintIntentToAnAcitivity(Context context , Class classActvity) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, classActvity);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(classActvity);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return resultPendingIntent;

    }
}
