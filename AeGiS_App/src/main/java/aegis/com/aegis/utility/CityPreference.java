package aegis.com.aegis.utility;

/**
 * Created by Lolo on 10/18/2015.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CityPreference {

    private SharedPreferences prefs;
    private SharedPreferences.Editor _editor;

    public CityPreference(Activity activity) {
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    public String getCity() {
        return prefs.getString("city", "Johannesburg");
    }

    public void setCity(String city)
    {
        _editor = prefs.edit();
        _editor.putString("city", city);
        _editor.commit();
    }

}
