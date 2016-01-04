package com.sarvajeet.locationtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils extends Service {

    public static NotificationManager mManager;
    public static Location location;
    public AppLocationService appLocationService;
    public Context context;
    Boolean isInternetPresent;
    ConnectionDetector cd;
    SharedPreferences sp;
    String userName, emailId, userId;
    JSONParser jsonParser;
    String TAG = "Utils";

    @Override
    public void onCreate() {
        Log.d(TAG, "utils create");
        this.context = this.getApplicationContext();
        appLocationService = new AppLocationService(context);
        cd = new ConnectionDetector(context);
        sp = context.getSharedPreferences(AppConstants.key_shared_pref, MODE_PRIVATE);
        userName = sp.getString(AppConstants.key_user_name, "");
        userId = sp.getString(AppConstants.key_user_id, "");
        emailId = sp.getString(AppConstants.key_email_id, "");
        jsonParser = new JSONParser();
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "utils Start");
        setLocation();
        sendLocation();
        sendNotification();
        super.onStart(intent, startId);
    }

    @SuppressWarnings("static-access")
    public void sendNotification() {
        Log.d(TAG, "SendNotificarion");
        mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, MainActivity.class);
        Notification notification = new Notification(R.drawable.ic_launcher, "Location Synced!", System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, "Location Updated", AppConstants.latitude + "--" + AppConstants.longitude, pendingNotificationIntent);
        mManager.notify(0, notification);
    }

    public void setLocation() {
        Log.d(TAG, "SetLocation");
        double latitude, longitude;
        location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            AppConstants.latitude = String.valueOf(latitude);
            AppConstants.longitude = String.valueOf(longitude);

            //locationTv.setText("Latitude: " + AppConstants.latitude + " Longitude: " + AppConstants.longitude);
            //locationTv.setVisibility(View.VISIBLE);
        } else {
            location = appLocationService
                    .getLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                AppConstants.latitude = String.valueOf(latitude);
                AppConstants.longitude = String.valueOf(longitude);

                //locationTv.setText("Latitude: " + AppConstants.latitude + " Longitude: " + AppConstants.longitude);
                //locationTv.setVisibility(View.VISIBLE);
            } else {
                AppConstants.latitude = "";
                AppConstants.longitude = "";
            }
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(AppConstants.key_latitude, AppConstants.latitude);
        editor.putString(AppConstants.key_longitude, AppConstants.longitude);
        editor.commit();
    }

    public void sendLocation() {
        Log.d(TAG, "SendLocation");
        isInternetPresent = cd.isConnectingToInternet(); // true or false


        userName = sp.getString(AppConstants.key_user_name, "");
        userId = sp.getString(AppConstants.key_user_id, "");
        emailId = sp.getString(AppConstants.key_email_id, "");

        Log.d(TAG, userId + "--" + userName + "--" + emailId);

        if (isInternetPresent) {
            if (!userId.equalsIgnoreCase("") && !userName.equalsIgnoreCase("") && !emailId.equalsIgnoreCase("")) {
                new AttemptLogin().execute();
            } else
                Log.d(TAG, "utils Please provide data!");
        } else
            Log.d(TAG, "utils Please turn on internet!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class AttemptLogin extends AsyncTask<Void, Void, Void> {
        //Before starting background thread Show Progress Dialog

        protected Void doInBackground(Void... args) {
            // TODO Auto-generated method stub
            // here Check for success tag

            Log.d("Utils Latitude: ", AppConstants.latitude);
            Log.d("Utils Latitude: ", AppConstants.longitude);


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("type", "2"));
            params.add(new BasicNameValuePair("userId", userId));
            params.add(new BasicNameValuePair("username", userName));
            params.add(new BasicNameValuePair("emailId", emailId));
            params.add(new BasicNameValuePair("lat", AppConstants.latitude));
            params.add(new BasicNameValuePair("lon", AppConstants.longitude));
            // Log.d("request!", "starting");
            Log.i("hd", " Login: Sending Parameters");
            JSONObject json = jsonParser.makeHttpRequest(Create_Fragment.LOGIN_URL, "GET", params);
            // checking  log for json response
            Log.i("utils hd", "Login: Json Response");
            Log.i("utils hd", json.toString());
            // success tag for json
            return null;
        }

    }
}
