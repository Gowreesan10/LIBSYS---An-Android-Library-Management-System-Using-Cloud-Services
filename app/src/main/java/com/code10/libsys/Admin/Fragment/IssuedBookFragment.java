package com.code10.libsys.Admin.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Adapter.IssuedBookAdapter;
import com.code10.libsys.R;


public class IssuedBookFragment extends Fragment {

    public IssuedBookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_issued_book, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerIssuedBooks);
        IssuedBookAdapter issuedBookAdapter = new IssuedBookAdapter(getContext());
        recyclerView.setAdapter(issuedBookAdapter);
        issuedBookAdapter.setQuery();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }
}