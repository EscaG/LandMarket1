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
import android.widget.TextView;
import android.widget.Toast;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.adapters.MyRecyclerAdapter;
import com.example.esca.landmarket.models.LandInfo;
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
 * Created by Esca on 21.05.2017.
 */

public class FragmentSectionEdit extends Fragment implements View.OnClickListener {
    private EditText inputAssignment, inputPrice, inputDescription;
    private TextView viewAddress, viewArea, viewOwner;
    private MyRecyclerAdapter adapter;
    private int position;
    private LandInfo landInfo;
    private Button btnSave;
    private Handler handler;
    public static final String TAG = "ONTAG";
    private FragmentListenerSectionEdit listener;

    public void setAdapterPosition(MyRecyclerAdapter adapter, int position, LandInfo landInfo) {
        this.adapter = adapter;
        this.position = position;
        this.landInfo = landInfo;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section_edit, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        btnSave = (Button) view.findViewById(R.id.frag_btn_save);
        btnSave.setOnClickListener(this);
        inputAssignment = (EditText) view.findViewById(R.id.frag_section_edit_input_assignment);
        inputDescription = (EditText) view.findViewById(R.id.frag_section_edit_input_description);
        inputPrice = (EditText) view.findViewById(R.id.frag_section_edit_input_price);
        viewAddress = (TextView) view.findViewById(R.id.frag_section_edit_view_address);
        viewArea = (TextView) view.findViewById(R.id.frag_section_edit_view_area);
        viewOwner = (TextView) view.findViewById(R.id.frag_section_edit_view_owner);
        inputAssignment.setText(landInfo.getAssignment());
        inputDescription.setText(landInfo.getDescription());
        inputPrice.setText(landInfo.getPrice());
        viewAddress.setText(landInfo.getAddress());
        viewOwner.setText(landInfo.getOwner());
        viewArea.setText(landInfo.getArea());


        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.frag_btn_save) {

//            adapter.updateContact(master, position);
            getActivity().getSupportFragmentManager().popBackStack();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
            String token = sharedPreferences.getString("TOKEN", "");
            Log.d(TAG, "putMasterUpdate: " + token);
            MediaType type = MediaType.parse("application/json; charset=utf-8");
            String assignment = inputAssignment.getText().toString();
            String price = inputPrice.getText().toString();
            String description = inputDescription.getText().toString();
            String address = viewAddress.getText().toString();
            String area = viewArea.getText().toString();
            String owner =  viewOwner.getText().toString();

            String json = new Gson().toJson(new LandInfo(0,0,"",landInfo.getId(),area,assignment,price,description,address, owner));
            RequestBody body = RequestBody.create(type, json);
            Request request = new Request.Builder()
                    .url("https://landmarket1.herokuapp.com/land/upgrade")
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
                    Log.d(TAG, "onResponseLandUpdate " + response.code());
                    if (response.isSuccessful()) {
                        if (response.code() < 400) {
                            handler.post(new RequestOk());
//                            listener.onClickNextFromSectionEdit();
                        }
                    } else if (response.code() == 409) {
                        handler.post(new ErrorRequest("Wrong data"));
                    } else handler.post(new ErrorRequest("Server error!"));
                }
            });
        }
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
//            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    class RequestOk implements Runnable {

        @Override
        public void run() {
//            progressFrame.setVisibility(View.INVISIBLE);
//            viewResult.setText("Registration Ok!");
//            Toast.makeText(getActivity(), "Change successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void setFragmentListener(FragmentListenerSectionEdit listener) {
        this.listener = listener;
    }

    public interface FragmentListenerSectionEdit {
        void onClickNextFromSectionEdit();
    }
}
