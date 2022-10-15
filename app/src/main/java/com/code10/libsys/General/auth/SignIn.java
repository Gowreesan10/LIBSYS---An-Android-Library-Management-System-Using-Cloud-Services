package com.code10.libsys.General.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.Admin.Activity.DashBoardAdmin;
import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.General.Model.LibraryDetails;
import com.code10.libsys.General.Service.FCMReceiver;
import com.code10.libsys.R;
import com.code10.libsys.User.Activity.DashBoardUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class SignIn extends AppCompatActivity implements  OnCompleteListener<Void> {
//implements SharedPreferences.OnSharedPreferenceChangeListener
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
      //  AdminSharedPreference.getInstance(getApplicationContext()).registerSharedPrefListener(this);

        progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setMessage("Sign in....");
        progressDialog.setCanceledOnTouchOutside(false);
        EditText emailEditText = findViewById(R.id.etSigninEmail);
        EditText passwordEditText = findViewById(R.id.etSigninPassword);
        progressBar = findViewById(R.id.progressBar);
        Button signIn = findViewById(R.id.btnSignIn);

        signIn.setOnClickListener(view -> {
//            Toast.makeText(SignIn.this, "Sign In", Toast.LENGTH_SHORT).show();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (validityCheck(email, password)) {
                signInAccount(email, password);
            }
        });

        ImageView back = findViewById(R.id.imageBack);
        back.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, GetStarted.class));
            finish();
        });
    }

    public void signInAccount(String email, String password) {

        progressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SIGN IN", "signInWithEmail:success");
                        currentUser = mFirebaseAuth.getCurrentUser();
                        if (currentUser.getDisplayName().endsWith("Librarian")) {
                            FCMReceiver.shareTokenToServer(getApplicationContext(), this);
                        } else {
                            progressDialog.dismiss();
                            Intent intent = new Intent(SignIn.this, DashBoardUser.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finishAffinity();
                        }
                    } else {
                        progressDialog.dismiss();
                        Log.w("SIGN IN", "signInWithEmail:failure", task.getException());
                        Toast.makeText(SignIn.this, "SignIn With Email failure", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(SignIn.this, "SignIn With Email failure", Toast.LENGTH_SHORT).show();
                });
    }

    private void getLibDetails() {
        db.collection("Libraries").document(currentUser.getDisplayName().replace(".Librarian", "")).get(Source.SERVER)
                .addOnSuccessListener(documentReference -> {
                    LibraryDetails libraryDetails = documentReference.toObject(LibraryDetails.class);
                    AdminSharedPreference.getInstance(getApplicationContext()).saveLibraryDetails(libraryDetails);
                    progressDialog.dismiss();
                    startActivity(new Intent(SignIn.this, DashBoardAdmin.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Log.w("UserDetailUpdateFail", "Error adding document", e);
                    progressDialog.dismiss();
                });
    }

    private boolean validityCheck(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        progressDialog.dismiss();
//        startActivity(new Intent(SignIn.this, DashBoardAdmin.class));
//        finishAffinity();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // AdminSharedPreference.getInstance(getApplicationContext()).unregisterSharedPrefListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        getLibDetails();
    }
}

