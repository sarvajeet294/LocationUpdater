package com.sarvajeet.locationtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmManagerService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmManagerService", "Welcome");
        Intent service1 = new Intent(context, Utils.class);
        context.startService(service1);

        /*Log.i("App", "called receiver method");
        try{
            Utils utils= new Utils(context);
            utils.setLocation();
            utils.sendLocation();
            utils.sendNotification();
        }catch(Exception e){
            e.printStackTrace();
        }*/
    }
}