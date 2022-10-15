package com.code10.libsys.Admin.Activity;

import android.content.Intent;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureAct extends CaptureActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        Intent intent = new Intent(this, DashBoardAdmin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}