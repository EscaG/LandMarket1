package com.example.esca.landmarket.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;


import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.LandInfo;

import java.util.ArrayList;

/**
 * Created by Esca on 08.02.2017.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private ArrayList<LandInfo> landInfos = new ArrayList<>();
    private LandInfoClickListener listener;

    public MyRecyclerAdapter(LandInfoClickListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sections, parent, false);
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
        holder.area.setText(landRegister.getArea());
        holder.owner.setText(landRegister.getOwner());

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
            owner = (TextView) view.findViewById(R.id.row_sections_view_owner);
            area = (TextView) view.findViewById(R.id.row_sections_view_area);
            assignment = (TextView) view.findViewById(R.id.row_sections_view_assignment);
            price = (TextView) view.findViewById(R.id.row_sections_view_price);
            description = (TextView) view.findViewById(R.id.row_sections_view_date);
            address = (TextView) view.findViewById(R.id.row_sections_view_address);


            btnShowOnTheMap = (Button) view.findViewById(R.id.row_sections_btn_show_on_the_map);
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

    public interface LandInfoClickListener {
        void onClickShowOnTheMap(View view, int position);

        void onRowClick(View view, int position);
    }

}
