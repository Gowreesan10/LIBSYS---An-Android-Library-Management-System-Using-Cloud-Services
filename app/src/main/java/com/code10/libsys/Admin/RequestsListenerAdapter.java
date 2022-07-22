package com.code10.libsys.Admin;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Message;
import com.code10.libsys.R;
import com.google.android.material.badge.BadgeDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestsListenerAdapter extends RecyclerView.Adapter<RequestsListenerAdapter.ViewHolder> {

    Context context;
    BadgeDrawable badgeExplorer;
    private ArrayList<Message> mSnapshots = new ArrayList<>();

    public RequestsListenerAdapter(Context context, BadgeDrawable badgeExplorer) {
        this.context = context;
        this.badgeExplorer = badgeExplorer;
    }

    public void setQuery() {
        mSnapshots = AdminSharedPreference.getInstance(context).getRequestArrayList();
        if (mSnapshots != null) {
            notifyDataSetChanged();
            increaseBadge(mSnapshots.size());
        }
    }

    protected void increaseBadge(int batchCount) {
        badgeExplorer.setVisible(true);
        badgeExplorer.setNumber(batchCount);
        badgeExplorer.setBackgroundColor(Color.RED);
        badgeExplorer.setBadgeTextColor(Color.WHITE);
    }

    protected void decreaseBadge() {
//        badgeExplorer.setNumber(--batchCount);
//        if (batchCount == 0) {
//            badgeExplorer.setVisible(false);
//        }
    }

    @Override
    public int getItemCount() {
        if (mSnapshots == null) {
            return 0;
        }
        return mSnapshots.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_request_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mSnapshots.get(holder.getAdapterPosition());
        Log.v("Request", message.toString());

        Picasso mPicasso = Picasso.get();
        mPicasso.load(message.getThumbnailLink()).into(holder.IVbookCover);
        holder.BookName.setText(message.getBookName());
        holder.requester.setText(String.format("Requested By: %s", message.getRequester()));

        holder.itemView.setOnClickListener(view -> {
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVbookCover;
        TextView BookName;
        TextView requester;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IVbookCover = itemView.findViewById(R.id.bookCoverIv);
            BookName = itemView.findViewById(R.id.Book_Name);
            requester = itemView.findViewById(R.id.requester);
        }
    }
}
