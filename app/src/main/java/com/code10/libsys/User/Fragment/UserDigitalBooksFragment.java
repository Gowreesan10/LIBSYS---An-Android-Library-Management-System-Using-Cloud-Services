package com.code10.libsys.User.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.code10.libsys.General.Adapter.SectionPagerAdapter;
import com.code10.libsys.R;
import com.code10.libsys.User.Adapter.EbookAdapter;
import com.google.android.material.tabs.TabLayout;

public class UserDigitalBooksFragment extends Fragment {
//
//    View view;
//    ViewPager viewPager;
//    TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_digital_books, container, false);
//        viewPager = view.findViewById(R.id.viewPager);
//        tabLayout = view.findViewById(R.id.tabLayout);
//        return view;
        View view = inflater.inflate(R.layout.fragment_user_e_book, container, false);

        RecyclerView gridEBooks = view.findViewById(R.id.EBookRecyclerView);
        EbookAdapter gridEBooksAdapter = new EbookAdapter(getActivity().getApplicationContext());
        gridEBooks.setAdapter(gridEBooksAdapter);
        gridEBooks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        setUpViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }
//
//    private void setUpViewPager(ViewPager viewPager) {
//        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
//
//        adapter.addFragment(new DigitalAudioBookFragment(), "Audio Books");
//        adapter.addFragment(new DigitalEBookFragment(), "E-Books");
//
//        viewPager.setAdapter(adapter);
//    }
}