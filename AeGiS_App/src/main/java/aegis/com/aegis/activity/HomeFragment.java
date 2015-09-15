package aegis.com.aegis.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import aegis.com.aegis.R;


public class HomeFragment extends Fragment implements View.OnClickListener{

    private Button sayHello;

    public HomeFragment() {
        // Required empty public constructor
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
                new Notifier(getActivity()).Notify("Hello World, this is a notification");
                break;
            default:
                break;
        }
    }
}
