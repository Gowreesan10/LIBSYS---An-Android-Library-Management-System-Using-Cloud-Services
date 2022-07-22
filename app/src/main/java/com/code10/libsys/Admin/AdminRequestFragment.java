package com.code10.libsys.Admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminRequestFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RequestsListenerAdapter requestsListenerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_request, container, false);

        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavViewAdmin);
        BadgeDrawable badgeExplorer = bnv.getOrCreateBadge(R.id.Request);
        badgeExplorer.setVisible(false);

        RecyclerView requestsRecyclerView = view.findViewById(R.id.recyclerViewRequest);
        requestsListenerAdapter = new RequestsListenerAdapter(getActivity().getApplicationContext(), badgeExplorer);
        requestsRecyclerView.setAdapter(requestsListenerAdapter);
        requestsListenerAdapter.setQuery();
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        AdminSharedPreference.getInstance(getActivity().getApplicationContext()).registerSharedPrefListener(this);
        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v("ACTIVE LISTENER", "");
        if (key.equals("Request ArrayList")) {
            requestsListenerAdapter.setQuery();
            Log.v("SET QUERY", "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AdminSharedPreference.getInstance(getActivity().getApplicationContext()).unregisterSharedPrefListener(this);
    }
}

//    private MyReceiver broadcastReceiver;

//        IntentFilter intentFilter = new IntentFilter("com.myApp.CUSTOM_EVENT");
//        broadcastReceiver = new MyReceiver(requestsListenerAdapter);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        IntentFilter intentFilter = new IntentFilter("com.myApp.CUSTOM_EVENT");
////        broadcastReceiver = new BroadcastReceiver() {
////            @Override
////            public void onReceive(Context context, Intent intent) {
////                set();
////            }
////        };
//        broadcastReceiver = new MyReceiver(requestsListenerAdapter);
////        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver,intentFilter);
//    }

//    private void set() {
//        Log.v("MESSAGE", "RECEIVED");
//    }
