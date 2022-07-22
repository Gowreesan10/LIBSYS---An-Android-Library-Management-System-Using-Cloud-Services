package com.code10.libsys.Admin;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.code10.libsys.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoardAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Start on create", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout_admin);

        BottomNavigationView bnv = findViewById(R.id.bottomNavViewAdmin);
        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Request:
                    replaceFragment(new AdminRequestFragment());
                    break;
                case R.id.homeAdmin:
                    replaceFragment(new AdminHomeFragment());
                    break;
                case R.id.profileAdmin:
                    replaceFragment(new AdminProfileFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutAdmin, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        replaceFragment(new AdminRequestFragment());
        replaceFragment(new AdminProfileFragment());
        replaceFragment(new AdminHomeFragment());
    }

}


//    JobScheduler jobScheduler;
//    private FirebaseJobDispatcher firebaseJobDispatcher;

//        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//
//        firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        if (!isRequestListenerServiceRunning()) {
//            Log.v("Admin", "Start service");
//            startJobDispatch();
//            startService(new Intent(this,MyService.class));
//            getApplicationContext().startService(new Intent(this,BarrowRequestListenerService.class));
//        }


//


//    private boolean isRequestListenerServiceRunning() {
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
//            if (BarrowRequestListenerService.class.getName().equals(serviceInfo.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

//    private void startJob() {
//        ComponentName componentName = new ComponentName(this, MyService.class);
//        JobInfo jobInfo = new JobInfo.Builder(101, componentName)
//                .setPersisted(true)
//                .build();
//
//        if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
//            Toast.makeText(this, "Job Started", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void startJobDispatch() {
//        Job job = firebaseJobDispatcher.newJobBuilder()
//                .setService(MyService.class)
//                .setLifetime(Lifetime.FOREVER)
//                .setTag("FDW")
//                .setTrigger(Trigger.NOW)
//                .setReplaceCurrent(false)
//                .setConstraints(Constraint.ON_ANY_NETWORK)
//                .build();
//        firebaseJobDispatcher.mustSchedule(job);
//
//    }
//}