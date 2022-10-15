package com.code10.libsys.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.code10.libsys.General.Model.LibraryDetails;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class AdminSharedPreference {
    private static AdminSharedPreference singleInstance = null;
    private static SharedPreferences sharedPreferences;
    private boolean isListenerRegistered = false;

    private AdminSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("Librarian", Context.MODE_PRIVATE);
    }

    public static AdminSharedPreference getInstance(Context context) {
        Log.v("get instance", "");
        if (singleInstance == null) {
            singleInstance = new AdminSharedPreference(context);
        }
        return singleInstance;
    }

    public void deleteDetails() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        singleInstance = null;
        Log.v("Sucessfully deleted", "success");
    }

    public LibraryDetails getLibraryDetails() {
        Gson gson = new Gson();
        String savedJson = sharedPreferences.getString("Library Details", null);
        Type type = new TypeToken<LibraryDetails>() {
        }.getType();
        Log.v("get library details", "");
        return gson.fromJson(savedJson, type);
    }

    public void saveLibraryDetails(LibraryDetails libraryDetails) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String saveJson = gson.toJson(libraryDetails);
        editor.putString("Library Details", saveJson);
        editor.apply();
        Log.v("Save Lib", "Sucess");
    }

    public void registerSharedPrefListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        if (!isListenerRegistered) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            isListenerRegistered = true;
        }
    }

    public void unregisterSharedPrefListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        isListenerRegistered = true;
    }

}

//    public void saveRequest(Message message) {
//        ArrayList<Message> messageArrayList;
//        messageArrayList = getRequestArrayList();
//        if (messageArrayList == null) {
//            messageArrayList = new ArrayList<>();
//        }
//        messageArrayList.add(message);
//        Log.v("Message Added", "");
//        setRequestArrayList(messageArrayList);
//    }

//    private void deleteRequestArrayList() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        editor.remove("Request ArrayList");
//        editor.apply();
//    }

//    public ArrayList<Message> getRequestArrayList() {
//        Gson gson = new Gson();
//        try {
//            String savedJson = sharedPreferences.getString("Request ArrayList", null);
//            Type type = new TypeToken<ArrayList<Message>>() {
//            }.getType();
//            Log.v("Arraylist returned", "");
//            return gson.fromJson(savedJson, type);
//        } catch (Exception exception) {
//            Log.v("Exception", exception.getMessage());
//            return null;
//        }
//    }

//    public static void setRequestArrayList(ArrayList<Message> messageArrayList) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String saveJson = gson.toJson(messageArrayList);
//        editor.putString("Request ArrayList", saveJson);
//        editor.apply();
//        Log.v("Message saved in preferences", "");
//    }


//    public void saveRequestData(QueryDocumentSnapshot requests) {
//        ArrayList<QueryDocumentSnapshot> requestList = getRequestArrayList();
//        if (requestList == null) {
//            Log.v("Create New", "requestList");
//            requestList = new ArrayList<>();
//        }
//
//        requestList.add(requests);
//        Log.v("SIze of reqList", Integer.toString(requestList.size()));
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String saveJson = gson.toJson(requestList);
//        editor.putString("Request", saveJson);
//        editor.apply();
//    }

//    public ArrayList<QueryDocumentSnapshot> getRequestArrayList() {
//        Gson gson = new Gson();
//        String savedJson = sharedPreferences.getString("Request", null);
//        Type type = new TypeToken<ArrayList<QueryDocumentSnapshot>>() {
//        }.getType();
//        return gson.fromJson(savedJson, type);
//    }