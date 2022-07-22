package com.code10.libsys.User;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;


public class AvailableLibraryAdapter extends RecyclerView.Adapter<AvailableLibraryAdapter.ViewHolder> implements OnCompleteListener<DocumentSnapshot> {

    private final ArrayList<Map<String, Object>> mSnapshots = new ArrayList<>();
    private DocumentReference reference;
    private Context context;
    private int selectedItem = 100;

    public AvailableLibraryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                getDocument(task);
            } else {
                Log.d("TAG", "No such document");
            }
        } else {
            Log.d("TAG", "get failed with ", task.getException());
        }
    }

    protected void getDocument(Task<DocumentSnapshot> task) {
        Map<String, Object> libraries = task.getResult().getData();

        for (Map.Entry<String, Object> entry : libraries.entrySet()) {
            Map<String, Object> library = (Map<String, Object>) (entry.getValue());
            mSnapshots.add(library);
            Log.d("OBJ", "object " + entry.getValue());
            notifyDataSetChanged();
        }
    }

    public void setQuery(DocumentReference mQuery) {
        this.reference = mQuery;
        mSnapshots.clear();
        notifyDataSetChanged();
        getData();
    }

    public void getData() {
        reference.get(Source.SERVER).addOnCompleteListener(this);
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
            holder.libraryDetails.setCardBackgroundColor(context.getResources().getColor(R.color.blue_A400));
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
