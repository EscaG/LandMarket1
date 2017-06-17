package com.example.esca.landmarket.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.esca.landmarket.R;

import java.util.Random;


public class PageFragment extends Fragment {

    private static final String TITLE = "title";
    private String title;
    private int bgColor;
    private int pistures;
    private int position;

    public static PageFragment newInstance(int title, int position) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
//        args.putString(TITLE, title);
        args.putInt(TITLE, title);
        args.putInt("POS", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            title = getArguments().getString(TITLE);
            pistures = getArguments().getInt(TITLE);
            position = getArguments().getInt("POS");
        }

        Random rnd = new Random();
        bgColor = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView titleTxt = (TextView) view.findViewById(R.id.fragmet_title);
//        FrameLayout mainContainer = (FrameLayout) view.findViewById(R.id.fragment_main_container);
//        titleTxt.setText(title);
//        mainContainer.setBackgroundColor(bgColor);
        ImageView imageView = (ImageView) view.findViewById(R.id.row_page_view_input_image);
        imageView.setImageResource(pistures);
//        RadioButton first = (RadioButton) view.findViewById(R.id.first);
//        RadioButton second = (RadioButton) view.findViewById(R.id.second);
//        RadioButton third = (RadioButton) view.findViewById(R.id.third);
//        switch (position) {
//            case 0:
//                first.setChecked(true);
//                break;
//            case 1:
//                second.setChecked(true);
//                break;
//            case 2:
//                third.setChecked(true);
//                break;
//        }
    }
}
