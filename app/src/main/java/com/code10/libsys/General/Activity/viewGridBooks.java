package com.code10.libsys.General.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Adapter.AdminBookGridViewAdapter;
import com.code10.libsys.General.Adapter.BookGridViewAdapter;
import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class viewGridBooks extends AppCompatActivity {

    Query mQuery;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        FirebaseFirestore userDb = FirebaseFirestore.getInstance();
        String queryType = getIntent().getStringExtra("SeeAll");

        switch (queryType) {
            case "Most Popular Books":
                mQuery = userDb.collection("BookView").orderBy("rating", Query.Direction.DESCENDING);
                break;
            case "All":
                mQuery = userDb.collection("BookView");
                break;
            default:
                mQuery = userDb.collection("BookView").whereEqualTo("category", queryType);
                break;
        }

        RecyclerView gridBooks = findViewById(R.id.gridBookRecyclerView);
        if (currentUser.getDisplayName().endsWith(".User")) {
            BookGridViewAdapter gridBooksAdapter = new BookGridViewAdapter(this);
            gridBooksAdapter.setQuery(mQuery);
            gridBooks.setAdapter(gridBooksAdapter);
            gridBooks.setLayoutManager(new GridLayoutManager(this, 3) {
                @Override
                public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
                    int overScroll = dy - scrollRange;
                    if (overScroll > 0) {
                        gridBooksAdapter.getData();
                    }
                    return scrollRange;
                }
            });
            gridBooks.setHasFixedSize(true);
        } else {
            AdminBookGridViewAdapter adminBookGridViewAdapter = new AdminBookGridViewAdapter(getBaseContext(), currentUser.getDisplayName().replace(".Librarian", ""));
            adminBookGridViewAdapter.setQuery(mQuery);
            gridBooks.setAdapter(adminBookGridViewAdapter);
            gridBooks.setLayoutManager(new GridLayoutManager(this, 3));
        }

        ImageView imageView = findViewById(R.id.imageGroup2);
        imageView.setOnClickListener(v -> finish());

    }


}