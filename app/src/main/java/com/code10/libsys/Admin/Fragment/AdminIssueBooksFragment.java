package com.code10.libsys.Admin.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.General.Adapter.SectionPagerAdapter;
import com.code10.libsys.General.Model.Message;
import com.code10.libsys.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class AdminIssueBooksFragment extends Fragment {
    private static AdminIssueBooksFragment adminRequestFragment = null;
    private final ArrayList<Message> requests = new ArrayList<>();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    View view;
    ViewPager viewPager;
    BadgeDrawable requestBadge;
    private TabLayout tabLayout;

    public AdminIssueBooksFragment() {
    }

    public static AdminIssueBooksFragment getInstance() {
        if (adminRequestFragment == null) {
            adminRequestFragment = new AdminIssueBooksFragment();
        }
        return adminRequestFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_issue_books, container, false);
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
        requestBadge = tabLayout.getTabAt(0).getOrCreateBadge();
        requestBadge.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googred));
        setBadges(requestBadge);
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new RequestFragment(), "Requests");
        adapter.addFragment(new IssuedBookFragment(), "Issued Books");

        viewPager.setAdapter(adapter);
    }

    private void setBadges(BadgeDrawable requestedBooks) {
        String LibName = AdminSharedPreference.getInstance(getActivity().getApplicationContext()).getLibraryDetails().getLibraryName();
        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("libName", LibName).whereEqualTo("status", "Pending");
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
                        break;
                    case REMOVED:
                        onDocumentRemoved(change);
                        break;
                }
            }
            setNumberBadges(requestedBooks);
        });
    }

    protected void onDocumentRemoved(DocumentChange change) {
        requests.remove(change.getOldIndex());
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
        int count = requests.size();
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavViewAdmin);
        BadgeDrawable badgeExplorer = bnv.getOrCreateBadge(R.id.Request);
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
}

/*
 private void setBadges(BadgeDrawable requestedBooks) {
        String LibName = AdminSharedPreference.getInstance(getActivity().getApplicationContext()).getLibraryDetails().getLibraryName();
        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("libName", LibName).whereNotEqualTo("status", "Timeout");
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
                        break;
                    case REMOVED:
                        onDocumentRemoved(change);
                        break;
                }
            }
            setNumberBadges(requestedBooks);
        });
    }

    protected void onDocumentRemoved(DocumentChange change) {
        requests.remove(change.getOldIndex());
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
        ArrayList<Message> TEMP = new ArrayList<>();
        for (Message message : requests) {
            if (message.getStatus().equals("Pending")) {
                if (!TEMP.contains(message)) {
                    count++;
                    TEMP.add(message);
                    Log.v("pending", message.getRequestTime().toString() + message.getReturnDate().toString());
                }
            }
        }
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavViewAdmin);
        BadgeDrawable badgeExplorer = bnv.getOrCreateBadge(R.id.Request);
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
}
 */