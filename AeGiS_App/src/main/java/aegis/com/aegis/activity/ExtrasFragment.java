package aegis.com.aegis.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import aegis.com.aegis.R;
import aegis.com.aegis.utility.WiFiDemo;


public class ExtrasFragment extends Fragment implements  View.OnClickListener
{

    private FloatingActionButton fab;
    private WiFiDemo wifimanager;
    private ProgressBar spinner;
    /*LinearLayout mLinearLayout;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_extras, container, false);

        /*RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.rect);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        ImageView i = new ImageView(getActivity());
        i.setImageResource(R.drawable.opacity);
        i.setAdjustViewBounds(true);
        i.setX(size.x / 2);
        i.setY(size.y / 2);


        layout.addView(i);*/

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_wifi);
        wifimanager =  new WiFiDemo(getActivity());
        spinner = (ProgressBar)rootView.findViewById(R.id.loading);
        spinner.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // if (wifimanager.receiver == null) {
        //   wifimanager.onResume();
        //}
        //Need to sort this causing wifi to scan on start up
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (wifimanager.receiver != null) {
            wifimanager.onStop();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_wifi:
                wifimanager.setWiFiStatus();
                wifimanager.onClick();
                spinner.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
