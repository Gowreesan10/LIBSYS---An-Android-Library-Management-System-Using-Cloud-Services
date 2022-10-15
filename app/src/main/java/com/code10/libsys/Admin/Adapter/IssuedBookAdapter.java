package com.code10.libsys.Admin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IssuedBookAdapter extends RecyclerView.Adapter<IssuedBookAdapter.ViewHolder> {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    ArrayList<HashMap<String, HashMap<String, String>>> data = new ArrayList<>();
    ArrayList<HashMap<String, HashMap<String, String>>> tempdata = new ArrayList<>();
    Context mcontext;

    public IssuedBookAdapter(Context context) {
        mcontext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.issuedadminbookcard, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HashMap<String, HashMap<String, String>> issueone = data.get(holder.getAbsoluteAdapterPosition());
        Log.v("onbind", issueone.toString());
        String Bookname = "", copyno = "", borrower = "", issuedtime = "";
        for (Map.Entry<String, HashMap<String, String>> gg : issueone.entrySet()) {
            Bookname = gg.getKey();
            Log.v("onbind book name", Bookname);
            HashMap<String, String> hashmap = gg.getValue();
            for (Map.Entry<String, String> entry : hashmap.entrySet()) {
                Log.v("onbind entry", entry.getKey());
                switch (entry.getKey()) {
                    case "CopyNo":
                        copyno = String.valueOf(entry.getValue());
                        break;
                    case "User":
                        holder.borrower.setText(entry.getValue());
                        break;
                    case "issuedTime":
                        String[] xx = String.valueOf(entry.getValue()).split(",", 2);
                        String seconds = xx[0].replace("Timestamp(seconds=", "");
                        String nanoSec = xx[1].replace(" nanoseconds=", "").replace(")", "");
                        Timestamp x = new Timestamp(Long.parseLong(seconds), Integer.parseInt(nanoSec));
                        holder.issuedtime.setText(x.toDate().toString());
                        break;
                }
            }
        }

        holder.copyno.setText(copyno);
        holder.bookname.setText(Bookname);
        db.collection("BookView").document(Bookname).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                BookView bookView = task.getResult().toObject(BookView.class);
                Picasso.get().load(bookView.getThumbnailLink()).into(holder.bookcover);
            }
        });

    }

    public void setQuery() {
        String lib = firebaseUser.getDisplayName().replace(".Librarian", "");
        db.collection("IssueDetails").document(lib).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("LISTEN TAG", "onEvent:error", error);
                return;
            }
           // data.clear();
            tempdata.clear();
            assert value != null;
            Map<String, Object> bookcopies = value.getData();
            if (bookcopies != null) {
                for (Map.Entry<String, Object> entry : bookcopies.entrySet()) {
                    String BookName = entry.getKey();
                    Log.v("BookName", BookName);
                    Map<String, Object> x = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> fields : x.entrySet()) {
                        Log.v("fields", fields.getKey());
                        if (fields.getKey().equals("Issued For")) {
                            Log.v("issuedfor", fields.toString());
                            ArrayList<HashMap<String, String>> issuedFor = (ArrayList<HashMap<String, String>>) fields.getValue();
                            for (HashMap<String, String> issue : issuedFor) {
                                HashMap<String, HashMap<String, String>> issueone = new HashMap<>();
                                issueone.put(BookName, issue);
//                                data.add(data.size(), issueone);
//                                notifyItemInserted(data.size() - 1);
                                tempdata.add(issueone);
                                Log.v("added", issueone.toString());
                            }
                        }
                    }
                    data = tempdata;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView borrower, issuedtime, copyno, bookname;
        public ImageView bookcover;

        public ViewHolder(View itemView) {
            super(itemView);
            bookcover = itemView.findViewById(R.id.bookCover);
            issuedtime = itemView.findViewById(R.id.issuedtimeenter);
            borrower = itemView.findViewById(R.id.borrowerenter);
            copyno = itemView.findViewById(R.id.copynoenter);
            bookname = itemView.findViewById(R.id.Book_Name);
        }
    }
}

