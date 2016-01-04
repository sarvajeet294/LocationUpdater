package com.sarvajeet.locationtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;


public class Update_Fragment extends Fragment {

    Button stopServiceButton, startServiceButton;
    ConnectionDetector cd;
    Boolean isInternetPresent;
    private PendingIntent pendingIntent;

    // JSON parser class
    Context context;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View x= inflater.inflate(R.layout.fragment_update, null);
        startServiceButton= (Button) x.findViewById(R.id.startServiceBtn);
        stopServiceButton= (Button) x.findViewById(R.id.stopServiceBtn);
        cd = new ConnectionDetector(context);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetPresent = cd.isConnectingToInternet(); // true or false
                if (isInternetPresent) {
                    if(!sp.getString(AppConstants.key_user_id,"").equalsIgnoreCase("")
                            && !sp.getString(AppConstants.key_user_name,"").equalsIgnoreCase("")
                            && !sp.getString(AppConstants.key_email_id,"").equalsIgnoreCase("")){

                        Calendar calendar= Calendar.getInstance();
                        Intent myIntent = new Intent(context, AlarmManagerService.class);
                        pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

                        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 5000, pendingIntent);

                        Snackbar.make(x, "Sync started!", Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Please provide data first!", Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(context, "Please turn on Internet!", Toast.LENGTH_SHORT).show();
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AlarmManagerService.class);
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(sender);

                Snackbar.make(x, "Sync stopped!", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return x;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context= getActivity().getApplicationContext();
        sp = context.getSharedPreferences(AppConstants.key_shared_pref, Context.MODE_PRIVATE);
    }


}
