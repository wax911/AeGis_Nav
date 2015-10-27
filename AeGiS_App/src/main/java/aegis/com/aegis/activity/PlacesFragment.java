package aegis.com.aegis.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import aegis.com.aegis.R;
import aegis.com.aegis.adapter.AutoCompleteAdapter;
import aegis.com.aegis.logic.Location;
import aegis.com.aegis.logic.Places_Impl;
import aegis.com.aegis.utility.ApiClientProvider;
import aegis.com.aegis.utility.DismissKeyboard;
import aegis.com.aegis.utility.IntentNames;
import aegis.com.aegis.utility.Notifier;

public class PlacesFragment extends android.support.v4.app.Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, TextView.OnEditorActionListener, AdapterView.OnItemClickListener, ResultCallback<PlaceBuffer> {

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    private static String TAG = "App";
    protected GoogleApiClient mGoogleApiClient;
    private AutoCompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private Places_Impl desired_place;
    private ImageButton clearButton;
    private Button OpenMapButton;
    private Bundle reciever;
    private String data;
    private RatingBar mRating;
    private TextView mPlaceDetailsText;
    private TextView mPlaceDetailsAttribution;

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String gps,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, gps, address, phoneNumber,
                                 websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, gps, address, phoneNumber,
                                           websiteUri));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = ApiClientProvider.getInstance(this, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        reciever = this.getArguments();
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient = ApiClientProvider.getInstance(this, getActivity());
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_places, container, false);
        // Retrieve the AutoCompleteTextView that will display Place_Abs suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                root.findViewById(R.id.autocomplete_places);
        mRating = (RatingBar)root.findViewById(R.id.place_rating);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Changing the color of the stars in our application
            LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(this);
        mAutocompleteView.setOnEditorActionListener(this);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        mPlaceDetailsText = (TextView) root.findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView) root.findViewById(R.id.place_attribution);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        clearButton = (ImageButton) root.findViewById(R.id.button_clear);
        clearButton.setOnClickListener(this);

        OpenMapButton = (Button) root.findViewById(R.id.open_inmap);
        OpenMapButton.setOnClickListener(this);

        OpenMapButton.setEnabled(false);

        if(reciever != null) {
            data = reciever.getString(IntentNames.Search_View_KEY, null);
            mAutocompleteView.setText(data);
        }

        SetProperties();

        return root;
    }

    private void SetProperties() {
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new AutoCompleteAdapter(getContext(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                                           null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        new Notifier(getContext()).Notify("Connection Error","Could not connect to Google API Client: Error " + connectionResult.getErrorCode());
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mGoogleApiClient = null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_clear:
                mAutocompleteView.setText("");
                break;
            case R.id.open_inmap:
                startActivity(new Intent(getActivity(), NavigationActivity.class).putExtra(IntentNames.MAP_INTENT_KEY, desired_place.getPlace_cord()));
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        switch (actionId)
        {
            case EditorInfo.IME_ACTION_DONE:
                DismissKeyboard.hideSoftKeyboard(getActivity());
            break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place_Abs suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
        final AutocompletePrediction item = mAdapter.getItem(position);
        final String placeId = item.getPlaceId();
        final CharSequence primaryText = item.getPrimaryText(null);

        Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place_Abs object with additional
             details about the place.
              */
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(this);

        Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    @Override
    public void onResult(PlaceBuffer places) {
        if (!places.getStatus().isSuccess()) {
            // Request did not complete successfully
            Toast.makeText(getActivity(), "Place query did not complete. Error: " + places.getStatus().toString(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
            places.release();
            return;
        }
        // Get the Place object from the buffer.
        final com.google.android.gms.location.places.Place place = places.get(0);

        //Dismiss the keyboard when we select an item.
        DismissKeyboard.hideSoftKeyboard(getActivity());

        desired_place = new Places_Impl(
                place.getId(),
                new Location(String.valueOf(place.getName()), place.getLatLng().latitude, place.getLatLng().longitude)
                , String.valueOf(place.getAddress()), place.getRating(), String.valueOf(place.getWebsiteUri()), String.valueOf(place.getAddress())
        );


        // Format details of the place for display and show it in a TextView.
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                                                     place.getLatLng().toString(), place.getAddress(), place.getPhoneNumber(),
                                                     place.getWebsiteUri()));

        OpenMapButton.setEnabled(true);

        mRating.setRating(place.getRating() * 10);

        if (mRating.getRating() < 0) {
            Snackbar.make(getActivity().findViewById(R.id.Places_screen), desired_place.getPlace_cord().getName() + " doesn't have ratings yet.", Snackbar.LENGTH_LONG).show();
        }

        // Display the third party attributions if set.
        final CharSequence thirdPartyAttribution = places.getAttributions();
        if (thirdPartyAttribution == null) {
            mPlaceDetailsAttribution.setVisibility(View.GONE);
        } else {
            mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
            mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
        }

        places.release();
    }
}
