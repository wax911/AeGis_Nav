package aegis.com.aegis.utility;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import aegis.com.aegis.activity.HomeFragment;

/**
 * Created by Maxwell on 10/29/2015.
 */
public class AsyncFunction extends AsyncTask<Geocoder, Void, String> {
    private final int maxattempts = 3;
    private Location myloc;
    private Activity context;
    private int strike = 0;
    private HomeFragment homeScreen;

    public AsyncFunction(Location loc, Activity contx, HomeFragment homeFragment) {
        myloc = loc;
        context = contx;
        homeScreen = homeFragment;
    }

    @Override
    protected String doInBackground(Geocoder... params) {
        List<Address> addresses = null;
        try {
            if(myloc == null) return null;
            addresses = params[0].getFromLocation(myloc.getLatitude(), myloc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            //exception try again!
            strike += 1;
            if (strike < maxattempts)
                doInBackground(params);
        }
        if (addresses != null)
            if (addresses.size() > 0)
                return addresses.get(0).getLocality();

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            new CityPreference(context).setCity(s);
            homeScreen.updateWeatherData();
        }
    }
}
