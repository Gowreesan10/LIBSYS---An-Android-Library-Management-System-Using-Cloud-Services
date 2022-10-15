package com.code10.libsys.User.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderAdapterViewHolder> implements EventListener<QuerySnapshot> {

    private final ViewPager2 viewPager2;
    private final ArrayList<QueryDocumentSnapshot> mSnapshots = new ArrayList<>();
    private final ArrayList<QueryDocumentSnapshot> ORIGINAL = new ArrayList<>();
    private final Runnable runnable = () -> {
//        mSnapshots.clear();
//        mSnapshots.addAll(ORIGINAL);
        mSnapshots.addAll(ORIGINAL);
        notifyDataSetChanged();
    };
    FirebaseFirestore userDb = FirebaseFirestore.getInstance();
    private final Query mQuery = userDb.collection("BookView").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
    ListenerRegistration mRegistration;


    public SliderAdapter(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
        Log.d("Running constructer", "start");
        startListening();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("SLIDER from", "onEvent:error", e);
            onError(e);
            return;
        }
        String x = "Slider FROM ";
        if (documentSnapshots.getMetadata().isFromCache()) {
            x = x + "Cache";
        } else {
            x = x + "server";
        }

        // Dispatch the event
        Log.d("TAG", "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            Log.v("Doc loc", "" + x + "  : " + change.getType());
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
//                    mSnapshots.clear();
//                    mRegistration.remove();
//                    startListening();
                    break;
            }
        }
        notifyDataSetChanged();
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void onStop() {
        mSnapshots.clear();
        mRegistration.remove();
        Log.d("Slider Adapter", "Clear snap");
    }

    protected void onDocumentAdded(DocumentChange change) {
//        ArrayList<QueryDocumentSnapshot> temp = new ArrayList<>();
        ORIGINAL.add(change.getDocument());
//        temp.addAll(ORIGINAL);
//        ORIGINAL = temp;
//        mSnapshots = ORIGINAL;
        mSnapshots.add(change.getDocument());
    }

    protected void onDocumentModified(DocumentChange change) {
//        ArrayList<QueryDocumentSnapshot> temp = new ArrayList<>();
        ORIGINAL.add(change.getDocument());
//        temp.addAll(ORIGINAL);
//        ORIGINAL = temp;
//        mSnapshots = ORIGINAL;
        mSnapshots.add(change.getDocument());
    }

    protected void onDocumentRemoved(DocumentChange change) {
//        ArrayList<QueryDocumentSnapshot> temp = new ArrayList<>();
//        ORIGINAL.add(change.getDocument());
//        temp.addAll(ORIGINAL);
//        ORIGINAL = temp;
//        mSnapshots = ORIGINAL;
//        mSnapshots.add( change.getDocument());
    }

    protected void onError(FirebaseFirestoreException e) {
        Log.w("TAG", "onError", e);
    }

    @NonNull
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container, parent, false);
        return new SliderAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        BookView bookView = mSnapshots.get(position).toObject(BookView.class);
        Picasso mPicasso = Picasso.get();
        mPicasso.load(bookView.getThumbnailLink()).into(viewHolder.imageView);

        if (position == getItemCount() - 2) {
            viewPager2.post(runnable);
        }

        viewHolder.itemView.setOnClickListener(view -> {
        });
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    static class SliderAdapterViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}

