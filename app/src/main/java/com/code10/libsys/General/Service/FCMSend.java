package com.code10.libsys.General.Service;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.code10.libsys.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "----------------------------";

    public static void pushNotification(Context context, String token, String title, String body) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("to", token);
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            notificationObject.put("icon", R.mipmap.ic_launcher_foreground);
            jsonObj.put("notification", notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObj, response -> {
                Log.v("SEND", response.toString());
                if (title.equals("Borrow Request")) {
                    Toast.makeText(context, title + " successfully sent", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Toast.makeText(context, title + " sending Failure", Toast.LENGTH_SHORT).show();
                Log.v("SEND", "Failure");
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Key=" + SERVER_KEY);
                    return params;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
