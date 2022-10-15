package com.code10.libsys.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.code10.libsys.User.Adapter.BorrowedBooksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BorrowedBookFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

    public BorrowedBookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String userName = currentUser.getDisplayName().replace(".User", "");
        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("requester", userName).whereEqualTo("status", "Issued");
        View view = inflater.inflate(R.layout.fragment_borrowed_book, container, false);
        RecyclerView requestsRecyclerView = view.findViewById(R.id.userRecyclerView);
        BorrowedBooksAdapter borrowedBooksAdapter = new BorrowedBooksAdapter(getActivity().getApplicationContext(), null);
        borrowedBooksAdapter.setQuery(query);
        requestsRecyclerView.setAdapter(borrowedBooksAdapter);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }
}