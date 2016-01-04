package com.sarvajeet.locationtracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class Tab_Fragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    Context context;
    SharedPreferences sp;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context=getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sp=context.getSharedPreferences(AppConstants.key_shared_pref, Context.MODE_PRIVATE);
        View x =  inflater.inflate(R.layout.fragment_tab,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        if(sp.getBoolean(AppConstants.key_shared_saved, false)){
            viewPager.setCurrentItem(1);
        }

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        // Inflate the layout for this fragment
        return x;
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new Create_Fragment();
                case 1 : return new Update_Fragment();
                case 2 : return new Map_Fragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Create";
                case 1 :
                    return "Update";
                case 2 :
                    return "Map";
            }
            return null;
        }
    }

}
