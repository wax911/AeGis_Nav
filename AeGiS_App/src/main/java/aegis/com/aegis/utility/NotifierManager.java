package aegis.com.aegis.utility;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import aegis.com.aegis.logic.INotify;

/**
 * Created by Maxwell on 10/1/2015.
 */
public class NotifierManager extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(INotify.appId);
    }
}
