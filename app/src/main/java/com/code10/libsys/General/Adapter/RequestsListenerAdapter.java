package com.code10.libsys.General.Adapter;

import static java.lang.Math.abs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Model.Message;
import com.code10.libsys.General.Service.FCMSend;
import com.code10.libsys.General.Utility;
import com.code10.libsys.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RequestsListenerAdapter extends RecyclerView.Adapter<RequestsListenerAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    private final ArrayList<Message> requests = new ArrayList<>();
    private final int layout;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Utility.callBack callBack;
    Context context;
    Query mQuery;
    private ListenerRegistration mRegistration;

    public RequestsListenerAdapter(Context context, int layout, Utility.callBack callBack) {
        this.context = context;
        this.layout = layout;
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
            Log.v("Item changed but remained in same position",change.getOldIndex()+"");
            requests.set(change.getOldIndex(), change.getDocument().toObject(Message.class));
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            Log.v("Item changed but remained in same position",change.getOldIndex()+" "+change.getNewIndex());
            requests.remove(change.getOldIndex());
            Log.v("changed",change.getDocument().toString());
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
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (R.layout.admin_request_card == layout) {
            Message message = requests.get(holder.getAdapterPosition());
            Picasso mPicasso = Picasso.get();
            holder.childView.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mPicasso.load(message.getThumbnailLink()).into(holder.book);
            mPicasso.load(message.getReceiptUri()).into(holder.imageView);
            holder.BookName.setText(message.getBookName());
            holder.requester.setText(message.getRequester());
            holder.requestStatus.setText(message.getStatus());
            if (message.getStatus().equals("Pending")) {
                Log.v("HI ERROR", message.getStatus() + message.getRequester());
                holder.itemView.setBackgroundColor(Color.parseColor("#DDEFFF"));
            }
            long diff = message.getReturnDate().getTime() - message.getRequestTime().toDate().getTime();
            TimeUnit timeUnit = TimeUnit.DAYS;
            holder.borrowingDays.setText(MessageFormat.format("{0}", timeUnit.convert(diff, TimeUnit.MILLISECONDS)));
//            String[] xx = message.getRequestTime().toString().split(",", 2);
//            String seconds = xx[0].replace("Timestamp(seconds=", "");
//            String nanoSec = xx[1].replace(" nanoseconds=", "").replace(")", "");
//            Timestamp x = new Timestamp(Long.parseLong(seconds), Integer.parseInt(nanoSec));
//            SimpleDateFormat dateformatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
            //Parsing the given String to Date object
//            Long msec = Long.parseLong(seconds)*1000;
//            Date date = new Date(msec);
            holder.requestDate.setText(message.getRequestTime().toDate().toString());
            holder.borrowingMethod.setText(message.getBorrowMethod());
            holder.returndate.setText(message.getReturnDate().toString());

            holder.itemView.setOnClickListener(view -> {
                if (message.getStatus().equals("Pending")) {
                    holder.childView.setVisibility(abs(holder.childView.getVisibility() - 8));
                }
            });

            holder.accept.setOnClickListener((View v) -> {
                callBack.callExistBook(message.getRequester() + "," + message.getRequestTime() + "," + message.getBookName());
            });

            holder.denyBtn.setOnClickListener(v -> {
                Log.v("DENY", "request Denied");
                increaseBookAndRemoveReserve(message);
            });

            holder.imageView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.recipt);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView x = dialog.findViewById(R.id.recipt);
                Picasso.get().load(message.getReceiptUri()).into(x);
                dialog.show();
            });
        }

        if (R.layout.user_request_card == layout) {
            Message message = requests.get(holder.getAdapterPosition());
            Log.v("Request", message.toString());

            Picasso mPicasso = Picasso.get();
            mPicasso.load(message.getThumbnailLink()).fit().into(holder.imageView);
            holder.BookName.setText(message.getBookName());
            holder.requester.setText(message.getLibName());
            holder.status.setText(message.getStatus());
            if ((!message.getNotificationViewed()) & (!message.getStatus().equals("Pending"))) {
                holder.itemView.setBackgroundColor(Color.parseColor("#DDEFFF"));
            }

            holder.itemView.setOnClickListener(view -> {
                holder.itemView.setBackgroundColor(Color.WHITE);
                db.collection("Borrow Requests").document(message.getRequestTime() + " " + message.getRequester()).
                        update("notificationViewed", true);

            });
        }
    }


    private void increaseBookAndRemoveReserve(Message message) {

        String LibName = message.getLibName();
        String Book = message.getBookName();
        String reqester = message.getRequester();
        Timestamp requestTime = message.getRequestTime();
        String TOKEN = message.getBorrowerToken();

        WriteBatch batch = db.batch();
        batch.update(db.collection("Borrow Requests").document(message.getRequestTime() + " " + reqester),
                "status",
                "Denied");
        batch.update(db.collection("Copies").document(Book),
                LibName + ".No of Copies",
                FieldValue.increment(1));
        batch.update(db.collection("IssueDetails").document(LibName),
                Book + ".Available Copies",
                FieldValue.increment(1));
        batch.update(db.collection("IssueDetails").document(LibName),
                Book + ".Reserved For",
                FieldValue.arrayRemove(reqester));
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.v("Successfully removed reservation", "");
//                db.collection("Users").document(reqester + ".User").get().addOnCompleteListener(task1 -> {
//                    if (task1.isSuccessful()) {
//                        String TOKEN1 = String.valueOf(task1.getResult().get("token"));
//                        if (!TOKEN1.equals("null")) {
                            FCMSend.pushNotification(context, TOKEN, "Borrow Request Denied", "Request For "+ Book+ " is Denied.");
//                        }
//                    }
//                });

            }
        }).addOnFailureListener(e -> Log.v("fail",e.getMessage()));
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
        ImageView book;
        TextView BookName;
        TextView requester;
        TextView returndate;
        TextView requestDate;
        TextView borrowingMethod;
        TextView borrowingDays;
        ImageView imageView;
        LinearLayout childView;
        TextView requestStatus;
        AppCompatButton denyBtn;
        AppCompatButton accept;
        TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (layout == R.layout.admin_request_card) {
                requestStatus = itemView.findViewById(R.id.status);
                book = itemView.findViewById(R.id.bookCoverIv);
                BookName = itemView.findViewById(R.id.BookName);
                requester = itemView.findViewById(R.id.requesterName);
                requestDate = itemView.findViewById(R.id.requestTime);
                returndate = itemView.findViewById(R.id.showreturndate);
                borrowingMethod = itemView.findViewById(R.id.borrowingMethod);
                borrowingDays = itemView.findViewById(R.id.borrowingDays);
                imageView = itemView.findViewById(R.id.receiptImage);
                childView = itemView.findViewById(R.id.childCardView);
                denyBtn = itemView.findViewById(R.id.denyBtn);
                accept = itemView.findViewById(R.id.acceptRequest);
            }
            if (layout == R.layout.user_request_card) {
                imageView = itemView.findViewById(R.id.bookCoverIv);
                BookName = itemView.findViewById(R.id.Book_Name);
                requester = itemView.findViewById(R.id.requester);
                status = itemView.findViewById(R.id.statusName);
            }
        }
    }
}
