package aegis.com.aegis.utility;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Maxwell on 10/29/2015.
 */
public final class LastLocationProvider implements GoogleApiClient.OnConnectionFailedListener {
    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks event;
    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    public LastLocationProvider(Context c, GoogleApiClient.ConnectionCallbacks event) {
        this.event = event;
        buildGoogleApiClient(c);
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(event)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onStart() {
        mGoogleApiClient.connect();
    }

    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public GoogleApiClient getObject() {
        return mGoogleApiClient;
    }

    public Location Retreive() {
        return mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("CustomLocation", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

}
