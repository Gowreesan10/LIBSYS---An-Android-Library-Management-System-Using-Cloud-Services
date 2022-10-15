package com.code10.libsys.User.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.code10.libsys.General.Adapter.SectionPagerAdapter;
import com.code10.libsys.General.Model.Message;
import com.code10.libsys.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class UserBooksFragment extends Fragment {
    private final ArrayList<Message> requests = new ArrayList<>();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    BadgeDrawable requestedBooks;
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_books, container, false);
        viewPager = view.findViewById(R.id.viewPager2);
        tabLayout = view.findViewById(R.id.tabLayout2);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        requestedBooks = tabLayout.getTabAt(0).getOrCreateBadge();
        requestedBooks.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googred));
        setBadges(requestedBooks);
    }

    private void setBadges(BadgeDrawable requestedBooks) {
        String userName = currentUser.getDisplayName().replace(".User", "");
        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("requester", userName).orderBy("requestTime", Query.Direction.DESCENDING);
        query.addSnapshotListener((querySnapshots, e) -> {
            if (e != null) {
                Log.w("LISTEN TAG", "onEvent:error", e);
                return;
            }
            Log.v("TAB", querySnapshots.getDocumentChanges().size() + "");
            for (DocumentChange change : querySnapshots.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        onDocumentAdded(change);
                        break;
                    case MODIFIED:
                        onDocumentModified(change);
                    case REMOVED:
                        break;
                }
            }
            setNumberBadges(requestedBooks);
        });
    }

    protected void onDocumentAdded(DocumentChange change) {
        requests.add(change.getNewIndex(), change.getDocument().toObject(Message.class));
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            requests.set(change.getOldIndex(), change.getDocument().toObject(Message.class));
        } else {
            requests.remove(change.getOldIndex());
            requests.add(change.getNewIndex(), change.getDocument().toObject(Message.class));
        }
    }

    private void setNumberBadges(BadgeDrawable requestedBooks) {
        int count = 0;
        for (Message message : requests) {
            if ((!message.getNotificationViewed()) & (!message.getStatus().equals("Pending"))) {
                count++;
                Log.v(message.getStatus().equals("Pending") + "", message.getBookName());
            }
        }
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavViewUser);
        BadgeDrawable badgeExplorer = bnv.getOrCreateBadge(R.id.BorrowBooksUser);
        Log.v(badgeExplorer.toString(), count + "");
        if (count == 0) {
            badgeExplorer.setVisible(false);
            requestedBooks.setVisible(false);
        } else {
            badgeExplorer.setVisible(true);
            requestedBooks.setVisible(true);
        }
        requestedBooks.setNumber(count);
        badgeExplorer.setNumber(count);
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new RequestedBookFragment(), "Requested Books");
        adapter.addFragment(new BorrowedBookFragment(), "Borrowed Books");

        viewPager.setAdapter(adapter);
    }
}