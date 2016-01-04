package com.sarvajeet.locationtracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map_Fragment extends Fragment {

    Activity activity;
    LatLng current_loc;
    private GoogleMap map;
    Context context;
    MapView mapView;

    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Now in","----onCreateView----");
        current_loc = new LatLng(Double.parseDouble(sp.getString(AppConstants.key_latitude,"0.0"))
                ,Double.parseDouble(sp.getString(AppConstants.key_longitude,"0.0")));
        final View x= inflater.inflate(R.layout.fragment_maps, null);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            // TODO handle this situation
        }

        mapView= (MapView)x.findViewById(R.id.map);
        mapView.onCreate(AppConstants.bundle);
        setUpMapIfNeeded(x);
        //initializeMap();
        // Inflate the layout for this fragment
        return x;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Now in", "----onCreate----");
        AppConstants.bundle = savedInstanceState;
    }

    private void setUpMapIfNeeded(View inflatedView) {
        Log.d("Now in", "----setUpMapIfNeeded----");
        map=null;
        if (map == null) {
            Log.d("Now in", "----Null Map----");
            map = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
        }
        setUpMap();

/*        if (map == null) {
            map = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
        }
        setUpMap();*/
    }

    private void setUpMap() {
        Log.d("Now in", "----setUpMap----");
        map.addMarker(new MarkerOptions().position(current_loc).title("Hamburg").snippet("Here is Hamburg!!"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_loc, 10));
        map.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);
    }

    @Override
    public void onResume() {
        Log.d("Now in", "----onResume----");
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        Log.d("Now in", "----onPause----");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d("Now in", "----onDestroy----");
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Now in", "----onAttach----");
        this.activity=activity;
        context= activity.getApplicationContext();
        sp = context.getSharedPreferences(AppConstants.key_shared_pref, Context.MODE_PRIVATE);
    }

}
