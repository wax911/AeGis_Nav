package aegis.com.aegis.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.vision.barcode.Barcode;

import aegis.com.aegis.R;
import aegis.com.aegis.adapter.AutoCompleteAdapter;
import aegis.com.aegis.barcodereader.BarcodeCaptureActivity;
import aegis.com.aegis.logic.User;
import aegis.com.aegis.utility.ApiClientProvider;
import aegis.com.aegis.utility.AsyncRunner;
import aegis.com.aegis.utility.DismissKeyboard;
import aegis.com.aegis.utility.ImageStore;
import aegis.com.aegis.utility.IntentNames;
import aegis.com.aegis.utility.Notifier;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener, SearchView.OnQueryTextListener, Animation.AnimationListener
{

    private static String TAG = MainActivity.class.getSimpleName();

    private ImageView profile_pic;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private TextView Username;
    private SharedPreferences applicationSettings;
    private FloatingActionButton fab;
    private static Animation spin;
    private User user;
    private SharedPreferences.Editor _editor;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private String Result;
    private AutoCompleteAdapter mAdapter;
    private Fragment fragment = null;
    private SearchView searchbar;


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
            profile_pic.setImageResource(R.drawable.ic_profile);
        }

        Username = (TextView) findViewById(R.id.nav_greeting);
        Username.setText(getString(R.string.greeting) + " " + applicationSettings.getString("example_text", "User"));
        fab.setOnClickListener(this);

        if(savedInstanceState == null) {
            // display the first navigation drawer view on app launch
            displayView(0);
        }
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

        if(id == R.id.action_logout)
        {
            Toast.makeText(getApplicationContext(), "Logs Out", Toast.LENGTH_SHORT).show();
            finish();
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
                fragment =  null;
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
        super.onDestroy();
        //attempt to clean up resources
        profile_pic = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_QR:
                fab.startAnimation(spin);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Barcode Result = barcode;
                    Snackbar.make(findViewById(R.id.drawer_layout),Result.displayValue,Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok),this).show();
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

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // launch barcode activity.
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
