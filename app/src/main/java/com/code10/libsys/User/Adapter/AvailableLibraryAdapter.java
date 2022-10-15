package com.code10.libsys.User.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Map;


public class AvailableLibraryAdapter extends RecyclerView.Adapter<AvailableLibraryAdapter.ViewHolder> {

    private final Context context;
    ListenerRegistration listenerRegistration;
    private ArrayList<Map<String, Object>> mSnapshots = new ArrayList<>();
    private ArrayList<Map<String, Object>> tempmSnapshots = new ArrayList<>();
    private DocumentReference reference;
    private int selectedItem = 100;

    public AvailableLibraryAdapter(Context context) {
        this.context = context;
    }

    public void setQuery(DocumentReference mQuery) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            Log.v("Listener", "remove");
        }
        this.reference = mQuery;
        mSnapshots.clear();
        notifyDataSetChanged();
        getData();
    }

    public void getData() {
        listenerRegistration = reference.addSnapshotListener((value, error) -> {
            Map<String, Object> libraries = value.getData();
            tempmSnapshots.clear();
            for (Map.Entry<String, Object> entry : libraries.entrySet()) {
                Map<String, Object> library = (Map<String, Object>) (entry.getValue());
                String noOfCopies = String.valueOf(library.get("No of Copies"));
                if (!noOfCopies.equals("null") && Integer.parseInt(noOfCopies) > 0) {
                    tempmSnapshots.add(library);
                    Log.d("OBJ", "object " + entry.getValue());
                    mSnapshots = tempmSnapshots;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.v("CARD VIEW", holder.libraryDetails.toString());

        holder.libraryDetails.setCardBackgroundColor(context.getResources().getColor(R.color.white_A700));

        if (selectedItem == position) {
            holder.libraryDetails.setCardBackgroundColor(Color.parseColor("#5CB3FF"));
        }

        Map<String, Object> library = mSnapshots.get(holder.getAdapterPosition());

        Log.v("Name", library.get("Library").toString());
        holder.libraryName.setText(library.get("Library").toString());
        holder.address.setText(library.get("Address").toString());
        holder.BookCount.setText("Book Count : " + library.get("No of Copies").toString());

        holder.itemView.setOnClickListener(view -> {
            int previousItem = selectedItem;
            selectedItem = holder.getAdapterPosition();
            notifyItemChanged(previousItem);
            notifyItemChanged(selectedItem);
        });
    }

    public String getSelectedLibrary() {
        return mSnapshots.get(selectedItem).get("Library").toString();
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView libraryDetails;
        TextView libraryName;
        TextView BookCount;
        TextView address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            libraryDetails = itemView.findViewById(R.id.libraryDetails);
            libraryName = itemView.findViewById(R.id.libraryName);
            BookCount = itemView.findViewById(R.id.AvailableBookCount);
            address = itemView.findViewById(R.id.address);
        }
    }
}
