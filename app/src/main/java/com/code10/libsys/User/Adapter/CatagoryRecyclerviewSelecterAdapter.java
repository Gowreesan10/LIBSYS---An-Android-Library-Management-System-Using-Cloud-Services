package com.code10.libsys.User.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.General.Utility;
import com.code10.libsys.R;

import java.util.Arrays;
import java.util.List;

public class CatagoryRecyclerviewSelecterAdapter extends RecyclerView.Adapter<CatagoryRecyclerviewSelecterAdapter.ViewHolder> {

    private final Context context;
    List<String> catagory;
    Utility.updateRecyclerView updateRecyclerView;
    private int selectedItem;

    public CatagoryRecyclerviewSelecterAdapter(Utility.updateRecyclerView updateRecyclerView, Context context) {
        this.updateRecyclerView = updateRecyclerView;
        this.context = context;
        selectedItem = 0;
        catagory = Arrays.asList(context.getResources().getStringArray(R.array.Categories));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.btn_iv_book_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryTV.setText(catagory.get(holder.getAdapterPosition()));

        holder.cardCV.setCardBackgroundColor(context.getResources().getColor(R.color.white_A700));

        if (selectedItem == position) {
            holder.cardCV.setCardBackgroundColor(context.getResources().getColor(R.color.blue_500));
        }

        holder.itemView.setOnClickListener(view -> {
            updateRecyclerView.updateCategaoryView(catagory.get(holder.getAdapterPosition()));
            int previousItem = selectedItem;
            selectedItem = holder.getAdapterPosition();
            CatagoryRecyclerviewSelecterAdapter.this.notifyItemChanged(previousItem);
            CatagoryRecyclerviewSelecterAdapter.this.notifyItemChanged(selectedItem);
        });
    }

    public String getSelectedItem() {
        return catagory.get(selectedItem);
    }

    @Override
    public int getItemCount() {
        return catagory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTV;
        CardView cardCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTV = itemView.findViewById(R.id.btnFiction);
            cardCV = itemView.findViewById(R.id.selecterCard);
        }
    }
}