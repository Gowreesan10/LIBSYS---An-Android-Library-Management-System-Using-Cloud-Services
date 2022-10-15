package com.code10.libsys.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.code10.libsys.User.Adapter.EbookAdapter;

public class DigitalEBookFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_e_book, container, false);

        RecyclerView gridEBooks = view.findViewById(R.id.EBookRecyclerView);
        EbookAdapter gridEBooksAdapter = new EbookAdapter(getActivity().getApplicationContext());
        gridEBooks.setAdapter(gridEBooksAdapter);
        gridEBooks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return view;
    }
}