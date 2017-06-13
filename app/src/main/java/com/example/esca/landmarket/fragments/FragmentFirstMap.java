package com.example.esca.landmarket.fragments;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.autocomplite.PlaceAutocompleteAdapter;
import com.example.esca.landmarket.models.LandInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Esca on 14.05.2017.
 */

public class FragmentFirstMap extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private ArrayList<String> str = new ArrayList<String>();
    private String[] strTest;
    private Handler handler;
    private static final String PATH = "/guest/list";
    //    private MasterArray masterArray = new MasterArray();
    public static final String TAG = "ONTAG";
    private Geocoder geocoder;
    private LatLng latLng;
    private Button btnSearch;
    private AutoCompleteTextView inputAddress;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private LinearLayout linear;
    private String location = "";
    private Map<LandInfo, LatLng> landLatLngMap = new HashMap<>();
    private MapView mMapView;
    private ShowLoadLand listener;
    private ArrayList<LandInfo> landInfoArrayList = new ArrayList<LandInfo>();
    private ImageView imgSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_map, null);
        mMapView = (MapView) view.findViewById(R.id.map_first_fragment);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
//        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_first_fragment);
//        mapFragment.getMapAsync(this);

        strTest = getResources().getStringArray(R.array.addresses);
        handler = new Handler();

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), 0 /* clientId */, this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {

        }
        imgSearch = (ImageView) view.findViewById(R.id.map_first_img_search);
        imgSearch.setOnClickListener(this);
//        btnSearch = (Button) view.findViewById(R.id.map_first_btn_search);
        inputAddress = (AutoCompleteTextView) view.findViewById(R.id.map_first_input_search_address);
        inputAddress.setOnItemClickListener(mAutocompleteClickListener);
        inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputAddress.setSelection(0);
            }
        });
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        inputAddress.setAdapter(mAdapter);
        geocoder = new Geocoder(getActivity().getBaseContext());
//        btnSearch.setOnClickListener(this);

        linear = (LinearLayout) view.findViewById(R.id.linear);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ACTION", getActivity().MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("ACTION", false);
        if (!result) {
            linear.setVisibility(View.VISIBLE);
            if (landLatLngMap.isEmpty()) {
                new GetAllMasters().execute();
                Log.d(TAG, "onCreateView: 1");
            } else {
//                handler.post(new FillMap());
                Log.d(TAG, "onCreateView: 2");
                new GetAllMasters().execute();
            }

        } else {
            linear.setVisibility(View.GONE);
        }

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
//        static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);
//        googleMap.addMarker(new MarkerOptions().position(MELBOURNE).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        googleMap.addMarker(new MarkerOptions().position(latLng).title(master.getEmail()).snippet(master.getAddresses()).icon(BitmapDescriptorFactory.fromResource(R.drawable.directory_icon)));
//        googleMap.setBuildingsEnabled(true);
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: " + marker.getTitle());
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Master master = new Master();
        LandInfo landInfo = new LandInfo();
