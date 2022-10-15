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

import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.R;
import com.code10.libsys.User.Activity.PDFViewerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EbookAdapter extends RecyclerView.Adapter<EbookAdapter.ViewHolder> {

    private final FirebaseFirestore userDb = FirebaseFirestore.getInstance();
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    String userName = currentUser.getDisplayName().replace(".User", "");
    File[] files;
    Context context;

    public EbookAdapter(Context context) {

        File x = context.getExternalFilesDir("EBOOK" + userName);
        Log.v("file", x.getPath());
        files = x.listFiles();
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ebookcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso mPicasso = Picasso.get();

        String filename = files[position].getName().replace(".pdf", "").replace("_", " ");
        holder.BookTitle.setText(filename);

        Log.v("FILE", filename);
        userDb.collection("BookView").document(filename).get().addOnSuccessListener(documentSnapshot -> {
            BookView bookView = documentSnapshot.toObject(BookView.class);
            mPicasso.load(bookView.getThumbnailLink()).into(holder.IVbookCover);
        });

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, PDFViewerActivity.class);
            i.putExtra("EXFILEDIR", "EBOOK" + userName);
            i.putExtra("POSITION", position + "");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVbookCover;
        TextView BookTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IVbookCover = itemView.findViewById(R.id.bookCoverImage);
            BookTitle = itemView.findViewById(R.id.bookName);
        }
    }
}
