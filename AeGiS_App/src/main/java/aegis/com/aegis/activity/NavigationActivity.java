package aegis.com.aegis.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.PolylineOptions;

import aegis.com.aegis.R;
import aegis.com.aegis.logic.Location;
import aegis.com.aegis.utility.IntentNames;


public class NavigationActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener , GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, View.OnClickListener{

    private GoogleMap mMap;
    private Toolbar mToolbar;
    private GroundOverlay gov;
    private GroundOverlayOptions goo;
    private Location l;
    private SharedPreferences applicationSettings;
    private FloatingActionButton fab_mylocation;
    private BitmapDescriptor overlay;
    private LatLng startp;
    private LatLng stopp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_navigation);

        l = (Location)getIntent().getSerializableExtra(IntentNames.MAP_INTENT_KEY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_maps);

        overlay = BitmapDescriptorFactory.fromResource(R.drawable.floor_plan_mp);

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

        if(l==null)
            l = new Location("Belgium Campus" ,-25.6840875,28.1315539);

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

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView))
                    .getMapAsync(this);
    }

    private void setUpMap()
    {
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

        //Show current indoor map for this item
        mMap.animateCamera(CameraUpdateFactory
                                   .newCameraPosition(new CameraPosition.Builder()
                                                              .target(new LatLng(l.getLat(), l.getLng())).zoom(19).build()));

        MarkerOptions where = new MarkerOptions().position(new LatLng(l.getLat(), l.getLng())).title(l.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).draggable(false);

        mMap.addMarker(where);

        MarkerOptions inzeta = new MarkerOptions().position(new LatLng(-25.6842879, 28.1311748)).title("Zeta");
        mMap.addMarker(inzeta);

        MarkerOptions newcenter = new MarkerOptions().position(new LatLng(-25.6841985, 28.1315539)).title("new center zone").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).draggable(false);
        mMap.addMarker(newcenter);
    }

    private void drawPath(LatLng start, LatLng stop)
    {
        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions()
                                 .add(start)
                                 .add(stop)
                                 .geodesic(true)); // move marker to this point
    }

    private void animateCameraFollowUser(LatLng mapCenter)
    {
        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        mMap.addMarker(new MarkerOptions()
                               .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation))
                               .position(mapCenter)
                               .flat(true)
                               .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                                                      .target(mapCenter)
                                                      .zoom(13)
                                                      .bearing(90)
                                                      .build();

        // Animate the change in camera view over 2 seconds
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                          2000, null);
    }

    public void onPickButtonClick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(NavigationActivity.this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, 123);


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

        if (requestCode == 123
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

            mMap.animateCamera(CameraUpdateFactory
                                       .newCameraPosition(new CameraPosition.Builder()
                                                                  .target(place.getLatLng()).zoom(19).build()));
            mMap.addMarker(new MarkerOptions()
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation))
                                   .position(place.getLatLng())
                                   .flat(true)
                                   .rotation(245));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void applyPreference()
    {
        mMap.setTrafficEnabled(applicationSettings.getBoolean("pref_trafic_enabled",false));
        mMap.setBuildingsEnabled(applicationSettings.getBoolean("pref_buildings_enabled",false));
        mMap.setMyLocationEnabled(applicationSettings.getBoolean("pref_mylocation_enabled",true));
        UiSettings uis = mMap.getUiSettings();
        uis.setCompassEnabled(applicationSettings.getBoolean("pref_compass_enabled",false));
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        //Check if the clicked item can be a store or something similar
    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        Toast.makeText(this,"Long Touch",Toast.LENGTH_LONG).show();
        MarkerOptions usermarker = new MarkerOptions().position(latLng).title("Users Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_usermarker)).draggable(true);
        mMap.addMarker(usermarker);
        startp = latLng;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_findme:
                //Snackbar.make(v,"Creates new circle with all properties",Snackbar.LENGTH_LONG).show();
                onPickButtonClick();
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(mMap == null) {
            mMap = googleMap;
            setUpMap();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this,marker.getTitle()+" started moving "+marker.getPosition().toString(),Toast.LENGTH_SHORT).show();
        if(stopp != null) {
            startp = stopp;
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //this is update on each dragging of the marker
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this,marker.getTitle()+" ended moving "+marker.getPosition().toString(),Toast.LENGTH_SHORT).show();
        stopp = marker.getPosition();
        drawPath(startp,stopp);
    }


}
