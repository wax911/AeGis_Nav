package aegis.com.aegis.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import aegis.com.aegis.R;
import aegis.com.aegis.logic.Places_Impl;

/**
 * Created by Maxwell on 11/8/2015.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ObjectHolder>
{
    private ArrayList<Places_Impl> mDataset;

    public static class ObjectHolder extends RecyclerView.ViewHolder
    {
        TextView name, address, website, phonenumber;

        public ObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.plname);
            address = (TextView) itemView.findViewById(R.id.pladdress);
            website = (TextView) itemView.findViewById(R.id.plwebsite);
            phonenumber = (TextView) itemView.findViewById(R.id.plnumber);
        }
    }

    public HistoryAdapter(ArrayList<Places_Impl> myDataset)
    {
        mDataset = myDataset;
    }

    @Override
    public ObjectHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardslayout, parent, false);

        ObjectHolder dataObjectHolder = new ObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ObjectHolder holder, int position)
    {
        holder.name.setText(mDataset.get(position).getPalce_name());
        holder.address.setText(mDataset.get(position).getPlace_address());
        holder.website.setText(mDataset.get(position).getPlace_website());
        holder.phonenumber.setText(mDataset.get(position).getPlace_contact());
    }

    public void addItem(Places_Impl dataObj, int index)
    {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        if(mDataset != null)
            return mDataset.size();
        return 0;
    }
}
