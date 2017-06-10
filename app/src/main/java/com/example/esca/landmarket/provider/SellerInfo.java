package com.example.esca.landmarket.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Esca on 07.06.2017.
 */

public class SellerInfo extends AsyncTask<Void, Void, Void> {


//    private Client client;
    private DownloadClient listener;
    private static final String TAG = "TAG";
    private Context context;
    private Handler handler;
    private static final String PATH = "/client/info";

    public SellerInfo(DownloadClient listener, Context context, Handler handler) {
        this.listener = listener;
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AUTH", context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");
        Log.d(TAG, "getClientInfo: " + token);

        MediaType type = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(type, "");
        Request request = new Request.Builder()
                .url("https://landmarket1.herokuapp.com/seller/infoseller ")
                .get()
                .addHeader("Authorization", token)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.post(new ErrorRequest("Connection Error!"));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                Log.d(TAG, "onResponseClientInfo: " + response.code());
//                Client client = new Client();
                if (response.isSuccessful()) {
                    Seller seller = new Gson().fromJson(response.body().string(),Seller.class);
                    listener.onLoadClient(seller);
                    Log.d(TAG, "onResponse: " + seller.getEmail());
//                    client = gson.fromJson(response.body().string(), Client.class);
//                    listener.onLoadClient(client);
//                    Log.d("TOG", "onResponseClientInfoGetEmail+getName: " + client.getClientEmail() + client.getClientName());
//                    if (client != null) {
//                        handler.post(new RequestOk(client));
//                    }
                } else if (response.code() == 401) {
                    new ErrorRequest("WTF");
                }
            }
        });
        return null;
    }

    class RequestOk implements Runnable {
//        private Client clientOk;

//        public RequestOk(Client clientOk) {
//            this.clientOk = clientOk;
//
//        }

        @Override
        public void run() {
//            listener.onLoadClient(clientOk);
//            Log.d("TEG", "client info: " + clientOk.getClientEmail());
        }
    }

    private class ErrorRequest implements Runnable {
        protected String s;

        public ErrorRequest(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }
    }

    public interface DownloadClient {
        void onLoadClient(Seller seller);
    }

}
