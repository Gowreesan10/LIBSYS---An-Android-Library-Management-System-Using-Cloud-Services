//package com.code10.libsys.Admin;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.code10.libsys.General.Model.Message;
//
//public class MyReceiver extends BroadcastReceiver {
//
////    RequestsListenerAdapter requestsListenerAdapter;
////
////    public MyReceiver() {
////    }
////
////    public MyReceiver(RequestsListenerAdapter requestsListenerAdapter) {
////        this.requestsListenerAdapter = requestsListenerAdapter;
////    }
////
////    @Override
////    public void onReceive(Context context, Intent intent) {
////        Log.v("MESSAGE", "RECEIVED");
////        Message message = (Message) intent.getSerializableExtra("Message");
//////        AdminSharedPreference.getInstance(context.getApplicationContext()).saveRequest(message);
//////        requestsListenerAdapter.setQuery();
////    }
//}