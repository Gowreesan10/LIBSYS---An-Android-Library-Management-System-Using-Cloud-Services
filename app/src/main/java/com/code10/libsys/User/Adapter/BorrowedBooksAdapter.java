package com.code10.libsys.User.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Model.Message;
import com.code10.libsys.General.Utility;
import com.code10.libsys.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BorrowedBooksAdapter extends RecyclerView.Adapter<BorrowedBooksAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    private final ArrayList<Message> requests = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    Query mQuery;
    private ListenerRegistration mRegistration;
    private Utility.callBack callBack;

    public BorrowedBooksAdapter(Context context, Utility.callBack callBack) {
        this.context = context;
        this.callBack = callBack;
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

        requests.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        stopListening();
        mQuery = query;
        startListening();
    }

    public void onStop() {
        stopListening();
    }

    protected void onDocumentAdded(DocumentChange change) {
        requests.add(change.getNewIndex(), change.getDocument().toObject(Message.class));
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            requests.set(change.getOldIndex(), change.getDocument().toObject(Message.class));
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            requests.remove(change.getOldIndex());
            requests.add(change.getNewIndex(), change.getDocument().toObject(Message.class));
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange change) {
        requests.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    protected void onError(FirebaseFirestoreException e) {
        Log.w("LISTEN TAG", "onError", e);
    }

    protected void onDataChanged() {
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_borrowed_book_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message message = requests.get(holder.getAdapterPosition());
        Log.v("Request", message.toString());

        Picasso mPicasso = Picasso.get();
        mPicasso.load(message.getThumbnailLink()).fit().into(holder.imageView);
        holder.BookName.setText(message.getBookName());
        holder.requester.setText(message.getLibName());

        holder.returnDate.setText(message.getReturnDate().toString());

        long allowDays = message.getReturnDate().getTime() - message.getRequestTime().toDate().getTime();
        long diff = message.getReturnDate().getTime() - Timestamp.now().toDate().getTime();
        int progress = 0;
        if (diff > 0) {
            long prog = Timestamp.now().toDate().getTime() - message.getRequestTime().toDate().getTime();
            progress = 100 - (int) (((double) prog / allowDays) * 100);
            Log.v("BORROW", "progress  " + progress);
        }
        TimeUnit timeUnit = TimeUnit.DAYS;
        String remaindays = MessageFormat.format("{0}", timeUnit.convert(diff, TimeUnit.MILLISECONDS));
        TimeUnit timeUnit1 = TimeUnit.HOURS;
        String remaininghours = MessageFormat.format("{0}", timeUnit1.convert(diff, TimeUnit.MILLISECONDS));
        int remainh = Integer.parseInt(remaininghours) - Integer.parseInt(remaindays) * 24;

        if (Integer.parseInt(remaindays) < 0) {
            holder.remaintime.setText("Over Due             :");
            remaindays = (Integer.parseInt(remaindays) * (-1)) + "";
        }
        holder.remain.setText(remainh == 0 ? remaindays + " Days " : remaindays + " Days " + remainh + " Hours");

//        if (progress == 0) {
//            holder.determinateBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
//        } else if
        if (progress < 20) {
            holder.determinateBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));
        }
        holder.determinateBar.setProgress(progress, true);
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("LISTEN TAG", "onEvent:error", e);
            onError(e);
            return;
        }
        String x = documentSnapshots.getMetadata().isFromCache() ? "Request get FROM Cache" : "Request get FROM server";

        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            Log.v("Borrow req listen", "" + x + "  : " + change.getType());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView BookName;
        TextView requester;
        ImageView imageView;
        TextView returnDate, remain, remaintime;
        ProgressBar determinateBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.bookCoverIv);
            BookName = itemView.findViewById(R.id.Book_Name);
            requester = itemView.findViewById(R.id.requester);
            returnDate = itemView.findViewById(R.id.dateOfBorrow);
            remain = itemView.findViewById(R.id.remain);
            determinateBar = itemView.findViewById(R.id.determinateBar);
            remaintime = itemView.findViewById(R.id.remaintime);
        }
    }
}
