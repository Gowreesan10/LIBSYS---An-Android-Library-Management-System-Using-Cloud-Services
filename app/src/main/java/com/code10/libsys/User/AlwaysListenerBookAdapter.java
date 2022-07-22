package com.code10.libsys.User;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.BookView;
import com.code10.libsys.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlwaysListenerBookAdapter extends RecyclerView.Adapter<AlwaysListenerBookAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    Query mQuery;
    Context context;
    private ListenerRegistration mRegistration;
    private FirebaseFirestore userDb = FirebaseFirestore.getInstance();
    private ArrayList<BookView> mSnapshots = new ArrayList<>();

    public AlwaysListenerBookAdapter(Context context) {
        this.context = context;
        startListening();
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("LISTEN TAG", "onEvent:error", e);
            onError(e);
            return;
        }
        String x = "LISTEN FROM ";
        if (documentSnapshots.getMetadata().isFromCache()) {
            x = x + "Cache";
        } else {
            x = x + "server";
        }

        // Dispatch the event
        Log.d("LISTEN TAG", "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            Log.v("LISTEN Doc loc", "" + x + "  : " + change.getType());
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

        onDataChanged();
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        stopListening();
        mQuery = query;
        startListening();
    }

    public Query getmQuery() {
        return mQuery;
    }

    public void onStop() {
        stopListening();
    }

    protected void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument().toObject(BookView.class));
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument().toObject(BookView.class));
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument().toObject(BookView.class));
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    protected void onError(FirebaseFirestoreException e) {
        Log.w("LISTEN TAG", "onError", e);
    }

    protected void onDataChanged() {
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_iv, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso mPicasso = Picasso.get();
        BookView bookView = mSnapshots.get(holder.getAdapterPosition());

//        Log.v("BOOK", bookView.toString());
        mPicasso.load(bookView.getThumbnailLink()).into(holder.IVbookCover);

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, UserViewDetails.class);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(bookView);
            i.putExtra("BookView", jsonStr);
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
