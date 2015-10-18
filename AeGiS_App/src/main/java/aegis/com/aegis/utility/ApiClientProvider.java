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
    private final static int id = 561000120;
    private static GoogleApiClient ourInstance;

    public static GoogleApiClient getInstance(GoogleApiClient.OnConnectionFailedListener event, Context app)
    {
        if(ourInstance == null) {
            ourInstance = new GoogleApiClient.Builder(app)
                    .enableAutoManage((FragmentActivity) app, id, event)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
        return ourInstance;
    }

    private ApiClientProvider()
    {

    }
}
