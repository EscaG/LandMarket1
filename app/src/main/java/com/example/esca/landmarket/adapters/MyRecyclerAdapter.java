package com.example.esca.landmarket.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.LandInfo;

import java.util.ArrayList;

/**
 * Created by Esca on 08.02.2017.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private ArrayList<LandInfo> landInfos = new ArrayList<>();
    private MasterClickListener listener;

    public MyRecyclerAdapter(MasterClickListener listener) {
        this.listener = listener;
    }

    public void addUser(LandInfo landInfo) {
        landInfos.add(landInfo);
        Log.d("ONTAG", "land" + landInfo.getOwner());
        notifyItemInserted(landInfos.size());
    }

    public void removeUser(int position) {
        landInfos.remove(position);
        notifyItemRemoved(position);
    }

    public void updateContact(LandInfo landInfo, int position) {
//        contacts.remove(position);
//        contacts.add(position,contact);
        LandInfo tmp = getItemByPosition(position);
        tmp = landInfo;
        notifyDataSetChanged();
    }

    public void clear() {
        notifyItemRangeRemoved(0, landInfos.size());
        landInfos.clear();

    }

    public void addItemAtFront(LandInfo landRegister) {
        landInfos.add(landRegister);
        notifyItemInserted(0);
    }

    public LandInfo getItemByPosition(int position) {
        return landInfos.get(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("MY_TAG", "MyAdapter onCreateViewHolder()");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_section, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d("MY_TAG", "MyAdapter onBindViewHolder()");
        LandInfo landRegister = landInfos.get(position);
        holder.assignment.setText(landRegister.getAssignment());
        holder.price.setText(landRegister.getPrice());
        holder.description.setText(landRegister.getDescription());
        holder.address.setText(landRegister.getAddress());

    }

    @Override
    public int getItemCount() {
        return landInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView area, assignment, price, description, address,owner;
        private Button btnShowOnTheMap;

        public MyViewHolder(View view) {
            super(view);
            assignment = (TextView) view.findViewById(R.id.row_section_assignment);
            price = (TextView) view.findViewById(R.id.row_section_price);
            description = (TextView) view.findViewById(R.id.row_section_description);
            address = (TextView) view.findViewById(R.id.row__section_address);
            btnShowOnTheMap = (Button) view.findViewById(R.id.row_section_show_on_the_map);
            btnShowOnTheMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickShowOnTheMap(v, getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRowClick(v, getAdapterPosition());
                }
            });
        }
    }

    public interface MasterClickListener {
        void onClickShowOnTheMap(View view, int position);

        void onRowClick(View view, int position);
    }

}