//        ArrayList<Master> masters = masterArray.getMasters();
        for (LandInfo landInfo1 : landInfoArrayList) {
            if (landInfo1.getOwner().equals(marker.getTitle())) {
                landInfo = landInfo1;
                break;
            }
        }
        listener.showLand(landInfo);
    }

    //Bitmap btm = BitmapFactory.decodeResource(this.getResources(), R.drawable.directory_icon);
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_first_img_search:
                List<Address> addressList = null;
                location = inputAddress.getText().toString();

                if (null != googleMap) {
                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(getActivity());
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(location).draggable(true));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                }
                break;
        }
    }

    public void setSelectedMasterListene(ShowLoadLand listener) {
        this.listener = listener;
    }

    public interface ShowLoadLand {
        void showLand(LandInfo landInfo);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class GetAllMasters extends AsyncTask<Void, Void, Void> {
        public GetAllMasters() {
        }

        @Override
        protected void onPreExecute() {
//            landLatLngMap = new HashMap<>();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
            String token = sharedPreferences.getString("TOKEN", "");
            Log.d(TAG, "getAllMasters: " + token);

            MediaType type = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(type, "");
            Request request = new Request.Builder()
                    .url("https://landmarket1.herokuapp.com/land/lands")
                    .get()
                    .addHeader("Authorization", token)
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    handler.post(new ErrorRequest("wrong Number"));
                }

                @Override
                public void onResponse(Response response) throws IOException {
//                    Log.d(TAG, "onResponse: " + response.code());

                    if (response.isSuccessful()) {

                        TypeToken<List<LandInfo>> typeToken = new TypeToken<List<LandInfo>>() {
                        };
                        landInfoArrayList = new Gson().fromJson(response.body().string(), typeToken.getType());

                        for (LandInfo landInfo : landInfoArrayList) {
                            Log.d(TAG, "addMarkersToMap: " + landInfo.getAddress() + landInfo.getPrice());
                            if (!landInfo.getAddress().equals("")) {
                                try {
                                    List<Address> address = geocoder.getFromLocationName(landInfo.getAddress(), 1);
                                    Address location = address.get(0);
//                                        location.getLatitude();
//                                        location.getLongitude();
                                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    landLatLngMap.put(landInfo, latLng);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        handler.post(new FillMap());
                    } else if (response.code() == 401) {
                        new ErrorRequest("WTF");
                    }
                }
            });
            return null;
        }

    }

    class FillMap implements Runnable {
        LatLng latLng;

        public FillMap(){

        }
//        private Map<LandInfo, LatLng> landLatLngMap = new HashMap<>();
//
//        private GoogleMap googleMap;
//        public FillMap(LatLng latLng) {
//            this.latLng = latLng;
//        }
//
//        public FillMap(Map<LandInfo, LatLng> landLatLngMap, GoogleMap googleMap) {
//            this.landLatLngMap = landLatLngMap;
//            this.googleMap = googleMap;
//        }

        @Override
        public void run() {

            //   try {
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ACTION", getActivity().MODE_PRIVATE);
//                boolean result = sharedPreferences.getBoolean("ACTION", false);
//                if (!result) {

            for (Map.Entry<LandInfo, LatLng> entry : landLatLngMap.entrySet()) {
                Log.d(TAG, "run: " + landLatLngMap.isEmpty() + "  " + entry.getKey().getAddress() + entry.getValue());
                googleMap.addMarker(new MarkerOptions().position(entry.getValue()).title(entry.getKey().getOwner()).snippet(entry.getKey().getAddress()));

//                        if (marker.getSnippet() != null || !marker.getSnippet().equals("")) {
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity().getApplicationContext(), R.style.Theme_AppCompat_DayNight_Dialog);
                        LayoutInflater inflater = (LayoutInflater) wrapper.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.info_window_for_map, null);
                        TextView name = (TextView) v.findViewById(R.id.info_window_name);
                        TextView address = (TextView) v.findViewById(R.id.info_window_address);
                        ImageView image = (ImageView) v.findViewById(R.id.info_window_image);

                        name.setText(marker.getTitle());
                        address.setText(marker.getSnippet());
                        return v;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
//                            v = getLayoutInflater().inflate(R.layout.marker_window, null);
//                            imageView = (ImageView) v.findViewById(R.id.imageView);
//                            Picasso.with(this).load(chosenMarker.getUrl()).into(imageView);
                        marker.showInfoWindow();
                        return null;
                    }
                });

//                        }
            }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }
    }


    private class ErrorRequest implements Runnable {
        protected String s;

        public ErrorRequest(String s) {
            this.s = s;
        }

        @Override
        public void run() {
//            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see GeoDataApi#getPlaceById(GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getActivity().getApplicationContext(), "Clicked: " + primaryText, Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    public ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        String placeId = "";

        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            final Place place = places.get(0);
            Log.d("PlaceId:", String.valueOf(place.getPlaceTypes().get(0)));
            placeId = String.valueOf(place.getPlaceTypes().get(0));

        }
    };

}
