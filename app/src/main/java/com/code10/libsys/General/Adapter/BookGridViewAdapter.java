package com.code10.libsys.General.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Activity.ViewDetails;
import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookGridViewAdapter extends RecyclerView.Adapter<BookGridViewAdapter.ViewHolder> implements OnCompleteListener<QuerySnapshot> {

    private final ArrayList<QueryDocumentSnapshot> mSnapshots = new ArrayList<>();
    private final Context context;
    private Query mQuery;
    private boolean getDataRunning = false;

    public BookGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        Log.d("Book size", "" + task.getResult().size());
        if (task.isSuccessful() && (!task.getResult().isEmpty())) {
            getDocument(task);
        }
    }

    protected void getDocument(Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d("Book Cover", document.getId() + "  " + document.getMetadata().isFromCache());
            mSnapshots.add(document);
            notifyDataSetChanged();
        }
        getDataRunning = false;
    }

    public DocumentSnapshot getLastSnapShot() {
        if (mSnapshots.size() == 0)
            return null;
        else
            return mSnapshots.get(mSnapshots.size() - 1);
    }

    public void setQuery(Query query) {
        mSnapshots.clear();
        notifyDataSetChanged();
        mQuery = query.limit(12);
        getData();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    public void getData() {
        if (!getDataRunning) {
            getDataRunning = true;
            if (getLastSnapShot() != null) {
                mQuery.startAfter(getLastSnapShot()).get().addOnCompleteListener(this);
            } else {
                mQuery.get().addOnCompleteListener(this);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso mPicasso = Picasso.get();
        BookView bookView = mSnapshots.get(holder.getAdapterPosition()).toObject(BookView.class);
        mPicasso.load(bookView.getThumbnailLink()).into(holder.IVbookCover);

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, ViewDetails.class);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(bookView);
            i.putExtra("BookView", jsonStr);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVbookCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IVbookCover = itemView.findViewById(R.id.bookCoverImage);
        }
    }
}
