package com.code10.libsys.User;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.code10.libsys.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoardUser extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout_user);

        replaceFragment(new UserHomeFragment());

        BottomNavigationView bnv = findViewById(R.id.bottomNavViewUser);
        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeUser:
                    Log.d("CLICK home", "click home");
                    replaceFragment(new UserHomeFragment());
                    break;
                case R.id.BorrowBooksUser:
                    Log.d("CLICK BORROW", "click borrow");
                    replaceFragment(new UserBookFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new UserProfileFragment());
                    break;
            }
            return true;
        });


//        new Handler(this.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BadgeDrawable badgeDrawable = bnv.getOrCreateBadge(R.id.)
//            }
//        })
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutUser, fragment);
        fragmentTransaction.commit();
    }

    //    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof SearchView) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    v.clearFocus();
////                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//
//    }
}
