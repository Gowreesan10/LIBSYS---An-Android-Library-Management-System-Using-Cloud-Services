package com.code10.libsys;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.User.UserViewDetails;
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
    private Query mQuery;
    private boolean getDataRunning = false;
    private Context context;

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
//        Map<String, Object> bookCover = mSnapshots.get(holder.getAdapterPosition()).getData();
//        Picasso mPicasso = Picasso.get();
//        mPicasso.load(bookCover.get("Thumbnail Link").toString()).into(holder.IVbookCover);
//
//        holder.itemView.setOnClickListener(view -> {
//            Intent i = new Intent(context, UserViewDetails.class);
//            i.putExtra("Category", bookCover.get("Category").toString());
//            i.putExtra("Keywords", bookCover.get("Keywords").toString());
//            i.putExtra("Rating", bookCover.get("Rating").toString());
//            i.putExtra("Thumbnail Link", bookCover.get("Thumbnail Link").toString());
//            i.putExtra("Title", bookCover.get("Title").toString());
//            context.startActivity(i);
//        });
        Picasso mPicasso = Picasso.get();
        BookView bookView = mSnapshots.get(holder.getAdapterPosition()).toObject(BookView.class);
        mPicasso.load(bookView.getThumbnailLink()).into(holder.IVbookCover);

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, UserViewDetails.class);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(bookView);
            i.putExtra("BookView", jsonStr);
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
