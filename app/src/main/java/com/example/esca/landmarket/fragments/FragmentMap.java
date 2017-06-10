package com.example.esca.landmarket.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.esca.landmarket.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Esca on 29.03.2017.
 */

public class FragmentMap extends Fragment implements OnMapReadyCallback {
    //    private Button btnSearch;
    private AutoCompleteTextView inputSearch;
    private GoogleMap googleMap;
    private FragmentListenerFromMap listener;
    private String address;

//    public static FragmentMap newInstance(String location) {
//        Bundle bundle = new Bundle();
//        bundle.putString("LOCATION", location);
//        FragmentMap fragment = new FragmentMap();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    public void setAddress(String address){
        this.address = address;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_add_section);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        inputSearch = (AutoCompleteTextView) view.findViewById(R.id.frag_map_input_search_address);
//        btnSearch = (Button) view.findViewById(R.id.frag_map_btn_search);
       // btnSearch.setOnClickListener(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
//        if(this.getArguments().getString("LOCATION") != null){
//            String location = this.getArguments().getString("LOCATION");

        List<Address> addressList = null;
        if (null != googleMap) {

            if (address != null || !address.equals("")) {
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(this.address).draggable(true));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
//    }
    }

    public void setFragmentListener(FragmentListenerFromMap listener){
        this.listener = listener;
    }
    public interface FragmentListenerFromMap{
        void onClickNextFromMap();
    }
}
