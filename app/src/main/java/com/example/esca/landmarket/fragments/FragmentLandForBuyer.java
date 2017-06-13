package com.example.esca.landmarket.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.LandInfo;

/**
 * Created by Esca on 12.05.2017.
 */

public class FragmentLandForBuyer extends Fragment implements View.OnClickListener {
    private LandInfo landInfo;
    private FragmentListenerLandForBuyer listener;

    public void setLand(LandInfo landInfo) {
        this.landInfo = landInfo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_land_for_buyer, container, false);
        settView(view);

        return view;
    }

    private void settView(View view) {
        TextView denotation = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_denotation);
        TextView address = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_address);
        TextView area = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_area);
        TextView price = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_price);
        TextView description = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_description);
        TextView owner = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_owner);
        TextView sellers = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_sellers);
//        Button btnBack = (Button) view.findViewById(R.id.frag_land_for_buyer_btn_back);
//        btnBack.setOnClickListener(this);
        if (landInfo != null) {
            denotation.setText(landInfo.getAssignment());
            address.setText(landInfo.getAddress());
            area.setText(landInfo.getArea());
            price.setText(landInfo.getPrice());
            description.setText(landInfo.getDescription());
            owner.setText(landInfo.getOwner());
            sellers.setText(landInfo.getOwner());
        }
    }

    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().popBackStack();
    }
    public void setFragmentListener(FragmentListenerLandForBuyer listener) {
        this.listener = listener;
    }

    public interface FragmentListenerLandForBuyer {
        void onClickNextFromLandForBuyer(boolean bool);
    }
}
