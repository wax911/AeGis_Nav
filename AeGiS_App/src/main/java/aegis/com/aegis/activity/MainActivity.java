package aegis.com.aegis.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import aegis.com.aegis.R;
import aegis.com.aegis.barcodereader.BarcodeCaptureActivity;
import aegis.com.aegis.logic.Location;
import aegis.com.aegis.logic.User;
import aegis.com.aegis.utility.AsyncRunner;
import aegis.com.aegis.utility.DismissKeyboard;
import aegis.com.aegis.utility.ImageStore;
import aegis.com.aegis.utility.IntentNames;
import aegis.com.aegis.utility.Notifier;



public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener, SearchView.OnQueryTextListener, Animation.AnimationListener
{
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static String TAG = MainActivity.class.getSimpleName();
    private static Animation spin;
    private ImageView profile_pic;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private TextView Username;
    private SharedPreferences applicationSettings;
    private FloatingActionButton fab;
    private User user;
    private SharedPreferences.Editor _editor;
    private Fragment fragment = null;
    private SearchView searchbar;
    private Intent action = null;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (User) getIntent().getSerializableExtra("user_profile");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab_QR);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }

        spin = AnimationUtils.loadAnimation(this, R.anim.rotate_it);
        spin.setAnimationListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        applicationSettings = PreferenceManager.getDefaultSharedPreferences(this);
        _editor = applicationSettings.edit();

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        InitUserElements();

        fab.setOnClickListener(this);

        if(savedInstanceState == null) {
            // display the first navigation drawer view on app launch
            displayView(0);
        }
    }

    //Load names and images for the user
    private void InitUserElements()
    {
        Username = (TextView)findViewById(R.id.nav_greeting);
        profile_pic = (ImageView) findViewById(R.id.header_profile);

        if (user != null) {
            if (applicationSettings.getString("example_text", "User") != user.getFullname())
                _editor.putString("example_text", user.getFullname());
            _editor.commit();

            profile_pic.setImageBitmap(new ImageStore(this).getProfileImage());

            if (profile_pic.getDrawable() == null && user.getProfile_pic() != null) {
                new AsyncRunner(profile_pic, this).execute(user.getProfile_pic());
                profile_pic.setImageBitmap(new ImageStore(this).getProfileImage());
            } else if (profile_pic.getDrawable() == null && user.getProfile_pic() == null) {
                profile_pic.setImageResource(R.drawable.ic_profile);
            }
        } else {
            profile_pic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile));
            _editor.putString("example_text", "User");
            _editor.commit();
        }
        Username.setText(getString(R.string.greeting) + " " + applicationSettings.getString("example_text", "User"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchbar = (SearchView)MenuItemCompat.getActionView(searchItem);
        searchbar.setOnQueryTextListener(this);
        // Configure the search info and add any event listeners
        searchbar.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this,"Rotated Screeen",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_feedback)
        {
            Toast.makeText(getApplicationContext(), "Send Feedback!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new PlacesFragment();
                title = getString(R.string.title_places);
                break;
            case 2:
                startActivity(new Intent(this, NavigationActivity.class));
                title = getString(R.string.title_navigation);
                break;
            case 3:
                fragment = new ExtrasFragment();
                title = getString(R.string.title_extras);
                break;
            default:
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onDestroy() {
        //attempt to clean up resources
        profile_pic.setImageBitmap(null);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_QR:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    fab.startAnimation(spin);
                else
                    onAnimationEnd(null);
                break;
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    final String intentData = barcode.displayValue;
                    Snackbar.make(findViewById(R.id.drawer_layout),barcode.displayValue,Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (barcode.valueFormat)
                            {
                                case Barcode.GEO:
                                    action = new Intent(MainActivity.this,NavigationActivity.class);
                                    final double lat = barcode.geoPoint.lat;
                                    final double lng = barcode.geoPoint.lng;
                                    action.putExtra(IntentNames.MAP_INTENT_KEY, new Location("Place", lat, lng));
                                    startActivity(action);
                                    break;
                                case Barcode.EMAIL:
                                    //start intent to give a list of emailing apps
                                    action = new Intent(Intent.ACTION_SEND);
                                    action.setType("plain/text");
                                    action.putExtra(Intent.EXTRA_EMAIL,intentData);
                                    startActivity(action);
                                    break;
                                case Barcode.PHONE:
                                    //open dialer
                                    action = new Intent(Intent.ACTION_DIAL);
                                    action.setData(Uri.parse("tel:"+intentData));
                                    startActivity(action);
                                    break;
                                case Barcode.URL:
                                    //open web page
                                    action = new Intent(Intent.ACTION_VIEW);
                                    action.setData(Uri.parse(intentData));
                                    startActivity(action);
                                    break;
                                case Barcode.WIFI:
                                    //do something with wifi details
                                    action = new Intent(Intent.ACTION_MANAGE_NETWORK_USAGE);
                                    break;
                                default:
                                    Toast.makeText(getApplication(),"No action set for this type of data "+barcode.valueFormat,Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }).show();
                } else {
                    new Notifier(this).Notify(null,getString(R.string.barcode_failure));
                }
            } else {
                new Notifier(this).Notify("Error reading barcode",String.format(getString(R.string.barcode_error),
                                                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        Bundle info = new Bundle();
        info.putString(IntentNames.Search_View_KEY, query);
        DismissKeyboard.hideSoftKeyboard(this);
        searchbar.setIconified(true);
        searchbar.clearFocus();
        //fragment.setArguments(info);
        displayView(1);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // launch barcode activity.
        intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
