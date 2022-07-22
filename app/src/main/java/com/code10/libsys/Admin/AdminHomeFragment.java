package com.code10.libsys.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHomeFragment extends Fragment {

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        AdminDashboardAdapter adminDashboardAdapter = new AdminDashboardAdapter(getContext());
        RecyclerView options = view.findViewById(R.id.recyclerAdminHome);
        options.setAdapter(adminDashboardAdapter);
        options.setLayoutManager(new GridLayoutManager(getContext(), 2));

        TextView txt = view.findViewById(R.id.txtGowreeshan);

        txt.setText(currentUser.getDisplayName().replace(".Librarian",""));
        return view;
    }

}