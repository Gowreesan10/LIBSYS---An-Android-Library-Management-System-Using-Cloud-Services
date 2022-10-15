package com.code10.libsys.User.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.code10.libsys.R;
import com.code10.libsys.User.Fragment.UserBooksFragment;
import com.code10.libsys.User.Fragment.UserHomeFragment;
import com.code10.libsys.User.Fragment.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class DashBoardUser extends FragmentActivity {
    private final String HOME = "Home";
    private final String BORROWBOOK = "Borrow Book";
//    private final String DIGITALBOOK = "Digital Book";
    private final String SETTINGS = "Settings";
    private String Tag = "Home";

    private UserHomeFragment homeFragment;
    private UserBooksFragment bookFragment;
    //   private UserDigitalBooksFragment digitalBooksFragment;
    private UserProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout_user);

        BottomNavigationView bnv = findViewById(R.id.bottomNavViewUser);

        bnv.setOnItemSelectedListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.homeUser:
                    Log.d("CLICK home", "click home");
                    replaceFragment(homeFragment, HOME);
                    break;
                case R.id.BorrowBooksUser:
                    Log.d("CLICK BORROW", "click borrow");
                    replaceFragment(bookFragment, BORROWBOOK);
                    break;
//                case R.id.DigitalBooks:
//                    Log.d("CLICK DIGITAL", "click Digital");
//                    replaceFragment(digitalBooksFragment, DIGITALBOOK);
//                    break;
                case R.id.settings:
                    Log.d("CLICK SETTINGS", "click settings");
                    this.replaceFragment(profileFragment, SETTINGS);
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.findFragmentByTag(Tag);
        fragmentTransaction.hide(Objects.requireNonNull(currentFragment));
        fragmentTransaction.show(fragment);
        Tag = tag;
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().isEmpty()) {
            homeFragment = new UserHomeFragment();
            bookFragment = new UserBooksFragment();
            // digitalBooksFragment = new UserDigitalBooksFragment();
            profileFragment = new UserProfileFragment();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutUser, profileFragment, SETTINGS);
            //    fragmentTransaction.add(R.id.frameLayoutUser, digitalBooksFragment, DIGITALBOOK);
            fragmentTransaction.add(R.id.frameLayoutUser, bookFragment, BORROWBOOK);
            fragmentTransaction.add(R.id.frameLayoutUser, homeFragment, HOME);
            fragmentTransaction.hide(profileFragment);
            //    fragmentTransaction.hide(digitalBooksFragment);
            fragmentTransaction.hide(bookFragment);
            fragmentTransaction.commit();
        }
    }

}
