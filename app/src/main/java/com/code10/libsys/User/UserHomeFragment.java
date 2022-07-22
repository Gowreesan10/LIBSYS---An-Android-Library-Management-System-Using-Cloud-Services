package com.code10.libsys.User;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.code10.libsys.R;
import com.code10.libsys.SearchDatabaseAdapter;
import com.code10.libsys.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeFragment extends Fragment implements Util.updateRecyclerView, Util.callBack {

    private final FirebaseFirestore userDb = FirebaseFirestore.getInstance();
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> mylist;
    private SearchView searchView;
    private RecyclerView databaseRecyclerView;
    private View layoutRecyclerView;
    private View layoutListview;
    private ImageView back;
    private CircleImageView profile;
    private ViewPager2 viewPager2;
    private final Handler sliderHandler = new Handler();
    private ListenerRegistration listener;
    private View view;
    private AlwaysListenerBookAdapter categoryBooksAdapter;
    private RecyclerView categoryBooks;
    private AlwaysListenerBookAdapter MostPopularBookAdapter;
    private CatagoryRecyclerviewSelecterAdapter catagorySelecterAdapter;
    private TextView seeAllCategory;
    private TextView seeAllPopular;
    private ListView listView;
    private ProgressBar progressBar;

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user_home, container, false);

        setSlider();
        setCategorySelecter();
        setCategory();
        setMostPopularBooks();

        searchView = view.findViewById(R.id.txtSearchAuthor);
        seeAllCategory = view.findViewById(R.id.txtSeeAllCategory);
        seeAllPopular = view.findViewById(R.id.txtSeeAllPopularBooks);
        profile = view.findViewById(R.id.imageEllipse10);
        Picasso.get().load(currentUser.getPhotoUrl()).into(profile);

        layoutListview = view.findViewById(R.id.listviewKey);
        layoutRecyclerView = view.findViewById(R.id.layout_recyclerview);
        databaseRecyclerView = view.findViewById(R.id.idRVBooks);
        progressBar = view.findViewById(R.id.progress_search);
        back = view.findViewById(R.id.imageArrowback);

        layoutListview.setVisibility(View.GONE);
        layoutRecyclerView.setVisibility(View.GONE);

        mylist = new ArrayList<>();
        mylist.add("C");
        mylist.add("Java");
        mylist.add("Advanced java");
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mylist);

        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            searchView.setQuery(listView.getItemAtPosition(position).toString(), true);
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.msg_search_author));
        searchView.clearFocus();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDataAndSetAdaptor();

        seeAllCategory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), viewGridBooks.class);
            intent.putExtra("SeeAll", catagorySelecterAdapter.getSelectedItem());
            startActivity(intent);
        });

        seeAllPopular.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), viewGridBooks.class);
            intent.putExtra("SeeAll", "Most Popular Books");
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchDatabase(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals("")) {
                    layoutListview.setVisibility(View.GONE);
                    databaseRecyclerView.setVisibility(View.GONE);
                } else {
                    adapter.getFilter().filter(newText);
                    layoutListview.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        back.setOnClickListener(v -> {
            layoutRecyclerView.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            searchView.clearFocus();
        });
    }

    private void searchDatabase(String search) {
        layoutRecyclerView.setVisibility(View.VISIBLE);
        layoutListview.setVisibility(View.GONE);
        databaseRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);

        Query query = userDb.collection("BookView").whereArrayContains("keywords", search);
        SearchDatabaseAdapter searchDatabaseAdapter = new SearchDatabaseAdapter(getContext(), this, R.layout.search_results_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        databaseRecyclerView.setLayoutManager(linearLayoutManager);
        databaseRecyclerView.setAdapter(searchDatabaseAdapter);
        searchDatabaseAdapter.setQuery(query);
        progressBar.setVisibility(View.GONE);
        databaseRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getDataAndSetAdaptor() {
        Log.v("KEY", "ON START");
        listener = userDb.collection("Keywords").addSnapshotListener((documentSnapshots, error) -> {
            if (error != null) {
                Log.w("LISTEN TAG", "onEvent:error", error);
                return;
            }
            String x = "LISTEN FROM ";
            if (documentSnapshots.getMetadata().isFromCache()) {
                x = x + "Cache";
            } else {
                x = x + "server";
            }

            Log.d("LISTEN TAG", "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
            for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                Log.v("LISTEN Doc loc", "" + x + "  : " + change.getType());
                switch (change.getType()) {
                    case ADDED:
                    case MODIFIED: {
                        ArrayList<String> keys = (ArrayList<String>) change.getDocument().getData().get("Keywords");
                        Set<String> set = new HashSet<>(keys);
                        keys = new ArrayList<>(set);
                        Log.v("",keys.toString());
                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, keys);
                        listView.setAdapter(adapter);
                        break;
                    }
                }
            }
        });
    }

    private void setSlider() {
        viewPager2 = view.findViewById(R.id.viewPagerImageSlider);
        viewPager2.setAdapter(new SliderAdapter(viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(0));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.75f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000);
            }
        });
    }

    private void setMostPopularBooks() {
        RecyclerView MostPopularBooks = view.findViewById(R.id.horizontalScrollGroup13);
        MostPopularBookAdapter = new AlwaysListenerBookAdapter(getContext());
        MostPopularBookAdapter.setQuery(userDb.collection("BookView").orderBy("rating", Query.Direction.DESCENDING).limit(6));
//        MostPopularBookAdapter.setQuery(userDb.collection("BookView").orderBy("timestamp", Query.Direction.DESCENDING).limit(5));
        MostPopularBooks.setAdapter(MostPopularBookAdapter);
        MostPopularBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setCategory() {
        categoryBooks = view.findViewById(R.id.categoryRecyclerView);
        categoryBooksAdapter = new AlwaysListenerBookAdapter(getContext());
        categoryBooksAdapter.setQuery(userDb.collection("BookView").limit(6));
        categoryBooks.setAdapter(categoryBooksAdapter);
        categoryBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryBooks.setHasFixedSize(true);
    }

    private void setCategorySelecter() {
        RecyclerView catagorySelecter = view.findViewById(R.id.horizontalScrollGroup11);
        catagorySelecterAdapter = new CatagoryRecyclerviewSelecterAdapter(this, this.getContext());
        catagorySelecter.setAdapter(catagorySelecterAdapter);
        catagorySelecter.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener.remove();

        categoryBooksAdapter.onStop();
        MostPopularBookAdapter.onStop();
        Log.d("Home", "Cleared Snap");
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }

    @Override
    public void updateCategaoryView(String category) {

        if (!category.equals("All")) {
            categoryBooksAdapter.setQuery(userDb.collection("BookView").whereEqualTo("category", category).limit(6));
        } else {
            categoryBooksAdapter.setQuery(userDb.collection("BookView").limit(6));
        }
    }

    @Override
    public void call() {
        Log.v("", "emoty");
    }

    @Override
    public void callExistBook(String Title) {

    }
}




/*
        RecyclerView RecentlyviewedBooks = view.findViewById(R.id.horizontalScrollGroup14);
        BookCoverReyclerViewAdapter RecentlyviewedBooksAdapter = new BookCoverReyclerViewAdapter(context);
        RecentlyviewedBooksAdapter.setBooks(books);
        RecentlyviewedBooks.setAdapter(RecentlyviewedBooksAdapter);
        RecentlyviewedBooks.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        RecyclerView RecommandedBooks = view.findViewById(R.id.recyclerNewUserHome);
        RecommandedBookReyclerViewAdapter RecommandedBooksAdapter = new RecommandedBookReyclerViewAdapter(context);
        RecommandedBooksAdapter.setBooks(books);
        RecommandedBooks.setAdapter(RecommandedBooksAdapter);
        RecommandedBooks.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
 */
