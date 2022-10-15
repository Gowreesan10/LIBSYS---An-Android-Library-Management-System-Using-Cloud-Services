package com.code10.libsys.Admin.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.General.auth.GetStarted;
import com.code10.libsys.General.auth.updateLibrarianProfile;
import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminProfileFragment extends Fragment {
    //removed finish
    private static AdminProfileFragment adminProfileFragment = null;
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog loadingBar;
    ProgressDialog progressDialog;
    private View view;
    private FirebaseUser currentUser;

    public AdminProfileFragment() {
    }

    public static AdminProfileFragment getInstance() {
        if (adminProfileFragment == null) {
            adminProfileFragment = new AdminProfileFragment();
        }
        return adminProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
        currentUser = mFirebaseAuth.getCurrentUser();

        CircleImageView userProfileimageCircleImageView = view.findViewById(R.id.userProfileimage);
        Picasso.get().load(currentUser.getPhotoUrl()).into(userProfileimageCircleImageView);

        TextView userName = view.findViewById(R.id.userName);
        userName.setText(currentUser.getDisplayName().replace(".Librarian", ""));


        db.collection("Libraries").document(currentUser.getDisplayName().replace(".Librarian", "")).addSnapshotListener((value, error) -> {
            Log.v("LIB100", value.getData().toString());
            Map<String, Object> map = value.getData();
            TextView location = view.findViewById(R.id.txtlocation);
            location.setText(map.get("address").toString());
            TextView telnoView1 = view.findViewById(R.id.telnoView);
            telnoView1.setText(map.get("telephoneNo").toString());
        });

        Button logout = view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(view -> {
            logout();
        });

        Button btnMyProfile = view.findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), updateLibrarianProfile.class);
            startActivity(i);
        });

        Button btnForgetPassword = view.findViewById(R.id.btnForgetPassword);
        btnForgetPassword.setOnClickListener(v -> {
            showRecoverPasswordDialog();
        });

        Button btnPrivacyPolicy = view.findViewById(R.id.btnPrivacyPolicy);
        btnPrivacyPolicy.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://pages.flycricket.io/libsys/privacy.html"));
            startActivity(i);
        });

        return view;
    }

    private void logout() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Logging Out....");
        progressDialog.show();
        db.collection("Libraries").document(currentUser.getDisplayName().replace(".Librarian", "")).update("token", null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AdminSharedPreference.getInstance(getActivity().getApplicationContext()).deleteDetails();
                mFirebaseAuth.signOut();
                progressDialog.dismiss();
                startActivity(new Intent(getActivity(), GetStarted.class));
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

//    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder alaBuilder = new AlertDialog.Builder(getContext());
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_forget_password, null);
        alaBuilder.setView(view1);
        EditText emailet = view1.findViewById(R.id.etEmailaddress);

        alaBuilder.setPositiveButton("Recover", (dialog, which) -> {
            String email = emailet.getText().toString().trim();
            beginRecovery(email);
        });

        alaBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alaBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void beginRecovery(String email) {
        loadingBar = new ProgressDialog(getContext());
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            loadingBar.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Email Successfully sent", Toast.LENGTH_LONG).show();
                logout();
            } else {
                Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            loadingBar.dismiss();
            Toast.makeText(getContext(), "Error Failed", Toast.LENGTH_LONG).show();
        });
    }

}