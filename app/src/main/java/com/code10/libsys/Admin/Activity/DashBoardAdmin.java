package com.code10.libsys.Admin.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.code10.libsys.Admin.Fragment.AdminHomeFragment;
import com.code10.libsys.Admin.Fragment.AdminIssueBooksFragment;
import com.code10.libsys.Admin.Fragment.AdminProfileFragment;
import com.code10.libsys.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoardAdmin extends AppCompatActivity {

    private final String HOME = "Home";
    private final String REQUEST = "Request";
    private final String PROFILE = "Profile";
    private String Tag = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Start on create", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout_admin);

        BottomNavigationView bnv = findViewById(R.id.bottomNavViewAdmin);
        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Request:
                    replaceFragment(AdminIssueBooksFragment.getInstance(), REQUEST);
                    break;
                case R.id.homeAdmin:
                    replaceFragment(AdminHomeFragment.getInstance(), HOME);
                    break;
                case R.id.profileAdmin:
                    replaceFragment(AdminProfileFragment.getInstance(), PROFILE);
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.findFragmentByTag(Tag);
        fragmentTransaction.hide(currentFragment);
        fragmentTransaction.show(fragment);
        Tag = tag;
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().isEmpty()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutAdmin, AdminProfileFragment.getInstance(), PROFILE);
            fragmentTransaction.add(R.id.frameLayoutAdmin, AdminIssueBooksFragment.getInstance(), REQUEST);
            fragmentTransaction.add(R.id.frameLayoutAdmin, AdminHomeFragment.getInstance(), HOME);
            fragmentTransaction.hide(AdminIssueBooksFragment.getInstance());
            fragmentTransaction.hide(AdminProfileFragment.getInstance());
            fragmentTransaction.commit();
        }
    }

}