package com.sarvajeet.locationtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Create_Fragment extends Fragment {

    AppLocationService appLocationService;
    Button locationButton, submitButton;
    Location location;
    TextView locationTv;
    ConnectionDetector cd;
    Boolean isInternetPresent;
    String userName, emailId, userId;
    EditText userid, username, email;
    private ProgressDialog pDialog;
    private PendingIntent pendingIntent;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    public static final String LOGIN_URL = "http://www.knowyourcollege-gov.in/ws/data/insertgeodata.php";
    public static final String TAG_SUCCESS = "response";
    Context context;
    SharedPreferences sp;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context= getActivity().getApplicationContext();
        sp = context.getSharedPreferences(AppConstants.key_shared_pref, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Now in","Create----onCreateView----");
        View x= inflater.inflate(R.layout.fragment_create, null);
        appLocationService = new AppLocationService(context);
        locationButton = (Button) x.findViewById(R.id.getLocationBtn);
        submitButton = (Button) x.findViewById(R.id.submitBtn);
        locationTv = (TextView) x.findViewById(R.id.locationTv);
        username = (EditText) x.findViewById(R.id.usernameET);
        userid= (EditText) x.findViewById(R.id.useridET);
        email = (EditText) x.findViewById(R.id.emailEt);

        userid.setText(sp.getString(AppConstants.key_user_id,""));
        username.setText(sp.getString(AppConstants.key_user_name,""));
        email.setText(sp.getString(AppConstants.key_email_id,""));

        cd = new ConnectionDetector(context);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    AppConstants.latitude = String.valueOf(latitude);
                    AppConstants.longitude = String.valueOf(longitude);

                    locationTv.setText("Latitude: " + AppConstants.latitude + " Longitude: " + AppConstants.longitude);
                    locationTv.setVisibility(View.VISIBLE);
                } else {
                    location = appLocationService
                            .getLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        AppConstants.latitude = String.valueOf(latitude);
                        AppConstants.longitude = String.valueOf(longitude);

                        locationTv.setText("Latitude: " + AppConstants.latitude + " Longitude: " + AppConstants.longitude);
                        locationTv.setVisibility(View.VISIBLE);
                    } else {
                        showSettingsAlert("GPS");
                    }
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetPresent = cd.isConnectingToInternet(); // true or false

                userId= userid.getText().toString();
                userName = username.getText().toString();
                emailId = email.getText().toString();

                if (isInternetPresent) {
                    if (!userId.equalsIgnoreCase("") && !userName.equalsIgnoreCase("") && !emailId.equalsIgnoreCase("")) {

                        SharedPreferences.Editor editor= sp.edit();
                        editor.putString(AppConstants.key_user_id, userid.getText().toString());
                        editor.putString(AppConstants.key_user_name, username.getText().toString());
                        editor.putString(AppConstants.key_email_id, email.getText().toString());
                        editor.putString(AppConstants.key_latitude, AppConstants.latitude);
                        editor.putString(AppConstants.key_longitude, AppConstants.longitude);
                        editor.putBoolean(AppConstants.key_shared_saved, true);
                        editor.apply();

                        new AttemptLogin().execute();
                    } else
                        Toast.makeText(context, "Please provide data!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Please turn on internet!", Toast.LENGTH_LONG).show();

                Calendar calendar= Calendar.getInstance();
                Intent myIntent = new Intent(context, AlarmManagerService.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 5000, pendingIntent);
            }
        });
        return x;
    }

    class AttemptLogin extends AsyncTask<String, String, String> {
        //Before starting background thread Show Progress Dialog

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Attempting for login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            Log.i("hd", "Login: pre-execute completed");
        }

        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag

            Log.d("Latitude: ", AppConstants.latitude);
            Log.d("Latitude: ", AppConstants.longitude);

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", "1"));
                params.add(new BasicNameValuePair("userid", userId));
                params.add(new BasicNameValuePair("username", userName));
                params.add(new BasicNameValuePair("emailId", emailId));
                params.add(new BasicNameValuePair("lat", AppConstants.latitude));
                params.add(new BasicNameValuePair("lon", AppConstants.longitude));
                // Log.d("request!", "starting");
                Log.i("hd", " Login: Sending Parameters");
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "GET", params);
                // checking  log for json response
                Log.i("hd", "Login: Json Response");
                Log.i("hd", json.toString());
                // success tag for json
                return json.getString(TAG_SUCCESS);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        //Once the background process is done we need to Dismiss the progress dialog asap

        protected void onPostExecute(String success) {
            pDialog.dismiss();
            Log.i("hd", "Login: Post Request Closed");
            /*if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }*/
            if (success.equalsIgnoreCase("Inserted Successfully")) {
                Toast.makeText(context, "Successfully sent!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Request failed!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

}
