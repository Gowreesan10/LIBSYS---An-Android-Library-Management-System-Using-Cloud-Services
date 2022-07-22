package com.code10.libsys.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.code10.libsys.R;
import com.code10.libsys.auth.GetStarted;
import com.google.firebase.auth.FirebaseAuth;

public class AdminProfileFragment extends Fragment {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(view -> {
            mFirebaseAuth.signOut();
            startActivity(new Intent(getActivity(), GetStarted.class));
            getActivity().finish();
        });

        return view;
    }
}