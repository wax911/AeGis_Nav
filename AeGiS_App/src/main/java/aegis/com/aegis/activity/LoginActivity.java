package aegis.com.aegis.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.search.SearchAuth;

import aegis.com.aegis.R;
import aegis.com.aegis.logic.User;
import aegis.com.aegis.utility.AlertManager;
import aegis.com.aegis.utility.AsyncRunner;
import aegis.com.aegis.utility.ImageStore;

/**
 * Created by Maxwell on 10/11/2015.
 */
public class LoginActivity extends AppCompatActivity implements
                                                     View.OnClickListener,
                                                     ActivityCompat.OnRequestPermissionsResultCallback,
                                                     GoogleApiClient.ConnectionCallbacks,
                                                     GoogleApiClient.OnConnectionFailedListener,
                                                     DialogInterface.OnClickListener {

    private static final String TAG = "Login";

    /* RequestCode for resolutions involving sign-in */
    private static final int RC_SIGN_IN = 1;

    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    private static final int RC_PERM_GET_ACCOUNTS = 2;

    /* Keys for persisting instance variables in savedInstanceState */
    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_SHOULD_RESOLVE = "should_resolve";
    private static Snackbar login_notifications;
    private Bitmap imgtemp;
    /* Client for accessing Google APIs */
    private GoogleApiClient mGoogleApiClient;
    /* View to display current status (signed-in, signed-out, disconnected, etc) */
    private TextView mStatus;
    // [START resolution_variables]
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve;
    private SharedPreferences applicationSettings;
    //Buttons
    private Button sign_out, disconnect, proceed;
    private SignInButton sign_in;
    //Classes
    private User user;
    private ImageView profile;
    private ViewGroup mRootView;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        mRootView = (ViewGroup) findViewById(R.id.main_layout);
        mRootView.setOnClickListener(this);

        applicationSettings = PreferenceManager.getDefaultSharedPreferences(this);

        //Apply application settings
        mShouldResolve = applicationSettings.getBoolean("pref_auto_sign-in", false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }
        // Restore from saved instance state
        // [START restore_saved_instance_state]
        if (savedInstanceState != null) {
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
        }
        // [END restore_saved_instance_state]

        // Set up button click listeners
        sign_in = (SignInButton)findViewById(R.id.sign_in_button);
        sign_in.setOnClickListener(this);
        sign_out = (Button)findViewById(R.id.sign_out_button);
        sign_out.setOnClickListener(this);
        disconnect = (Button)findViewById(R.id.disconnect_button);
        disconnect.setOnClickListener(this);
        proceed = (Button)findViewById(R.id.proceed_button);
        proceed.setOnClickListener(this);
        proceed.setVisibility(View.GONE);

        profile = (ImageView)findViewById(R.id.profile_image);
        profile.setVisibility(View.GONE);

        // Large sign-in
        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);

        // Start with sign-in button disabled until sign-in either succeeds or fails
        findViewById(R.id.sign_in_button).setEnabled(false);

        user = new User();

        // Set up view instances
        mStatus = (TextView) findViewById(R.id.status);

        // [START create_google_api_client]
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
        // [END create_google_api_client]

        Snackbar.make(findViewById(R.id.main_layout), "Do you want to sign in as a guest?", Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainStarter = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainStarter);
            }
        }).show();

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            new AlertManager(this, this).Create("Location Services Disabled", this.getString(R.string.LocationMsg));
    }

    private void updateUI(boolean isSignedIn){
        if (isSignedIn) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                // Show signed-in user's name
                user.setFullname(currentPerson.getDisplayName());
                mStatus.setText(getString(R.string.signed_in_fmt, user.getFullname()));

                String profile_link = null;

                if(currentPerson.hasImage()) {
                    profile_link = currentPerson.getImage().getUrl();
                    // by default the profile url gives 50x50 px image only
                    // we can replace the value with whatever dimension we want by
                    user.setProfile_pic(profile_link.substring(0, profile_link.length() - 2) + 400);
                }

                // Show users' email address (which requires GET_ACCOUNTS permission)
                if (checkAccountsPermission()) {
                    user.setEmail(Plus.AccountApi.getAccountName(mGoogleApiClient));
                    ((TextView) findViewById(R.id.email)).setText(user.getEmail());
                }

                if(!applicationSettings.getBoolean("stored_info",false) && profile_link != null)
                {
                    Snackbar.make(findViewById(R.id.main_layout),"Profile Picture is being loaded. Please wait..",Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Nothing to do here as of now
                        }
                    }).show();
                    new AsyncRunner(profile, this).execute(user.getProfile_pic());
                }
                else if(profile_link != null)
                {
                    if(new ImageStore(this).hasPicture()) {
                        imgtemp = new ImageStore(this).getProfileImage();
                        profile.setImageBitmap(imgtemp);
                    }
                    else {
                        new AsyncRunner(profile, this).execute(user.getProfile_pic());
                        Toast.makeText(this, "Image is being loaded, you may continue..", Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                // If getCurrentPerson returns null there is generally some error with the
                // configuration of the application (invalid Client ID, Plus API not enabled, etc).
                Log.w(TAG, getString(R.string.error_null_person));
                mStatus.setText(getString(R.string.signed_in_err));
                proceed.setText(R.string.proceed_guest);
            }

            // Set button visibility
            sign_in.setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            proceed.setVisibility(View.VISIBLE);
            profile.setVisibility(View.VISIBLE);
        } else {
            // Show signed-out message and clear email field
            mStatus.setText(R.string.signed_out);
            ((TextView) findViewById(R.id.email)).setText("");

            // Set button visibility
            sign_in.setEnabled(true);
            sign_in.setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            proceed.setVisibility(View.GONE);
            profile.setVisibility(View.GONE);
        }
    }

    /**
     * Check if we have the GET_ACCOUNTS permission and request it if we do not.
     * @return true if we have the permission, false if we do not.
     */
    private boolean checkAccountsPermission() {
        final String perm = Manifest.permission.GET_ACCOUNTS;
        int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // We have the permission
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
            // Need to show permission rationale, display a snackbar and then request
            // the permission again when the snackbar is dismissed.
            Snackbar.make(findViewById(R.id.main_layout),
                          R.string.contacts_permission_rationale,
                          Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Request the permission again.
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                                              new String[]{perm},
                                                              RC_PERM_GET_ACCOUNTS);
                        }
                    }).show();
            return false;
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                                              new String[]{perm},
                                              RC_PERM_GET_ACCOUNTS);
            return false;
        }
    }

    private void showSignedInUI() {
        updateUI(true);
    }

    private void showSignedOutUI() {
        updateUI(false);
    }

    // [START on_start_on_stop]
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Snackbar.make(findViewById(R.id.main_layout), "Do you want to sign in as a guest?", Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainStarter = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainStarter);
            }
        }).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    // [END on_start_on_stop]

    // [START on_save_instance_state]
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
        outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
        Snackbar.make(findViewById(R.id.main_layout), "Do you want to sign in as a guest?", Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainStarter = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainStarter);
            }
        }).show();
    }
    // [END on_save_instance_state]

    // [START on_activity_result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }
    // [END on_activity_result]

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode);
        if (requestCode == RC_PERM_GET_ACCOUNTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSignedInUI();
            } else {
                Log.d(TAG, "GET_ACCOUNTS Permission Denied.");
            }
        }
    }



    // [START on_connected]
    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        showSignedInUI();
    }
    // [END on_connected]

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost. The GoogleApiClient will automatically
        // attempt to re-connect. Any UI elements that depend on connection to Google APIs should
        // be hidden or disabled until onConnected is called again.
        Log.w(TAG, "onConnectionSuspended:" + i);
    }

    // [START on_connection_failed]
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if(connectionResult.getErrorCode() == SearchAuth.StatusCodes.INTERNAL_ERROR) {
            login_notifications = Snackbar.make(findViewById(R.id.main_layout), "Unable to connect, Proceed as Guest User?",
                                                Snackbar.LENGTH_INDEFINITE);
            login_notifications.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainStarter = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(mainStarter);
                }
            }).show();
        }

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            showSignedOutUI();
        }
    }
    // [END on_connection_failed]

    private void showErrorDialog(ConnectionResult connectionResult) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, RC_SIGN_IN,
                                               new DialogInterface.OnCancelListener() {
                                                   @Override
                                                   public void onCancel(DialogInterface dialog) {
                                                       mShouldResolve = false;
                                                       showSignedOutUI();
                                                   }
                                               }).show();
            } else {
                Log.w(TAG, "Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

                mShouldResolve = false;
                showSignedOutUI();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(imgtemp != null)
            imgtemp.recycle();
        super.onDestroy();
    }

    // [START on_click]
    @Override
    public void onClick(View v)
    {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(mRootView, new Fade());
        }*/
        switch (v.getId()) {
            case R.id.sign_in_button:
                onSignInClicked();
                break;
            case R.id.sign_out_button:
                onSignOutClicked();
                Dispose();
                break;
            case R.id.disconnect_button:
                onDisconnectClicked();
                Dispose();
                break;
            case R.id.proceed_button:
                Intent mainStarter = new Intent(this,MainActivity.class);
                mainStarter.putExtra("user_profile",user);
                startActivity(mainStarter);
                break;
            default:
                break;
        }
    }
    // [END on_click]

    private void Dispose()
    {
        profile.setImageBitmap(null);
    }

    // [START on_sign_in_clicked]
    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        mStatus.setText(R.string.signing_in);
    }
    // [END on_sign_in_clicked]

    // [START on_sign_out_clicked]
    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        showSignedOutUI();
    }
    // [END on_sign_out_clicked]

    // [START on_disconnect_clicked]
    private void onDisconnectClicked() {
        // Revoke all granted permissions and clear the default account.  The user will have
        // to pass the consent screen to sign in again.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        showSignedOutUI();
    }
    // [END on_disconnect_clicked]

    /**
     * This method will be invoked when a button in the dialog is clicked.
     *
     * @param dialog The dialog that received the click.
     * @param which  The button that was clicked (e.g.
     *               {@link DialogInterface#BUTTON1}) or the position
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Show location settings when the user acknowledges the alert dialog
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}
