package com.code10.libsys.User.Fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.code10.libsys.General.auth.GetStarted;
import com.code10.libsys.General.auth.UpdateUserProfile;
import com.code10.libsys.R;
import com.code10.libsys.User.UserSharedPreferance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public ProgressDialog loginprogress;
    ProgressDialog loadingBar;
    TextView location;
    TextView dob;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile, container, false);
        CircleImageView profileImage = view.findViewById(R.id.userProfileimage);
        Picasso.get().load(firebaseUser.getPhotoUrl()).into(profileImage);

        TextView Name = view.findViewById(R.id.userName);
        Name.setText(firebaseUser.getDisplayName().replace(".User", ""));
        Button btnMyProfile = view.findViewById(R.id.btnMyProfile);
        Button btnForgetPassword = view.findViewById(R.id.btnForgetPassword);
//        Button btnViewAllLibraries = view.findViewById(R.id.btnViewAllLibraries);
        Button btnPrivacyPolicy = view.findViewById(R.id.btnPrivacyPolicy);
        Button logout = view.findViewById(R.id.btnLogout);
        location = view.findViewById(R.id.txtlocation);
        dob = view.findViewById(R.id.dob);

        btnMyProfile.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), UpdateUserProfile.class);
            startActivity(i);
        });

        btnForgetPassword.setOnClickListener(v -> showRecoverPasswordDialog());
        logout.setOnClickListener(view -> {
            logout();
        });

        btnPrivacyPolicy.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://pages.flycricket.io/libsys/privacy.html"));
            startActivity(i);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseFirestore.collection("Users").document(firebaseUser.getDisplayName()).addSnapshotListener((value, error) -> {
            Map<String, Object> userData = value.getData();
            Log.v("userDta",userData.toString());
            location.setText(userData.get("Address").toString());
            dob.setText("Born on " + userData.get("DOB").toString());
        });
    }

    private void logout() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Logging Out....");
        progressDialog.show();
        firebaseFirestore.collection("Users").document(firebaseUser.getDisplayName()).update("token", null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFirebaseAuth.signOut();
                UserSharedPreferance.getInstance(getActivity().getApplicationContext()).deleteDetails();
                progressDialog.dismiss();
                Intent intent = new Intent(getActivity(), GetStarted.class);
                startActivity(intent);
                requireActivity().finishAffinity();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }
        });

    }

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
