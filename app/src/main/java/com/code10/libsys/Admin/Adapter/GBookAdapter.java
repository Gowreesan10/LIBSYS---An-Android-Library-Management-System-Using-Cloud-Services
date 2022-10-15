package com.code10.libsys.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Activity.AddBookDetails;
import com.code10.libsys.General.Model.BookDetails;
import com.code10.libsys.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GBookAdapter extends RecyclerView.Adapter<GBookAdapter.BookViewHolder> {

    private final ArrayList<BookDetails> bookInfoArrayList;
    private final Context mcontext;

    public GBookAdapter(ArrayList<BookDetails> bookInfoArrayList, Context mcontext) {
        this.bookInfoArrayList = bookInfoArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_rv_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        BookDetails bookInfo = bookInfoArrayList.get(position);
        holder.titleTV.setText(bookInfo.getTitle());
        holder.subtitleTV.setText(bookInfo.getSubTitle());
        holder.authorTV.setText(bookInfo.getAuthors().toString().replace("[", "").replace("]", ""));
        holder.ISBN10.setText(bookInfo.getISBN_10());
        holder.ISBN13.setText(bookInfo.getISBN_13());
        Picasso.get().load(bookInfo.getThumbnailLink()).into(holder.bookIV);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(mcontext, AddBookDetails.class);
            i.putExtra("Book", bookInfo);
            mcontext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return bookInfoArrayList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, subtitleTV, authorTV, ISBN10, ISBN13;
        ImageView bookIV;

        public BookViewHolder(View itemView) {
            super(itemView);
            bookIV = itemView.findViewById(R.id.idIVbook);
            titleTV = itemView.findViewById(R.id.idTVBookTitle);
            subtitleTV = itemView.findViewById(R.id.idTVSubtitleRv);
            authorTV = itemView.findViewById(R.id.idTVAuthor);
            ISBN10 = itemView.findViewById(R.id.idTVISBN10);
            ISBN13 = itemView.findViewById(R.id.idTVISBN13);
        }
    }
}
