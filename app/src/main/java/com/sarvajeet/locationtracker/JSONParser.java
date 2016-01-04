package com.sarvajeet.locationtracker;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jsonObj;
    static String json = "";

    // default no argument constructor for jsonpaser class
    public JSONParser() {
    }

    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {
        // Make HTTP request
        try {
            // checking request method
            if (method.equals("POST")) {
                // now defaultHttpClient object
                Log.d("makeHttpRequest", "POST");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                String message = httpResponse.getStatusLine().getReasonPhrase();

                Log.d("Status Code: ", String.valueOf(responseCode));
                Log.d("Reason Phrase: ", message);

                HttpEntity httpEntity = httpResponse.getEntity();

                is = httpEntity.getContent();
            } else if (method.equals("GET")) {
                // request method is GET
                Log.d("hd", "GET");
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder str = new StringBuilder();
            String strLine = null;
            while ((strLine = reader.readLine()) != null) {
                str.append(strLine);
            }
            is.close();
            json = str.toString();
            Log.d("OUTPUT", json);
        } catch (Exception e) {

        } // now will try to parse the string into JSON object
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {

        }
        return jsonObj;
    }

}
