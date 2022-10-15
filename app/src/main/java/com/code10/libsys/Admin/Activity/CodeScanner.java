package com.code10.libsys.Admin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Pattern;

public class CodeScanner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isbn_scanner);
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning...");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.v("REQUEST CODE", requestCode + "");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (result.getContents() != null) {
                builder.setTitle("Scanning Result");
                builder.setMessage(result.getContents());
                builder.setCancelable(false);
                builder.setPositiveButton("Scan Again", (dialog, which) -> scanCode())
                        .setNegativeButton("Finish", (dialog, which) -> {
                            Intent intent;
                            Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
                            if (!pattern.matcher(result.getContents()).matches()) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("Copy", result.getContents());
                                setResult(Activity.RESULT_OK, returnIntent);
                                Log.v("done", result.getContents());
                            } else {
                                intent = new Intent(getBaseContext(), GetBookDetails.class);
                                intent.putExtra("Search", result.getContents());
                                startActivity(intent);
                            }
                            finish();
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}