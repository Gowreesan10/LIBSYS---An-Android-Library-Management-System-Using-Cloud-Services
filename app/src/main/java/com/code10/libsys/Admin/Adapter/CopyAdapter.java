package com.code10.libsys.Admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.R;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CopyAdapter extends RecyclerView.Adapter<CopyAdapter.ViewHolder> {
    ArrayList<HashMap<String, String>> copy;

    public CopyAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.copyitem, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HashMap<String, String> map = copy.get(holder.getAbsoluteAdapterPosition());
        holder.txtCopyCounter.setText("Copy No " + map.get("CopyNo"));
        holder.borrower.setText(map.get("User"));
        for (Map.Entry<String, String> c : map.entrySet()) {
            if (c.getKey().equals("issuedTime")) {
                String[] xx = c.toString().split(",", 2);
                String seconds = xx[0].replace("issuedTime=Timestamp(seconds=", "");
                String nanoSec = xx[1].replace(" nanoseconds=", "").replace(")", "");
                Timestamp x = new Timestamp(Long.parseLong(seconds), Integer.parseInt(nanoSec));
                holder.txtborrowtime.setText(x.toDate().toString());
            }
        }
    }

    public void setQuery(ArrayList<HashMap<String, String>> copy) {
        this.copy = copy;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return copy.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView borrower, txtborrowtime, txtCopyCounter;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txtCopyCounter = itemView.findViewById(R.id.txtCopyCounter);
            this.borrower = (TextView) itemView.findViewById(R.id.txtborrower);
            this.txtborrowtime = itemView.findViewById(R.id.txtborrowtime);
        }
    }
}
