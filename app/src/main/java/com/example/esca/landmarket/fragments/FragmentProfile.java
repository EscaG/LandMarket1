package com.example.esca.landmarket.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.Seller;
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
 * Created by Esca on 29.03.2017.
 */

public class FragmentProfile extends Fragment implements View.OnClickListener {
    private FragmentListenerProfile listener;
    private Button btnBack, btnEdit;
    private ImageView logoImage;
    private TextView viewCompany, viewPhone, viewAddress, viewManagersName, viewTeudat, viewEmail;
    private FrameLayout progressFrame;
    private Handler handler;
    public static final String TAG = "ONTAG";
    private static final String PATH = "/client/info";
    private Seller sellerAfterLogin = new Seller();
    private Seller seller;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        handler = new Handler();
        new GetClientInfo().execute();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressFrame = (FrameLayout) view.findViewById(R.id.frag_my_profile_progress_frame);
        viewCompany = (TextView) view.findViewById(R.id.frag_my_profile_view_company);
        viewPhone = (TextView) view.findViewById(R.id.frag_my_profile_view_phone);
        viewAddress = (TextView) view.findViewById(R.id.frag_my_profile_view_address);
        viewManagersName = (TextView) view.findViewById(R.id.frag_my_profile_view_manager);
        viewTeudat = (TextView) view.findViewById(R.id.frag_my_profile_view_teudat);
        viewEmail = (TextView) view.findViewById(R.id.frag_my_profile_view_email);
        btnBack = (Button) view.findViewById(R.id.frag_my_profile_btn_back);
        btnEdit = (Button) view.findViewById(R.id.frag_my_profile_btn_edit);
        logoImage = (ImageView) view.findViewById(R.id.frag_my_profile_view_image);
        btnEdit.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
        progressFrame.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.frag_my_profile_btn_back) {
            listener.onClickNextFromMyProfile(false, null);
        } else if (view.getId() == R.id.frag_my_profile_btn_edit) {
            listener.onClickNextFromMyProfile(true, seller);
        }
    }



    class GetClientInfo extends AsyncTask<Void, Void, Void> {
        public GetClientInfo() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
            String token = sharedPreferences.getString("TOKEN", "");
            Log.d(TAG, "getClientInfo: " + token);

            MediaType type = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(type, "");
            Request request = new Request.Builder()
                    .url("https://landmarket1.herokuapp.com/seller/infoseller ")
                    .get()
                    .addHeader("Authorization", token)
                    .build();
            final OkHttpClient clientOk = new OkHttpClient();
            clientOk.setConnectTimeout(5, TimeUnit.SECONDS);
            clientOk.setReadTimeout(5, TimeUnit.SECONDS);

            clientOk.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    handler.post(new ErrorRequest("Connection Error!"));
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    Gson gson = new Gson();
                    seller = new Seller();
                    Log.d(TAG, "onResponseClientInfo: " + response.code());
                    if (response.isSuccessful()) {
                        if (seller != null) {
                            seller = gson.fromJson(response.body().string(), Seller.class);
                            Log.d(TAG, "onResponseSellerInfo " + seller.getEmail());
                            if (seller != null) {
                                handler.post(new RequestOk(seller));
                            }
                        }
                    } else if (response.code() == 401) {
                        new ErrorRequest("WTF");
                    }
                }
            });
            return null;
        }
    }

    class RequestOk implements Runnable {
        private Seller seller;

        public RequestOk(Seller seller) {
            this.seller = seller;
        }

        @Override
        public void run() {
            sellerAfterLogin = seller;
            Log.d(TAG, "RequestOk: " + sellerAfterLogin.getEmail());
            viewPhone.setText(sellerAfterLogin.getPhone());
            viewAddress.setText(sellerAfterLogin.getLogin());
            viewEmail.setText(sellerAfterLogin.getEmail());
            viewCompany.setText(sellerAfterLogin.getLogin());
            viewManagersName.setText(sellerAfterLogin.getPassport());
            progressFrame.setVisibility(View.INVISIBLE);
        }
    }

    private class ErrorRequest implements Runnable {
        protected String s;

        public ErrorRequest(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            progressFrame.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }
    public void setFragmentListener(FragmentListenerProfile listener) {
        this.listener = listener;
    }

    public interface FragmentListenerProfile {
        void onClickNextFromMyProfile(boolean bool, Seller seller);
    }
}
