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
import com.code10.libsys.General.Utility;
import com.code10.libsys.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchDatabaseAdapter extends RecyclerView.Adapter<SearchDatabaseAdapter.ViewHolder> {

    private final FirebaseFirestore userDb = FirebaseFirestore.getInstance();
    private final ArrayList<BookView> queryDocumentSnapshots = new ArrayList<>();
    private final ArrayList<BookDetails> DocumentSnapshots = new ArrayList<>();
    Query mQuery;
    Context context;
    ViewGroup group;
    Utility.callBack callBack;
    int resourceId;

    public SearchDatabaseAdapter(Context context, Utility.callBack call, int resourceId) {
        this.context = context;
        callBack = call;
        this.resourceId = resourceId;
    }

    public void setQuery(Query query) {

        mQuery = query;
        queryDocumentSnapshots.clear();
        DocumentSnapshots.clear();
        getTitle();
    }

    private void getTitle() {
        mQuery.get(Source.SERVER).addOnCompleteListener(task -> {
                    Log.d("Title", "Sucess");
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            callBack.call();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Title", document.toString());
                                try {
                                    BookView bookView = document.toObject(BookView.class);
                                    queryDocumentSnapshots.add(bookView);
                                    getBookData(bookView.getTitle());
                                } catch (Exception e) {
                                    DocumentSnapshots.add(document.toObject(BookDetails.class));
                                    Log.d("Doc", document.getData().toString());
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    private void getBookData(String Title) {
        userDb.collection("BookDetails").document(Title).get().addOnSuccessListener(documentSnapshot -> {
            DocumentSnapshots.add(documentSnapshot.toObject(BookDetails.class));
            Log.d("Doc", documentSnapshot.getData().toString());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return DocumentSnapshots.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
        group = parent;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookDetails x = DocumentSnapshots.get(holder.getAdapterPosition());
        Picasso.get().load(x.getThumbnailLink()).into(holder.bookIV);
        holder.titleTV.setText(x.getTitle());
        holder.authorTV.setText(x.getAuthors().toString().replace("[", "").replace("]", ""));

        if (resourceId == R.layout.book_rv_item) {
            holder.subtitleTV.setText(x.getSubTitle());
            holder.ISBN10.setText(x.getISBN_10());
            holder.ISBN13.setText(x.getISBN_13());
        } else {
            holder.ratTV.setText(x.getUserRating());
            holder.pubDateTV.setText(x.getPublishedDate());
            holder.pubTV.setText(x.getPublisher());
        }
        holder.itemV.setOnClickListener(v -> {
            if (resourceId == R.layout.book_rv_item) {
                callBack.callExistBook(x.getTitle());
            } else {
                Intent i = new Intent(context, ViewDetails.class);
                Gson gson = new Gson();
                String jsonStr = gson.toJson(queryDocumentSnapshots.get(holder.getAdapterPosition()));
                i.putExtra("BookView", jsonStr);
                context.startActivity(i);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, subtitleTV, authorTV, ISBN10, ISBN13, pubTV, pubDateTV, ratTV;
        ImageView bookIV;
        View itemV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemV = itemView;
            if (resourceId == R.layout.book_rv_item) {
                bookIV = itemView.findViewById(R.id.idIVbook);
                titleTV = itemView.findViewById(R.id.idTVBookTitle);
                subtitleTV = itemView.findViewById(R.id.idTVSubtitleRv);
                authorTV = itemView.findViewById(R.id.idTVAuthor);
                ISBN10 = itemView.findViewById(R.id.idTVISBN10);
                ISBN13 = itemView.findViewById(R.id.idTVISBN13);
            } else {
                ratTV = itemView.findViewById(R.id.txtRTV);
                pubDateTV = itemView.findViewById(R.id.txtPubDate);
                pubTV = itemView.findViewById(R.id.txtPublisherName);
                authorTV = itemView.findViewById(R.id.txtAuth);
                titleTV = itemView.findViewById(R.id.txtBookTitle1);
                bookIV = itemView.findViewById(R.id.IVrecommandedBook);
            }
        }
    }


}
