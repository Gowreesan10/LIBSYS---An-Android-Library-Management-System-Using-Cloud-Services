package com.code10.libsys.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Adapter.RequestsListenerAdapter;
import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class RequestedBookFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    private RequestsListenerAdapter requestsListenerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_book, container, false);

        String userName = currentUser.getDisplayName().replace(".User", "");
        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("requester", userName).orderBy("requestTime", Query.Direction.DESCENDING);

        RecyclerView requestsRecyclerView = view.findViewById(R.id.userRecyclerView);
        requestsListenerAdapter = new RequestsListenerAdapter(getActivity().getApplicationContext(), R.layout.user_request_card, null);
        requestsRecyclerView.setAdapter(requestsListenerAdapter);
        requestsListenerAdapter.setQuery(query);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }
}