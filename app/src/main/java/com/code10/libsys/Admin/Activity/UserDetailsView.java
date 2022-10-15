package com.code10.libsys.Admin.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Adapter.UserViewAdapter;
import com.code10.libsys.R;

public class UserDetailsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_view);
        ImageView back = findViewById(R.id.imgback);
        back.setOnClickListener(v -> finish());

        RecyclerView userRecyclerview = findViewById(R.id.userRecyclerview);
        UserViewAdapter userViewAdapter = new UserViewAdapter();
        userRecyclerview.setAdapter(userViewAdapter);
        userViewAdapter.setQuery();
        userRecyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
    }
}