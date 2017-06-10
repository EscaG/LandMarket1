package com.example.esca.landmarket.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.Registration;
import com.example.esca.landmarket.models.Token;
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

public class FragmentRegistration extends Fragment implements View.OnClickListener {
    private EditText inputEmail, inputLogin, inputPass, inputConfirm;
    private Button btnregistration, btnCancel;
    private FragmentListenerRegistration listener;
    private FrameLayout progressFrame;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        progressFrame = (FrameLayout) view.findViewById(R.id.frag_registr_progress_frame);

        inputEmail = (EditText) view.findViewById(R.id.frag_registr_input_email);
        inputLogin = (EditText) view.findViewById(R.id.frag_registr_input_login);
        inputPass = (EditText) view.findViewById(R.id.frag_registr_input_pass);
        inputConfirm = (EditText) view.findViewById(R.id.frag_registr_input_confirm);

        btnregistration = (Button) view.findViewById(R.id.frag_registr_btn_registr);
        btnCancel = (Button) view.findViewById(R.id.frag_registr_btn_cancel);
        btnCancel.setOnClickListener(this);
        btnregistration.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.frag_registr_btn_registr) {
            progressFrame.setVisibility(View.VISIBLE);
            MediaType type = MediaType.parse("application/json; charset=utf-8");

            String login = inputLogin.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPass.getText().toString();
            String confirm = inputConfirm.getText().toString();
            Registration registration = new Registration(login,email,password,confirm);
            String json = new Gson().toJson(registration);
            RequestBody body = RequestBody.create(type, json);
            Request request = new Request.Builder()
                    .url("https://landmarket1.herokuapp.com/register/seller")
//                    .header("Content-Type" , "application/json")
                    .post(body)
                    .build();
//            new HttpHelper().AsyncPost("https://hair-salon-personal.herokuapp.com/register/client/", json, new IResponseCallback() {
//                @Override
//                public void handler(IResponse response) {
//                    Log.d("REGISTER", response.getBody());
//                    if (response.getStatus() == 200) {
//                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("TOKEN", response.getBody());
//                        editor.commit();
//                        listener.onClickNextFromCreateAccount(true);
//                    } else if (response.getStatus() == 409) {
//                        handler.post(new ErrorRequest("User already exists!"));
//                    } else handler.post(new ErrorRequest("Server error!"));
//
//                }
//            });


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
                    if (response.isSuccessful()) {
                        handler.post(new FragmentRegistration.RequestOk());
                        Gson gson = new Gson();
                        Token token = gson.fromJson(response.body().string(), Token.class);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TOKEN", token.getToken());
                        editor.commit();
                        if (response.code() < 400) {
                            listener.onClickNextFromRegistration(true);
                        }

                    } else if (response.code() == 409) {
                        handler.post(new ErrorRequest("User already exists!"));
                    } else handler.post(new ErrorRequest("Server error!"));
                }
            });
        } else if (view.getId() == R.id.frag_registr_btn_cancel) {
            listener.onClickNextFromRegistration(false);
        }
    }

    public void setFragmentListener(FragmentListenerRegistration listener) {
        this.listener = listener;
    }

    public interface FragmentListenerRegistration {
        void onClickNextFromRegistration(boolean bool);
    }

    class ErrorRequest implements Runnable {
        private String result;

        public ErrorRequest(String result) {
            this.result = result;
        }

        @Override
        public void run() {
            progressFrame.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    class RequestOk implements Runnable {

        @Override
        public void run() {
            progressFrame.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Registration Ok!", Toast.LENGTH_SHORT).show();
        }
    }
}
