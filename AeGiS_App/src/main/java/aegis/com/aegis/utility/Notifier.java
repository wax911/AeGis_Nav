package aegis.com.aegis.utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import aegis.com.aegis.R;
import aegis.com.aegis.logic.INotify;

/**
 * Created by Maxwell on 9/13/2015.
 */
//we'll use this class as a notification center mediator
public class Notifier implements INotify
{
    private Context application;
    private SharedPreferences applicationSettings;
    private NotificationManager notifyManager = null;
    //the notification which will be used
    private String notification = null;
    private static long[] vibrate;

    public void Notify(String Title,String Message)
    {
        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // big view of the notification.
        Intent dismissIntent = new Intent(application, NotifierManager.class);
        PendingIntent piDismiss = PendingIntent.getBroadcast(application, 0, dismissIntent, 0);

        if (applicationSettings.getBoolean("pref_notify_me", true))
        {
            NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(application.getApplicationContext()).setSmallIcon(R.mipmap.ic_notification)
                     .setContentTitle(Title == null ? "Aegis": Title)
                     .setSound(Uri.parse(notification))
                     .setContentText(Message)
                     .setStyle(new NotificationCompat.BigTextStyle().bigText(Message))
                     .setVibrate(vibrate)
                     .addAction(R.drawable.ic_dismiss, "Dismiss", piDismiss)
                     .setOnlyAlertOnce(true)
                    .setAutoCancel(true);

            notifyManager.notify(appId, mbuilder.build());
        }
    }

    /**Get the application context**/
    public Notifier(Context context)
    {
        if(application != null) return;
        application = context;
        applicationSettings = PreferenceManager.getDefaultSharedPreferences(application);
        notification = applicationSettings.getString("notifications_new_message_ringtone", "DEFAULT_SOUND");
        notifyManager = (NotificationManager)application.getSystemService(context.NOTIFICATION_SERVICE);
        if(applicationSettings.getBoolean("notifications_new_message_vibrate",false))
            vibrate = new long[] {200,200,200};
    }
}
