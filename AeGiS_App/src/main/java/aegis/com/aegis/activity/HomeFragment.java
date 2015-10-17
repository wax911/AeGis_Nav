package aegis.com.aegis.activity;


import android.app.Activity;
import android.content.Intent;
<<<<<<< HEAD
=======
import android.content.SharedPreferences;
>>>>>>> origin/master
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
<<<<<<< HEAD
=======
import android.widget.TextView;
>>>>>>> origin/master
import android.widget.Toast;

import aegis.com.aegis.R;
import aegis.com.aegis.logic.Location;
import aegis.com.aegis.utility.IntentNames;
import aegis.com.aegis.utility.Notifier;


public class HomeFragment extends Fragment implements View.OnClickListener{

    private Button sayHello;
    private  Location[] lcs;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sayHello = (Button)rootView.findViewById(R.id.notify_btn);
        sayHello.setOnClickListener(this);

        // Inflate the layout for this fragment
        lcs = new Location[] {new Location("O.R International Tambo" ,-26.1314138,28.2323354)
                , new Location("Kolonade Shopping Center" ,-25.681241, 28.250713)
                ,new Location("Cape Town International" ,-33.9696438,18.5970359)
<<<<<<< HEAD
                ,new Location("Belgium Campus", -25.6841985,28.1315539)};

        final Spinner sp = (Spinner)rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinnerlayout,
=======
                    ,new Location("Belgium Campus", -25.6841985,28.1315539)};

        final Spinner sp = (Spinner)rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
>>>>>>> origin/master
                                                          new String[] {"O.R International Tambo",
                                                                  "Kolonade Shopping Center",
                                                                  "Cape Town International",
                                                                  "Belgium Campus"});
        sp.setAdapter(adapter);
        final boolean[] touched = new boolean[1];
        sp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touched[0] = true;
                return false;
            }
        });
<<<<<<< HEAD

=======
>>>>>>> origin/master
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!touched[0]) return;
<<<<<<< HEAD
                startActivity(new Intent(getActivity(), NavigationActivity.class).putExtra(IntentNames.MAP_INTENT_KEY, lcs[i]));
=======
                Toast.makeText(getActivity(), lcs[i].getName(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), NavigationActivity.class).putExtra("Loc", lcs[i]));
>>>>>>> origin/master
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.notify_btn:
<<<<<<< HEAD
                new Notifier(getActivity()).Notify("Demo Notification","Hello World, this is a notification by AeGis Nav App. Swipe me to the left or right to dismiss me :)");
=======
                new Notifier(getActivity()).Notify("Hello World, this is a notification by AeGis Nav App. Swipe me to the left or right to dismiss me :)");
>>>>>>> origin/master
                break;
            default:
                break;
        }
    }
}
