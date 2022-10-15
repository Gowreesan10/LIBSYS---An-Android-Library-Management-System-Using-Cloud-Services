package com.code10.libsys.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Activity.ViewDetails;
import com.code10.libsys.General.Model.BookDetails;
import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.General.Model.Message;
import com.code10.libsys.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecommandedBooksAdapter extends RecyclerView.Adapter<RecommandedBooksAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    private final FirebaseFirestore userDb = FirebaseFirestore.getInstance();
    private final ArrayList<Message> mSnapshots = new ArrayList<>();
    private final ArrayList<BookDetails> bookViews = new ArrayList<>();
    Query mQuery;
    Context context;
    private ListenerRegistration mRegistration;

    public RecommandedBooksAdapter(Context context) {
        this.context = context;
        startListening();
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            onError(e);
            return;
        }

        Log.d("SET BORROW", "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    mSnapshots.add(change.getDocument().toObject(Message.class));
                case MODIFIED:
                case REMOVED:
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

    protected void onError(FirebaseFirestoreException e) {
        Log.w("SET", "onError", e);
    }

    protected void onDataChanged() {
        ArrayList<String> category = new ArrayList<>();
        for (Message message : mSnapshots) {
            category.add(message.getCategory());
        }
        Log.v("SET", category.toString());

        Query query;
        if (!category.isEmpty()) {
            query = userDb.collection("BookDetails").whereEqualTo("category", mostFrequent(category)).orderBy("userRating", Query.Direction.DESCENDING).limit(8);
        } else {
            query = userDb.collection("BookDetails").orderBy("userRating", Query.Direction.DESCENDING).limit(8);
        }
        getBookView(query);
    }

    public String mostFrequent(ArrayList<String> arr) {
        int maxcount = 0;
        String element_having_max_freq = arr.get(0);
        for (String value : arr) {
            int count = 0;
            for (String s : arr) {
                if (value.equals(s)) {
                    count++;
                }
            }

            if (count > maxcount) {
                maxcount = count;
                element_having_max_freq = value;
            }
        }

        return element_having_max_freq;
    }

    private void getBookView(Query query) {
        bookViews.clear();
        notifyDataSetChanged();
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.v("SET", queryDocumentSnapshots.getDocuments().size() + "");
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                boolean ithas = false;

                if (!mSnapshots.isEmpty()) {
                    for (Message bookView00 : mSnapshots) {
                        if (bookView00.getBookName().equals(documentSnapshot.toObject(BookDetails.class).getTitle())) {
                            ithas = true;
                            break;
                        }
                    }
                }
                if (!ithas) {
                    bookViews.add(documentSnapshot.toObject(BookDetails.class));
                    notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(bookViews.size(), 5);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_iv_recommanded_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso mPicasso = Picasso.get();
        BookDetails book = bookViews.get(holder.getAdapterPosition());

        mPicasso.load(book.getThumbnailLink()).into(holder.IVbookCover);
        holder.BookTitle.setText(book.getTitle());
        holder.txtAuthName.setText(book.getAuthors().toString().replace("[", "").replace("]", ""));
        holder.txtPublisherName.setText(book.getPublisher());
        holder.txtPubDate.setText(book.getPublishedDate());
        holder.txtRTV.setText(book.getUserRating());

        holder.itemView.setOnClickListener(view -> {
            userDb.collection("BookView").document(book.getTitle()).get().addOnSuccessListener(documentSnapshot -> {
                Intent i = new Intent(context, ViewDetails.class);
                Gson gson = new Gson();
                String jsonStr = gson.toJson(documentSnapshot.toObject(BookView.class));
                i.putExtra("BookView", jsonStr);
                context.startActivity(i);
            });

        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVbookCover;
        TextView BookTitle, txtAuthName, txtPublisherName, txtPubDate, txtRTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IVbookCover = itemView.findViewById(R.id.IVrecommandedBook);
            BookTitle = itemView.findViewById(R.id.txtBookTitle1);
            txtAuthName = itemView.findViewById(R.id.txtAuth);
            txtPublisherName = itemView.findViewById(R.id.txtPublisherName);
            txtPubDate = itemView.findViewById(R.id.txtPubDate);
            txtRTV = itemView.findViewById(R.id.txtRTV);
        }
    }
}
