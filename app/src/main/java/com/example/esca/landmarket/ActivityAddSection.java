package com.example.esca.landmarket;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivityAddSection extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private ScrollView relativeLayout;
    private FrameLayout frameLayout;
    private ImageView imageFirst, imageSecond, imageThird;
    private Button btnShowOnTheMap;
    private FloatingActionButton fab;
    private AutoCompleteTextView inputAddress;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private boolean goBack = false;
    private Handler handler;
    private EditText inputArea, inputAssignment, inputPrice, inputDescription, inputOwner;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA_FIRST = 1;
    private final int PICK_IMAGE_CAMERA_SECOND = 2;
    private final int PICK_IMAGE_CAMERA_THIRD = 3;
    private final int PICK_IMAGE_GALLERY_FIRST = 4;
    private final int PICK_IMAGE_GALLERY_SECOND = 5;
    private final int PICK_IMAGE_GALLERY_THIRD = 6;

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
        fab = (FloatingActionButton) findViewById(R.id.add_section_fab);
        fab.setImageResource(R.mipmap.ic_save);
        fab.setOnClickListener(this);
//        btnBack = (Button) findViewById(R.id.add_section_btn_back);
//        btnSave = (Button) findViewById(R.id.add_section_btn_add);
//        btnBack.setOnClickListener(this);
//        btnSave.setOnClickListener(this);
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
            super.onBackPressed();
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
                fragment.setAddress(location);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_for_add_frag, fragment, "SHOW_ON_THE_MAP")
                    .addToBackStack("SHOW_ON_THE_MAP")
                    .commit();
        } /*else if (v.getId() == R.id.add_section_btn_back) {
            finish();
        }*/ else if (v.getId() == R.id.add_section_fab) {
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
            String json = new Gson().toJson(new LandRegister(area, assignment, price, description, address, owner));
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
            selectImage(PICK_IMAGE_CAMERA_FIRST, PICK_IMAGE_GALLERY_FIRST);
//            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//            photoPickerIntent.setType("image/*");
//            startActivityForResult(photoPickerIntent, 1);
        } else if (v.getId() == R.id.image_second) {
            selectImage(PICK_IMAGE_CAMERA_SECOND, PICK_IMAGE_GALLERY_SECOND);
        } else if (v.getId() == R.id.image_third) {
            selectImage(PICK_IMAGE_CAMERA_THIRD, PICK_IMAGE_GALLERY_THIRD);
        }
    }

    private void selectImage(final int cameraPosition,final int galleryPosition) {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            switch (cameraPosition){
                                case PICK_IMAGE_CAMERA_FIRST:
                                    startActivityForResult(intent, cameraPosition);
                                    break;
                                case PICK_IMAGE_CAMERA_SECOND:
                                    startActivityForResult(intent, cameraPosition);
                                    break;
                                case PICK_IMAGE_CAMERA_THIRD:
                                    startActivityForResult(intent, cameraPosition);
                                    break;
                            }
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            switch (galleryPosition){
                                case PICK_IMAGE_GALLERY_FIRST:
                                    startActivityForResult(pickPhoto, galleryPosition);
                                    break;
                                case PICK_IMAGE_GALLERY_SECOND:
                                    startActivityForResult(pickPhoto, galleryPosition);
                                    break;
                                case PICK_IMAGE_GALLERY_THIRD:
                                    startActivityForResult(pickPhoto, galleryPosition);
                                    break;
                            }

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA_FIRST || requestCode == PICK_IMAGE_CAMERA_SECOND || requestCode == PICK_IMAGE_CAMERA_THIRD) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                if (requestCode == PICK_IMAGE_CAMERA_FIRST){
                    imageFirst.setImageBitmap(bitmap);
                }else if (requestCode == PICK_IMAGE_CAMERA_SECOND){
                    imageSecond.setImageBitmap(bitmap);
                }else if (requestCode == PICK_IMAGE_CAMERA_THIRD){
                    imageThird.setImageBitmap(bitmap);
                }
//                imageview.setImageBitmap(bitmap);
//                MainActivity.CompleteManager.callComplete(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY_FIRST || requestCode == PICK_IMAGE_GALLERY_SECOND || requestCode == PICK_IMAGE_GALLERY_THIRD ) {
            try {
                Uri selectedImage = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                if (requestCode == PICK_IMAGE_GALLERY_FIRST){
                    imageFirst.setImageBitmap(bitmap);
                }else if (requestCode == PICK_IMAGE_GALLERY_SECOND){
                    imageSecond.setImageBitmap(bitmap);
                }else if (requestCode == PICK_IMAGE_GALLERY_THIRD){
                    imageThird.setImageBitmap(bitmap);
                }
//                imageview.setImageBitmap(bitmap);
//                MainActivity.CompleteManager.callComplete(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

//            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText, Toast.LENGTH_SHORT).show();

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
//        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(ActivityAddSection.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    class RequestOk implements Runnable {

        @Override
        public void run() {
//            progressFrame.setVisibility(View.INVISIBLE);
//            viewResult.setText("Registration Ok!");
//            Toast.makeText(ActivityAddSection.this, "Change successfully", Toast.LENGTH_SHORT).show();
        }
    }

}
