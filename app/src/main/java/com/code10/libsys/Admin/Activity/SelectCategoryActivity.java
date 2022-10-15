package com.code10.libsys.Admin.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Adapter.CategoryBookAdapter;
import com.code10.libsys.Admin.Model.Category;
import com.code10.libsys.R;

import java.util.ArrayList;

public class SelectCategoryActivity extends AppCompatActivity {
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        searchView = findViewById(R.id.searchViewSearch);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();

        ArrayList<Category> myListData = new ArrayList<>();
        myListData.add(new Category("All", R.drawable.category64));
        myListData.add(new Category("Art and Architecture", R.drawable.category64));
        myListData.add(new Category("Biography", R.drawable.category64));
        myListData.add(new Category("Comics", R.drawable.category64));
        myListData.add(new Category("Computer", R.drawable.category64));
        myListData.add(new Category("Fiction", R.drawable.category64));
        myListData.add(new Category("Games", R.drawable.category64));
        myListData.add(new Category("Health", R.drawable.category64));
        myListData.add(new Category("History", R.drawable.category64));
        myListData.add(new Category("House and Home", R.drawable.category64));
        myListData.add(new Category("Law", R.drawable.category64));
        myListData.add(new Category("Languages", R.drawable.category64));
        myListData.add(new Category("Mathematics", R.drawable.category64));


        RecyclerView categoryRecyclerView = findViewById(R.id.recyclerLanguage);
        CategoryBookAdapter categoryBookAdapter = new CategoryBookAdapter(myListData, getBaseContext());
        categoryBookAdapter.setQuery("");
        categoryRecyclerView.setAdapter(categoryBookAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                categoryBookAdapter.setQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                categoryBookAdapter.setQuery(newText);
                return false;
            }
        });

        ImageView imageView = findViewById(R.id.imageArrowleft);
        imageView.setOnClickListener(v -> finish());
    }
}