package aegis.com.aegis.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import aegis.com.aegis.R;

/**
 * Created by Maxwell on 9/13/2015.
 */
//we'll use this class as a notification center mediator
public class Notifier
{
    private Context application;
    private SharedPreferences applicationSettings;
    private NotificationManager notifyManager = null;
    //the notification which will be used
    private String notification = null;
    //just an application Identifier
    private static final int appId = 61195110;

    public void Notify(String Message)
    {
        if (applicationSettings.getBoolean("pref_notify_me", true))
        {
            NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(application.getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher)
                                                                                                                     .setContentTitle("Aegis")
                                                                                                                     .setSound(Uri.parse(notification))
                                                                                                                     .setContentText(Message);
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
    }
}
