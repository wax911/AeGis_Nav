package aegis.com.aegis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aegis.com.aegis.R;
import aegis.com.aegis.logic.CustomLocation;
import aegis.com.aegis.route.AbstractRouting;
import aegis.com.aegis.route.Route;
import aegis.com.aegis.route.Routing;
import aegis.com.aegis.route.RoutingListener;
import aegis.com.aegis.utility.DirectionProvider;
import aegis.com.aegis.utility.IntentNames;
import aegis.com.aegis.utility.Notifier;
import aegis.com.aegis.utility.SphericalUtil;


public class NavigationActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener,
                                                                     View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener,
                                                                     GoogleMap.OnMyLocationChangeListener, RoutingListener {

    private GoogleMap mMap;
    private Toolbar mToolbar;
    private GroundOverlay gov;
    private GroundOverlayOptions goo;
    private CustomLocation l;
    private SharedPreferences applicationSettings;
    private FloatingActionButton fab_mylocation;
    private BitmapDescriptor overlay;
    private LatLng startp;
    private LatLng stopp;
    private DirectionProvider direction;
    private Location mylocation;
    private boolean isnavigating;
    private Polyline mPolyline;
    private double distance;
    private TextView distDisplay;
    private Marker destinationM;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;
    private boolean iswalking;
    // Creating and Building the Dialog
    private AlertDialog.Builder builder;

    // Strings to Show In Dialog with Radio Buttons
    final CharSequence[] items = {" Indoors "," Driving "};
    private AlertDialog levelDialog;

    /*
    * Benard WiFi Finder at bottom of screen :) easier code splitting
    * */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_navigation);

        context = getApplicationContext();

        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (mainWifi.isWifiEnabled() == false) {
            mainWifi.setWifiEnabled(true);
        }

        builder = new AlertDialog.Builder(this);
        direction = new DirectionProvider(this);
        direction.onResume();

        l = (CustomLocation) getIntent().getSerializableExtra(IntentNames.MAP_INTENT_KEY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_maps);

        distDisplay = (TextView) findViewById(R.id.distance);
        fab_mylocation = (FloatingActionButton)findViewById(R.id.fab_findme);
        fab_mylocation.setOnClickListener(this);
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            mToolbar.setLogo(R.drawable.ic_navi);
            if(l!=null)
                mToolbar.setTitle(" " + l.getName());
            else
                mToolbar.setTitle(" AeGis Free Mode");
        }
        applicationSettings = PreferenceManager.getDefaultSharedPreferences(this);

        if (l == null)
            l = new CustomLocation("Belgium Campus", -25.6840875, 28.1315539);
        distDisplay.setVisibility(View.INVISIBLE);
        setUpMapIfNeeded();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gov.remove();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        try {
            context.unregisterReceiver(receiverWifi);
        }catch (RuntimeException e)
        {
            Log.e("Navigation",e.getMessage());
        }
        super.onPause();
    }

    /**
     * Save all appropriate fragment state.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        direction.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        direction.onPause();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView))
                    .getMapAsync(this);
    }

    private void setUpMap()
    {
        overlay = BitmapDescriptorFactory.fromResource(R.drawable.floor_plan_mp);
        LatLng campus = new LatLng(-25.6840875,28.1315539);
        goo = new GroundOverlayOptions().image(overlay).position(campus, 200f, 200f).bearing(199f);
        gov = mMap.addGroundOverlay(goo);
        applyPreference();

        if(!mMap.isIndoorEnabled())
        {
            Toast.makeText(this, "Indoor maps is currently unavailable", Toast.LENGTH_LONG).show();
            mMap.setIndoorEnabled(true);
            return;
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMyLocationButtonClickListener(this);

        //Show current indoor map for this item
        mMap.animateCamera(CameraUpdateFactory
                                   .newCameraPosition(new CameraPosition.Builder()
                                                              .target(new LatLng(l.getLat(), l.getLng())).zoom(19).build()));

        MarkerOptions where = new MarkerOptions().position(new LatLng(l.getLat(), l.getLng())).title(l.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).draggable(false);

        mMap.addMarker(where);

        MarkerOptions inzeta = new MarkerOptions().position(new LatLng(-25.6842879, 28.1311748)).title("Zeta");
        mMap.addMarker(inzeta);

        //MarkerOptions newcenter = new MarkerOptions().position(new LatLng(-25.683888, 28.131110)).title("new center zone").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).draggable(false);
        //mMap.addMarker(newcenter);

        mPolyline = mMap.addPolyline(new PolylineOptions()
                .color(R.color.colorAccent)
                                                      .geodesic(true));
    }

    private void drawPath(LatLng start, LatLng stop)
    {
        if(iswalking) {
            mPolyline.setPoints(Arrays.asList(start, stop));
            // Polylines are useful for marking paths and routes on the map.
        }
    }

    public void onPickButtonClick()
    {
        //check if we can clear some previous navigation data
        if (isnavigating)
            destinationM.remove();
        isnavigating = false;
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(NavigationActivity.this);
            mylocation = mMap.getMyLocation();
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, IntentNames.Places_Request_Code);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), NavigationActivity.this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(NavigationActivity.this, "Google Play Services is not available.",
                           Toast.LENGTH_LONG)
                 .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        iswalking = false;
        removePolylines();
        removePolyline();

        if(destinationM != null)
            destinationM.remove();

        if (requestCode == IntentNames.Places_Request_Code
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            Toast.makeText(this,name+" "+address+Html.fromHtml(attributions),Toast.LENGTH_LONG).show();
            if(place == null) super.onActivityResult(requestCode, resultCode, data);
                destinationM = mMap.addMarker(new MarkerOptions()
                                                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_usermarker))
                                                      .title("Your Destination")
                                                      .position(place.getLatLng())
                                                      .draggable(false)
                                                      .snippet(name + " " + address + Html.fromHtml(attributions)));
            stopp = place.getLatLng();
            progressDialog = ProgressDialog.show(this, "Please wait.",
                                                 "Getting your location", true);

            startp = new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
            l = new CustomLocation(String.valueOf(name),stopp.latitude,stopp.longitude);

            progressDialog.dismiss();

            mMap.animateCamera(CameraUpdateFactory
                                       .newCameraPosition(new CameraPosition.Builder()
                                                                  .target(new LatLng(mylocation.getLatitude(), mylocation.getLongitude())).bearing(direction.getRotation()).zoom(18).build()), 2500, null);


            builder.setIcon(R.mipmap.ic_launcher_main);
            builder.setTitle("Mode of Travel");
            //builder.setMessage("Since the application is in it's alpha stage in terms of navigation, Please Select your mode of travel. Driving Outdoors(Car/On Foot) Or Indoors (Shopping Malls, Airports, e.t.c)");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            iswalking = true;
                            isnavigating = true;
                            distDisplay.setVisibility(View.VISIBLE);
                            drawPath(new LatLng(mylocation.getLatitude(), mylocation.getLongitude()), place.getLatLng());
                            break;
                        case 1:
                            iswalking = false;
                            isnavigating = true;
                            distDisplay.setVisibility(View.VISIBLE);
                            Route();
                            break;
                    }
                    levelDialog.dismiss();
                }
            });
            levelDialog = builder.create();
            levelDialog.show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void applyPreference()
    {
        mMap.setTrafficEnabled(applicationSettings.getBoolean("pref_trafic_enabled",false));
        mMap.setBuildingsEnabled(applicationSettings.getBoolean("pref_buildings_enabled", false));
        mMap.setMyLocationEnabled(applicationSettings.getBoolean("pref_mylocation_enabled", true));
        UiSettings uis = mMap.getUiSettings();
        uis.setCompassEnabled(applicationSettings.getBoolean("pref_compass_enabled", false));
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        //Check if the clicked item can be a store or something similar
    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {

        MarkerOptions usermarker = new MarkerOptions().position(latLng).title("Users Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_usermarker)).draggable(true);
        mMap.addMarker(usermarker);
        //startp = latLng;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_findme:
                onPickButtonClick();
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(mMap == null) {
            mMap = googleMap;
            TestWifi();
            setUpMap();
            mainWifi.startScan();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        /*if(stopp != null) {
            startp = stopp;
        }*/
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //this is update on each dragging of the marker
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        /*stopp = marker.getPosition();
        drawPath(startp, stopp);*/
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mylocation = mMap.getMyLocation();
        if(mylocation == null) return false;
        startp = new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
        LatLng current = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
        CameraPosition cameraPosition = CameraPosition.builder()
                                                      .target(current)
                                                      .zoom(19)
                                                      .bearing(direction.getRotation())
                                                      .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        return true;
    }


    @Override
    public void onMyLocationChange(android.location.Location location) {
        //forces our app to ignore larger inaccurate values
        if (location.getAccuracy() > 12) return;
        mainWifi.startScan();
        TestWifi();
        if (isnavigating) {
            mylocation = location;
            //calculate distance display some stuff in here
            mMap.animateCamera(CameraUpdateFactory
                                       .newCameraPosition(new CameraPosition.Builder()
                                                                  .target(new LatLng(location.getLatitude(), location.getLongitude())).bearing(direction.getRotation()).zoom(18).build()), 2000, null);
            UpdateUIDistance();
        }
    }

    private void UpdateUIDistance() {
        LatLng start = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
        distance = SphericalUtil.computeDistanceBetween(stopp, start);

        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }
        distDisplay.setText(String.format("Distance: %4.3f%s", distance, unit));

        if(distance < 50 && unit == "m") {
            new Notifier(getApplication()).Notify("Approaching Destination", "You desination is less than 50 Meters away");
            iswalking = false;
            isnavigating = false;
            removePolylines();
            removePolyline();

            distDisplay.setVisibility(View.INVISIBLE);
            return;
        }
        if(iswalking && isnavigating)
            drawPath(startp,stopp);
        else if(!iswalking && isnavigating)
        {
            //add changes to the exisiting path
            //Route();
        }
    }

    public void Route()
    {
            progressDialog = ProgressDialog.show(this, "Please wait.",
                                                 "Fetching route information.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(startp, stopp)
                    .build();
            routing.execute();
    }


    @Override
    public void onRoutingFailure() {
        // The Routing request failed
        progressDialog.dismiss();
        Toast.makeText(this,"Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        progressDialog.dismiss();
        removePolylines();

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    private void removePolylines()
    {
        if(polylines != null)
            if(polylines.size()>0) {
                for (Polyline poly : polylines) {
                    poly.remove();
                }
            }

    }

    private void removePolyline()
    {
        mPolyline.remove();
        mPolyline = mMap.addPolyline(new PolylineOptions()
                                             .color(R.color.colorAccent)
                                             .geodesic(true));
    }

    @Override
    public void onRoutingCancelled() {

    }

    private ListView lv;
    private TextView tv;
    private ArrayList<Integer> circles = new ArrayList<Integer>();
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private StringBuilder sb;
    private ArrayAdapter<String> adapter;
    private Context context;
    private Button b;
    private View myView;
    private MarkerOptions aegisdroppin;
    private Marker myMarker;

    public void TestWifi() {

        ArrayList<String> connections;
        ArrayList<Float> Signal_Strenth;
        ArrayList<String> FullSpot;

        connections = new ArrayList<String>();
        Signal_Strenth = new ArrayList<Float>();
        FullSpot = new ArrayList<String>();
        sb = new StringBuilder();
        List<ScanResult> wifiList;
        wifiList = mainWifi.getScanResults();
/*        if (wifiList.size() == 0)
        {
            FullSpot.add("No Wifi's In Area");
            Toast.makeText(context,"No Sectors Found Around You", Toast.LENGTH_LONG).show();
            //tv.setText("Not In Sector");
        }else {*/
        for (int i = 0; i < wifiList.size(); i++) {
            //connections.add(wifiList.get(i).SSID);
            //Signal_Strenth.add((float) wifiList.get(i).level);
            DecimalFormat df = new DecimalFormat("#.##");
            // Log.d(TAG, wifiList.get(i).BSSID + ": "+ wifiList.get(i).level + ", d: " + df.format(calculateDistance((double) wifiList.get(i).level, wifiList.get(i).frequency)) + "m");

            FullSpot.add("WiFi Name: " + wifiList.get(i).SSID + "\nSignal Distance: " + wifiList.get(i).BSSID + ": " + wifiList.get(i).level + ", \nDistance: " + df.format(calculateDistance((double) wifiList.get(i).level, wifiList.get(i).frequency)) + "m");

        }
        for (int k = 0; k < wifiList.size(); k++) {
//                    Toast.makeText(context,wifiList.size(), Toast.LENGTH_LONG).show();
            String idd = wifiList.get(k).BSSID;
            String name = wifiList.get(k).SSID;
            //Toast.makeText(context,idd, Toast.LENGTH_LONG).show();
            if (idd.equals(applicationSettings.getString("pref_provider","58:17:0c:47:48:d4")) || name.equals(applicationSettings.getString("pref_provider_name","Xperia Mini"))) {
                //Toast.makeText(context,"In Loop Found Wifi :"+wifiList.get(k).SSID, Toast.LENGTH_LONG).show();
                int level1 = (int) calculateDistance((double) wifiList.get(k).level, wifiList.get(k).frequency);

                //Toast.makeText(context,"level1"+level1, Toast.LENGTH_LONG).show();
                if (level1 <= 10) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    Toast.makeText(context, "Found Wifi :" + df.format(calculateDistance((double) wifiList.get(k).level, wifiList.get(k).frequency)), Toast.LENGTH_LONG).show();

                    if (myMarker != null)
                        myMarker.remove();

                    aegisdroppin = new MarkerOptions().position(new LatLng(-25.684359, 28.132313999999997)).title("Gamma").draggable(true);
                    myMarker = mMap.addMarker(aegisdroppin);
                    //tv.setText(wifiList.get(k).SSID + " Sector");
                    //Toast.makeText(context,"After TextSEt Toast", Toast.LENGTH_LONG).show();
                    return;
                }
                if (level1 >= 10)
                    myMarker.remove();

                aegisdroppin = new MarkerOptions().position(new LatLng(-25.684188, 28.131681999999998)).title("Leuven").draggable(true);
                myMarker = mMap.addMarker(aegisdroppin);
                //tv.setText("No Sectors Found Around You");
            } else {
                if (myMarker != null)
                    myMarker.remove();
                //return;
            }
        }
    }
    public double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            ArrayList<String> connections = new ArrayList<String>();
            ArrayList<Float> Signal_Strenth = new ArrayList<Float>();
            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {
                connections.add(wifiList.get(i).SSID);
            }


        }
    }


}
