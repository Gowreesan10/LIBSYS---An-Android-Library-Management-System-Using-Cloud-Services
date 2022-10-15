package com.code10.libsys.User.Adapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Activity.ViewDetails;
import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.R;
import com.code10.libsys.User.UserSharedPreferance;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecentlyViewedBooksAdapter extends RecyclerView.Adapter<RecentlyViewedBooksAdapter.ViewHolder> implements SharedPreferences.OnSharedPreferenceChangeListener {

    Context context;
    UserSharedPreferance userSharedPreferance;
    private ArrayList<BookView> mSnapshots;

    public RecentlyViewedBooksAdapter(Context context) {
        userSharedPreferance = UserSharedPreferance.getInstance(context);
        userSharedPreferance.registerSharedPrefListener(this);
        this.context = context;
        setQuery();
    }

    public void setQuery() {
        mSnapshots = userSharedPreferance.getRecentlyViewdBooks();
        if (mSnapshots != null) {
            notifyDataSetChanged();
        } else {
            mSnapshots = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        if (mSnapshots.isEmpty()) {
            return 0;
        }
        return mSnapshots.size();
    }

    @NonNull
    @Override
    public RecentlyViewedBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_iv, parent, false);
        RecentlyViewedBooksAdapter.ViewHolder viewHolder = new RecentlyViewedBooksAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyViewedBooksAdapter.ViewHolder holder, int position) {
        Picasso mPicasso = Picasso.get();
        BookView bookView = mSnapshots.get(holder.getAdapterPosition());

        mPicasso.load(bookView.getThumbnailLink()).into(holder.IVbookCover);

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, ViewDetails.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(bookView);
            i.putExtra("BookView", jsonStr);
            context.startActivity(i);
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSnapshots.clear();
        setQuery();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVbookCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IVbookCover = itemView.findViewById(R.id.bookCoverImage);
        }
    }
}
