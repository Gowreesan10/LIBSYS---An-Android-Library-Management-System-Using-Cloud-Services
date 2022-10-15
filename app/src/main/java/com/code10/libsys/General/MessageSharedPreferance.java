//package com.code10.libsys.General;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import com.code10.libsys.General.Model.BookView;
//import com.google.common.reflect.TypeToken;
//import com.google.gson.Gson;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class MessageSharedPreferance {
//    private static MessageSharedPreferance singleInstance = null;
//    private static SharedPreferences sharedPreferences;
//    private boolean isListenerRegistered = false;
//
//    private MessageSharedPreferance(Context context) {
//        sharedPreferences = context.getSharedPreferences("Messages", Context.MODE_PRIVATE);
//    }
//
//    public static MessageSharedPreferance getInstance(Context context) {
//        Log.v("user get instance", "");
//        if (singleInstance == null) {
//            singleInstance = new MessageSharedPreferance(context);
//        }
//        return singleInstance;
//    }
//
//    public void deleteDetails() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//        singleInstance = null;
//        Log.v("Successfully deleted messages details", "success");
//    }
//
//    public ArrayList<Map<String, String>> getRecentlyViewdBooks() {
//        Gson gson = new Gson();
//        String savedJson = sharedPreferences.getString("Recent Books", null);
//        Type type = new TypeToken<ArrayList<Map<String, String>>>() {
//        }.getType();
//        return gson.fromJson(savedJson, type);
//    }
//
//    public void saveLibraryDetails(String Title, String Body) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Map<String,String> message = new HashMap<>();
//        message.put("Title",Title);
//        message.put("Body",Body);
//        ArrayList<Map<String, String>> original = getRecentlyViewdBooks();
//        if(original.)
//        editor.putString("Recent Books", saveJson);
//        editor.apply();
//        Log.v("Save recent books", "Sucess");
//    }
//
//    public void registerSharedPrefListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
//        if (!isListenerRegistered) {
//            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
//            isListenerRegistered = true;
//        }
//    }
//
//    public void unregisterSharedPrefListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
//        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
//        isListenerRegistered = true;
//    }
//
//}
//
