package com.code10.libsys.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.Admin.DashBoardAdmin;
import com.code10.libsys.R;
import com.code10.libsys.User.DashBoardUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

//TODO: FINISH SEARCH
public class GetStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        Intent intent;

        if (currentUser != null) {
            if (currentUser.getDisplayName().endsWith("Librarian")) {
                intent = new Intent(this, DashBoardAdmin.class);
                Log.v("Activity", "Open admin from get started");
            } else {
                intent = new Intent(this, DashBoardUser.class);
                Log.v("Activity", "Open user from get started");
            }

            startActivity(intent);
//            finish();
        }

        Button getStarted = findViewById(R.id.btnGetStarted);
        getStarted.setOnClickListener(view -> {
            Toast.makeText(GetStarted.this, "Sign Up", Toast.LENGTH_SHORT).show();
            Log.v("Activity", "Open sign up from get started");
            startActivity(new Intent(GetStarted.this, SignUp.class));
//            finish();
        });

        Button LogIn = findViewById(R.id.btnLogin);
        LogIn.setOnClickListener(view -> {
            Toast.makeText(GetStarted.this, "Sign In", Toast.LENGTH_SHORT).show();
            Log.v("Activity", "Open sign in from get started");
            startActivity(new Intent(GetStarted.this, SignIn.class));
//            finish();
        });
    }
}