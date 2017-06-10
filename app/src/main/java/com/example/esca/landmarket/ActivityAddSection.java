package com.example.esca.landmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.esca.landmarket.autocomplite.PlaceAutocompleteAdapter;
import com.example.esca.landmarket.fragments.FragmentMap;
import com.example.esca.landmarket.models.LandRegister;
import com.example.esca.landmarket.models.Token;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ActivityAddSection extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private ScrollView relativeLayout;
    private FrameLayout frameLayout;
    private ImageView imageFirst, imageSecond, imageThird;
    private Button btnShowOnTheMap, btnBack, btnSave;
    private AutoCompleteTextView inputAddress;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private boolean goBack = false;
    private Handler handler;
    private EditText  inputArea, inputAssignment, inputPrice, inputDescription, inputOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_section);
        setTitle("Add section");
        ActionBar bar = getSupportActionBar();
        bar.hide();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        handler = new Handler();
        inputArea = (EditText) findViewById(R.id.add_section_area);
        inputAssignment = (EditText) findViewById(R.id.add_section_assignment);
        inputDescription = (EditText) findViewById(R.id.add_section_description);
        inputPrice = (EditText) findViewById(R.id.add_section_price);
        inputOwner = (EditText) findViewById(R.id.add_section_owner);
        relativeLayout = (ScrollView) findViewById(R.id.container_activity);
        frameLayout = (FrameLayout) findViewById(R.id.container_for_add_frag);
        //btnSearch = (Button) findViewById(R.id.add_section_btn_search);
        inputAddress = (AutoCompleteTextView) findViewById(R.id.add_section_autocomplete);
        imageFirst = (ImageView) findViewById(R.id.image_first);
        imageSecond = (ImageView) findViewById(R.id.image_second);
        imageThird = (ImageView) findViewById(R.id.image_third);
        btnShowOnTheMap = (Button) findViewById(R.id.add_section_btn_show_on_the_map);
        btnBack = (Button) findViewById(R.id.add_section_btn_back);
        btnSave = (Button) findViewById(R.id.add_section_btn_add);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        imageFirst.setOnClickListener(this);
        imageSecond.setOnClickListener(this);
        imageThird.setOnClickListener(this);
        btnShowOnTheMap.setOnClickListener(this);

        inputAddress.setOnItemClickListener(mAutocompleteClickListener);
        inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputAddress.setSelection(0);
            }
        });

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        inputAddress.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            relativeLayout.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.add_section_btn_show_on_the_map) {
            relativeLayout.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
            goBack = true;
            FragmentMap fragment = new FragmentMap();
            if (inputAddress.getText().toString() != null && !inputAddress.getText().toString().equals("")) {
                String location = inputAddress.getText().toString();
//                fragment = FragmentMap.newInstance(location);
                fragment.setAddress(location);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_for_add_frag, fragment, "SHOW_ON_THE_MAP")
                    .addToBackStack("SHOW_ON_THE_MAP")
                    .commit();
        } else if (v.getId() == R.id.add_section_btn_back) {
            finish();
        } else if (v.getId() == R.id.add_section_btn_add) {
            SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
            final String token = sharedPreferences.getString("TOKEN", "");
            Log.d("TAG", "putClientUpdate: " + token);
            MediaType type = MediaType.parse("application/json; charset=utf-8");

            String area = inputArea.getText().toString();
            String assignment = inputAssignment.getText().toString();
            String price = inputPrice.getText().toString();
            String description = inputDescription.getText().toString();
            String address = inputAddress.getText().toString();
            String owner = inputOwner.getText().toString();
            String json = new Gson().toJson(new LandRegister(area,assignment,price,description,address,owner));
            RequestBody body = RequestBody.create(type, json);
            Request request = new Request.Builder()
                    .url("https://landmarket1.herokuapp.com/register/land")
                    .addHeader("Authorization", token)
                    .post(body)
                    .build();
            OkHttpClient clientOK = new OkHttpClient();
            clientOK.setConnectTimeout(5, TimeUnit.SECONDS);
            clientOK.setReadTimeout(5, TimeUnit.SECONDS);

            clientOK.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                    handler.post(new ErrorRequest("Connection error"));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Log.d("TAG", "onResponseClientInfo: " + response.code());
                    if (response.isSuccessful()) {
                        handler.post(new RequestOk());
                        Token token1 = new Gson().fromJson(response.body().string(), Token.class);
                        Log.d("TAG", "onResponse: " + token1.getToken());
                        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TOKEN2", token1.getToken());
                        editor.commit();
                        if (response.code() < 400) {
                            finish();
                        }
                    } else if (response.code() == 401) {
                        handler.post(new ErrorRequest("Wrong data"));
                    } else handler.post(new ErrorRequest("Server error!"));
                }
            });
        } else if (v.getId() == R.id.image_first) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        } else if (v.getId() == R.id.image_second) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 2);
        } else if (v.getId() == R.id.image_third) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = imageReturnedIntent.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                MainActivity.CompleteManager.callComplete(selectedImage);
                imageFirst.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = imageReturnedIntent.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                MainActivity.CompleteManager.callComplete(selectedImage);
                imageSecond.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = imageReturnedIntent.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                MainActivity.CompleteManager.callComplete(selectedImage);
                imageThird.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


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

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText, Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }
    class ErrorRequest implements Runnable {
        private String result;

        public ErrorRequest(String result) {
            this.result = result;
        }

        @Override
        public void run() {
//            progressFrame.setVisibility(View.INVISIBLE);
//            viewResult.setText(result);
            Toast.makeText(ActivityAddSection.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    class RequestOk implements Runnable {

        @Override
        public void run() {
//            progressFrame.setVisibility(View.INVISIBLE);
//            viewResult.setText("Registration Ok!");
            Toast.makeText(ActivityAddSection.this, "Change successfully", Toast.LENGTH_SHORT).show();
        }
    }

}
