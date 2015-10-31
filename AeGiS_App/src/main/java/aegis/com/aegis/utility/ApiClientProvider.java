package aegis.com.aegis.utility;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

/**
 * Created by Maxwell on 10/12/2015.
 * Singleton class to assure that we have only one instance of the GoogleAPIClient
 */
public class ApiClientProvider {
    private final static int id = 561903120;
    private static GoogleApiClient ourInstance;

    private ApiClientProvider() {

    }

    public static GoogleApiClient getInstance(GoogleApiClient.OnConnectionFailedListener event, Context app)
    {
        if(ourInstance == null) {
            ourInstance = new GoogleApiClient.Builder(app)
                    .enableAutoManage((FragmentActivity) app, id, event)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        } else if (!ourInstance.isConnected()) {
            ourInstance.connect();
        }
        return ourInstance;
    }

    public void stopService()
    {
        if (ourInstance.isConnected() && ourInstance != null)
            ourInstance.disconnect();
    }

    public void startServiceOrResume() {
        if (!ourInstance.isConnected() && ourInstance != null)
            ourInstance.connect();
    }
}
