package aegis.com.aegis.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import aegis.com.aegis.Data.DataAccess;
import aegis.com.aegis.Data.ICommon;
import aegis.com.aegis.R;
import aegis.com.aegis.adapter.HistoryAdapter;
import aegis.com.aegis.logic.Places_Impl;


public class ExtrasFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView heading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_extras, container, false);
        heading = (TextView)rootView.findViewById(R.id.extrasHeading);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.historyList);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new HistoryAdapter(getDataSet());
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //heading.setText("No history!");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private ArrayList<Places_Impl> getDataSet() {
        DataAccess da = new DataAccess(getContext());
        return da.GetAll(ICommon.TableNames.Place);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

