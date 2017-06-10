package com.example.esca.landmarket.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.Login;
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

public class FragmentLogin extends Fragment implements View.OnClickListener {
    private EditText inputLogin, inputPassword;
    private TextView viewCreateAccount;
    private Button loginBtn;
    private FragmentListenerLogin listener;
    private FrameLayout progressFrame;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        inputLogin = (EditText) view.findViewById(R.id.frag_login_edit_login);
        inputPassword = (EditText) view.findViewById(R.id.frag_login_edit_password);
        viewCreateAccount = (TextView) view.findViewById(R.id.frag_login_create_acc);
        loginBtn = (Button) view.findViewById(R.id.frag_login_btn_login);
        progressFrame = (FrameLayout) view.findViewById(R.id.frag_login_progress_frame);
        loginBtn.setOnClickListener(this);
        viewCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.frag_login_btn_login:
                progressFrame.setVisibility(View.VISIBLE);
                String json = new Gson().toJson(new Login(inputLogin.getText().toString(),inputPassword.getText().toString()));
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("EMAIL", inputLogin.getText().toString());
//                editor.commit();
                MediaType type = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(type, json);
                Request request = new Request.Builder()
                        .url("https://landmarket1.herokuapp.com/login/login/")
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
                        if (response.isSuccessful()) {
                            handler.post(new RequestOk());
                            Token token = new Gson().fromJson(response.body().string(), Token.class);

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("TOKEN", token.getToken());
                            editor.commit();
                            Log.d("TAG", "onResponse: " + token.getToken());
                            if (response.code() < 400) {
                                listener.onClickNextFromLogin(true);
                            }
                        } else if (response.code() == 401) {
                            handler.post(new ErrorRequest("Wrong password or login!"));
                        } else handler.post(new ErrorRequest("Server error!"));
                    }
                });
                break;
            case R.id.frag_login_create_acc:
                listener.onClickNextFromLogin(false);
                break;
        }
    }

    public void setFragmentListener(FragmentListenerLogin listener) {
        this.listener = listener;
    }

    public interface FragmentListenerLogin {
        void onClickNextFromLogin(boolean bool);
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
            Toast.makeText(getActivity(), "Login Ok!", Toast.LENGTH_SHORT).show();
        }
    }
}
