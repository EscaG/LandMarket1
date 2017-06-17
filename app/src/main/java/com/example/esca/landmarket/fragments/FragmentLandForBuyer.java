package com.example.esca.landmarket.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.esca.landmarket.R;
import com.example.esca.landmarket.models.LandInfo;

/**
 * Created by Esca on 12.05.2017.
 */

public class FragmentLandForBuyer extends Fragment implements View.OnClickListener {
    private LandInfo landInfo;
    private FragmentListenerLandForBuyer listener;
    private ViewPager myViewPager;

    public void setLand(LandInfo landInfo) {
        this.landInfo = landInfo;
    }

    private RadioButton first;
    private RadioButton second;
    private RadioButton third;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_land_for_buyer, container, false);
        myViewPager = (ViewPager) view.findViewById(R.id.my_view_pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        myViewPager.setAdapter(adapter);
        final RadioButton first = (RadioButton) view.findViewById(R.id.first);
        final RadioButton second = (RadioButton) view.findViewById(R.id.second);
        final RadioButton third = (RadioButton) view.findViewById(R.id.third);

        Log.d("TAG", "getCurrentItem: " + myViewPager.getCurrentItem());
        myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("TAG", "onPageScrolled: " + position);

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        first.setChecked(true);
                        break;
                    case 1:
                        second.setChecked(true);
                        break;
                    case 2:
                        third.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        if(myViewPager.getCurrentItem() == 0){
//
//        }

        return view;
    }

    private void settView(View view) {
        TextView denotation = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_denotation);
        TextView address = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_address);
        TextView area = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_area);
        TextView price = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_price);
        TextView description = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_description);
        TextView owner = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_owner);
        TextView sellers = (TextView) view.findViewById(R.id.frag_land_for_buyer_view_sellers);

        first = (RadioButton) view.findViewById(R.id.first);
        second = (RadioButton) view.findViewById(R.id.second);
        third = (RadioButton) view.findViewById(R.id.third);

//        Button btnBack = (Button) view.findViewById(R.id.frag_land_for_buyer_btn_back);
//        btnBack.setOnClickListener(this);
        if (landInfo != null) {
            denotation.setText(landInfo.getAssignment());
            address.setText(landInfo.getAddress());
            area.setText(landInfo.getArea());
            price.setText(landInfo.getPrice());
            description.setText(landInfo.getDescription());
            owner.setText(landInfo.getOwner());
            sellers.setText(landInfo.getOwner());
        }
    }

    private int position1 = 0;

    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setFragmentListener(FragmentListenerLandForBuyer listener) {
        this.listener = listener;
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        String[] names = {"Vasya", "Petya", "Dima"};
        int[] pictures = {R.mipmap.cat, R.mipmap.ic_image, R.mipmap.earth111};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(pictures[position], position);
        }


        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }
//        public interface AdapterListener{
//            void onClickFromAdapter(int position);
//        }
    }

    public interface FragmentListenerLandForBuyer {
        void onClickNextFromLandForBuyer(int position);
    }
}
