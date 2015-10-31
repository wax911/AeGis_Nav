package aegis.com.aegis.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import aegis.com.aegis.R;
import aegis.com.aegis.utility.AsyncFunction;
import aegis.com.aegis.utility.CityPreference;
import aegis.com.aegis.utility.LastLocationProvider;
import aegis.com.aegis.utility.RemoteFetch;


public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    Handler handler;
    private Typeface weatherFont;
    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;
    private ImageView places, maps, extras;
    private ProgressBar spinner;
    private Location mylocation;
    private LastLocationProvider provider;
    private AsyncFunction backgroundRunner;


    public HomeFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        spinner = (ProgressBar) rootView.findViewById(R.id.loading);
        spinner.setVisibility(View.VISIBLE);
        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        places = (ImageView) rootView.findViewById(R.id.places);
        maps = (ImageView) rootView.findViewById(R.id.maps);
        extras = (ImageView) rootView.findViewById(R.id.extras);
        places.setOnClickListener(this);
        maps.setOnClickListener(this);
        extras.setOnClickListener(this);
        provider.onStart();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons.ttf");
        provider = new LastLocationProvider(getContext(), this);
    }

    private void updateWeatherData() {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(new CityPreference(getActivity()).getCity());
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                Toast.makeText(getActivity(),
                                               getActivity().getString(R.string.place_not_found),
                                               Toast.LENGTH_LONG).show();
                                spinner.setVisibility(View.INVISIBLE);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                            spinner.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                                      ", " +
                                      json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp")) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                           json.getJSONObject("sys").getLong("sunrise") * 1000,
                           json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new PlacesFragment();
                break;
            case 2:
                startActivity(new Intent(getActivity(), NavigationActivity.class));
                break;
            case 3:
                fragment = new ExtrasFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.places:
                displayView(1);
                break;
            case R.id.maps:
                displayView(2);
                break;
            case R.id.extras:
                displayView(3);
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mylocation = provider.Retreive();

        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        backgroundRunner = new AsyncFunction(mylocation, getActivity());
        backgroundRunner.execute(gcd);
        Toast.makeText(getContext(), "Obtaining your CustomLocation, Please wait..", Toast.LENGTH_LONG).show();
        updateWeatherData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //for some reason we lost the connection to google play services
        provider.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateWeatherData();
    }
}
