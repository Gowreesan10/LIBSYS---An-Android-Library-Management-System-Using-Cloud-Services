package com.code10.libsys.Admin.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.ViewHolder> {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<QueryDocumentSnapshot> users = new ArrayList<>();

    public UserViewAdapter() {

    }

    public void setQuery() {
        db.collection("Users").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("LISTEN TAG", "onEvent:error" + error);
                return;
            }
            String x = value.getMetadata().isFromCache() ? "Request get FROM Cache" : "Request get FROM server";

            for (DocumentChange change : value.getDocumentChanges()) {
                Log.v("Borrow req listen", "" + x + "  : " + change.getType());
                switch (change.getType()) {
                    case ADDED:
                        onDocumentAdded(change);
                        break;
                }
            }
            notifyDataSetChanged();
        });
    }

    //TODO: USERPROFILE
    private void onDocumentAdded(DocumentChange change) {
        users.add(change.getDocument());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.userviewcard, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, Object> userdata = users.get(holder.getAbsoluteAdapterPosition()).getData();
        String UserName = users.get(holder.getAbsoluteAdapterPosition()).getId().replace(".User", "");
        String Age = "", Address = "", Pic = "";
        for (Map.Entry<String, Object> x : userdata.entrySet()) {
            switch (x.getKey()) {
                case "DOB":
                    Age = String.valueOf(x.getValue());
                    break;
                case "Address":
                    Address = String.valueOf(x.getValue());
                    break;
                case "Pic":
                    Pic = String.valueOf(x.getValue());
                    break;
            }
        }
        if (!Pic.equals("")) {
            Picasso.get().load(Pic).into(holder.profileimg);
        }
        holder.age.setText(Age);
        holder.address.setText(Address);
        holder.UserName.setText(UserName);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView UserName, age, address;
        public CircleImageView profileimg;

        public ViewHolder(View itemView) {
            super(itemView);
            profileimg = itemView.findViewById(R.id.profileimg);
            UserName = itemView.findViewById(R.id.UserName);
            age = itemView.findViewById(R.id.age);
            address = itemView.findViewById(R.id.address);
        }
    }
}

