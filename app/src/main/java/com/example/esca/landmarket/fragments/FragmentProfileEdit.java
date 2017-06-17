package com.example.esca.landmarket.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esca.landmarket.MainActivity;
import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.Seller;
import com.example.esca.landmarket.models.SellerUpdate;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Esca on 02.05.2017.
 */

public class FragmentProfileEdit extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    private Button btnBack, btnSave, btnLogo;
    private FragmentListenerFromProfileEdit listener;
    private ImageView image;
    private EditText inputPhone, inputAddress, inputManager, inputTeudat, inputEmail, inputPassword, inputConfirm;
    private Handler handler;
    public static final String TAG = "ONTAG";
    private static final String PATH = "/client/update";
    private FrameLayout progressFrame;
    private Seller seller;

    public void setClientInfo(Seller seller) {
        this.seller = seller;
        Log.d("TEG", seller.getEmail());

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit,container,false);
        handler = new Handler();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressFrame = (FrameLayout) view.findViewById(R.id.frag_profile_edit_progress_frame);
        btnBack = (Button) view.findViewById(R.id.frag_profile_edit_btn_back);
        btnSave = (Button) view.findViewById(R.id.frag_profile_edit_btn_save);
        btnLogo = (Button) view.findViewById(R.id.frag_profile_edit_btn_logo);
        image = (ImageView) view.findViewById(R.id.frag_profile_edit_image_view);
        inputPhone = (EditText) view.findViewById(R.id.frag_profile_edit_input_phone);
        inputAddress = (EditText) view.findViewById(R.id.frag_profile_edit_input_address);
        inputManager = (EditText) view.findViewById(R.id.frag_profile_edit_input_manager);
        inputTeudat = (EditText) view.findViewById(R.id.frag_profile_edit_input_teudat);
        inputEmail = (EditText) view.findViewById(R.id.frag_profile_edit_input_email);
        inputPassword = (EditText) view.findViewById(R.id.frag_profile_edit_input_pass);
        inputConfirm = (EditText) view.findViewById(R.id.frag_profile_edit_input_confirm);
        inputConfirm.setOnEditorActionListener(this);
        inputPhone.setText(seller.getPhone());
        inputManager.setText(seller.getPassport());
        inputAddress.setText(seller.getLogin());
        inputEmail.setText(seller.getEmail());
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnLogo.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frag_profile_edit_btn_back:
                listener.onClickNextFromProfileEdit("back", "", "", null);
                break;
            case R.id.frag_profile_edit_btn_save:
                progressFrame.setVisibility(View.VISIBLE);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
                String token = sharedPreferences.getString("TOKEN", "");
                Log.d(TAG, "puClientUpdate: " + token);
                MediaType type = MediaType.parse("application/json; charset=utf-8");

                String clientPhoneNumber = inputPhone.getText().toString();
                String clientEmail = inputEmail.getText().toString();
                String clientPassword = inputPassword.getText().toString();
                String clientName = inputManager.getText().toString();
                String clientLastName = inputEmail.getText().toString();

                final Seller seller = new Seller(clientPhoneNumber,clientEmail,clientPassword,clientName,clientLastName,clientPhoneNumber);
                final SellerUpdate sellerUpdate = new SellerUpdate(clientPassword, clientName, clientPhoneNumber);
                String json = new Gson().toJson(sellerUpdate);
                RequestBody body = RequestBody.create(type, json);
                Request request = new Request.Builder()
                        .url("https://landmarket1.herokuapp.com/seller/upgrade")
                        .addHeader("Authorization", token)
                        .put(body)
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
                        Log.d(TAG, "onResponseClientInfo: " + response.code());
                        if (response.isSuccessful()) {
                            handler.post(new RequestOk());
                            if (response.code() < 400) {
                                listener.onClickNextFromProfileEdit("", "save", "", seller);
                            }
                        } else if (response.code() == 401) {
                            handler.post(new ErrorRequest("Wrong data"));
                        } else handler.post(new ErrorRequest("Server error!"));
                    }
                });
//                listener.onClickNextFromEditMyProfile("","save","");
                break;
            case R.id.frag_profile_edit_btn_logo:
                listener.onClickNextFromProfileEdit("", "", "logo", null);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.CompleteManager.listener = new MainActivity.IImageCompleteListener() {
            @Override
            public void onComplete(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }
        };
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            progressFrame.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
            String token = sharedPreferences.getString("TOKEN", "");
            Log.d(TAG, "puClientUpdate: " + token);
            MediaType type = MediaType.parse("application/json; charset=utf-8");

            String clientPhoneNumber = inputPhone.getText().toString();
            String clientEmail = inputEmail.getText().toString();
            String clientPassword = inputPassword.getText().toString();
            String clientName = inputManager.getText().toString();
            String clientLastName = inputEmail.getText().toString();

            final Seller seller = new Seller(clientPhoneNumber,clientEmail,clientPassword,clientName,clientLastName,clientPhoneNumber);
            final SellerUpdate sellerUpdate = new SellerUpdate(clientPassword, clientName, clientPhoneNumber);
            String json = new Gson().toJson(sellerUpdate);
            RequestBody body = RequestBody.create(type, json);
            Request request = new Request.Builder()
                    .url("https://landmarket1.herokuapp.com/seller/upgrade")
                    .addHeader("Authorization", token)
                    .put(body)
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
                    Log.d(TAG, "onResponseClientInfo: " + response.code());
                    if (response.isSuccessful()) {
                        handler.post(new RequestOk());
                        if (response.code() < 400) {
                            listener.onClickNextFromProfileEdit("", "save", "", seller);
                        }
                    } else if (response.code() == 401) {
                        handler.post(new ErrorRequest("Wrong data"));
                    } else handler.post(new ErrorRequest("Server error!"));
                }
            });
            return true;
        }
        return false;
    }

    public interface FragmentListenerFromProfileEdit{
        void onClickNextFromProfileEdit(String back, String save, String logo, Seller client);
    }
    public void setFragmentListener(FragmentListenerFromProfileEdit listener){
        this.listener = listener;
    }

    class ErrorRequest implements Runnable {
        private String result;

        public ErrorRequest(String result) {
            this.result = result;
        }

        @Override
        public void run() {
            progressFrame.setVisibility(View.INVISIBLE);
//            viewResult.setText(result);
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    class RequestOk implements Runnable {

        @Override
        public void run() {
            progressFrame.setVisibility(View.INVISIBLE);
//            viewResult.setText("Registration Ok!");
            Toast.makeText(getActivity(), "Change successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
