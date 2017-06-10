package com.example.esca.landmarket.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.adapters.MyRecyclerAdapter;
import com.example.esca.landmarket.models.LandInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esca on 10.06.2017.
 */

public class FragmentSections extends Fragment implements View.OnClickListener, MyRecyclerAdapter.MasterClickListener {

    public static final String TAG = "ONTAG";
    private static final String PATH = "/guest/list";
    private RecyclerView myList;
    private MyRecyclerAdapter adapter;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private NextFromFragmentSections listener;
    private boolean isSwipedForRefresh = false;
    private FrameLayout progressFrame;
    private UpdateListTask updateListTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sections, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.frag_first_fab_add_item);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_item_list);
        progressFrame = (FrameLayout) view.findViewById(R.id.frag_progress_bar);
        updateListTask = new UpdateListTask();
        updateListTask.execute();
        fab.setOnClickListener(this);
        adapter = new MyRecyclerAdapter(this);
        myList = (RecyclerView) view.findViewById(R.id.frag_sections_recycler_view);
        myList.setLayoutManager(new LinearLayoutManager(getActivity()));
        myList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        myList.setItemAnimator(new DefaultItemAnimator());
        myList.setAdapter(adapter);
//        myList.addOnItemTouchListener(new MyOnItemTouchListener(getActivity(), myList, new ClickListener() {
//        }));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwipedForRefresh = true;
                updateListTask = new UpdateListTask();
                updateListTask.execute();
            }
        });
        handler = new Handler();
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.frag_first_fab_add_item) {
//            adapter.addUser(new LandInfo());
            listener.onClickNextFromSections(adapter,0,null,true);
        }
    }

    @Override
    public void onClickShowOnTheMap(View view, int position) {
        LandInfo landInfo = adapter.getItemByPosition(position);
        listener.onClickNextFromSections(adapter, position, landInfo, false);
//        adapter.removeUser(position);
    }

    @Override
    public void onRowClick(View view, int position) {
        LandInfo landInfo = adapter.getItemByPosition(position);
        listener.onClickNextFromSections(adapter, position, landInfo, true);
    }


    class UpdateListTask extends AsyncTask<Object, Object, ArrayList<LandInfo>> {

        @Override
        protected ArrayList<LandInfo> doInBackground(Object... params) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AUTH", getActivity().MODE_PRIVATE);
            String token = sharedPreferences.getString("TOKEN", "");
            Log.d(TAG, "getAllMasters: " + token);

            MediaType type = MediaType.parse("application/json; charset=utf-8");
//            RequestBody requestBody = RequestBody.create(type, "");
            Request request = new Request.Builder()
//                    .url("https://landmarket1.herokuapp.com/land/sellerlands")
                    .url("https://landmarket1.herokuapp.com/land/lands")
                    .get()
                    .addHeader("Authorization", token)
                    .build();
            final ArrayList<LandInfo> lands = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    handler.post(new ErrorRequest("wrong Number"));
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    Log.d(TAG, "onResponse: " + response.code());
//                    LandInfoArray landInfoArray = new LandInfoArray();

                    if (response.isSuccessful()) {
                        ArrayList<LandInfo> landInfoArrayList = new ArrayList<LandInfo>();
                        TypeToken<List<LandInfo>> typeToken = new TypeToken<List<LandInfo>>(){};
                        landInfoArrayList = new Gson().fromJson(response.body().string(), typeToken.getType());
                        if (landInfoArrayList != null) {
                            for (LandInfo landInfo : landInfoArrayList) {
                                Log.d(TAG, "loadMasters: " + landInfo.getArea() + " & " + landInfo.getPrice());
                                if (landInfo.getAddress() != null) {
                                    lands.add(landInfo);
                                    handler.post(new LandShow(landInfo));
                                }
                            }
                        }
                    } else if (response.code() == 401) {
                        new ErrorRequest("WTF");
                    }
                }
            });
            return lands;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (adapter != null) {
                adapter.clear();
            }
            if (!isSwipedForRefresh)
                progressFrame.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(ArrayList<LandInfo> landInfo) {
            super.onPostExecute(landInfo);
            swipeRefreshLayout.setRefreshing(false);
            isSwipedForRefresh = false;
            progressFrame.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            updateListTask = null;
            isSwipedForRefresh = false;
            progressFrame.setVisibility(View.GONE);
        }
    }


    class LandShow implements Runnable {
        LandInfo landInfo;

        public LandShow() {
        }

        public LandShow(LandInfo landInfo) {
            this.landInfo = landInfo;
            Log.d(TAG, landInfo.getId());
        }

        @Override
        public void run() {

            Log.d(TAG, "master add to list:   " + landInfo.getAddress());
            LandInfo land = new LandInfo();
            land.setAddress(landInfo.getAddress());
            land.setArea(landInfo.getArea());
            land.setAssignment(landInfo.getAssignment());
            land.setDescription(landInfo.getDescription());
            land.setId(landInfo.getId());
            land.setLatitude(landInfo.getLatitude());
            land.setLongitude(landInfo.getLongitude());
            land.setOwner(landInfo.getOwner());
            land.setPlaceId(landInfo.getPlaceId());
            land.setPrice(landInfo.getPrice());
            adapter.addUser(land);
        }
    }

    private class ErrorRequest implements Runnable {
        protected String s;

        public ErrorRequest(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }


    public void setFragmentListener(NextFromFragmentSections listener) {
        this.listener = listener;
    }

    public interface NextFromFragmentSections {
        void onClickNextFromSections(MyRecyclerAdapter adapter, int position, LandInfo landInfo, boolean bool);
    }

}
