package com.code10.libsys.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Model.Category;
import com.code10.libsys.General.Activity.viewGridBooks;
import com.code10.libsys.R;

import java.util.ArrayList;

public class CategoryBookAdapter extends RecyclerView.Adapter<CategoryBookAdapter.ViewHolder> {
    private ArrayList<Category> listdata;
    private Context mcontext;
    private ArrayList<Category> filtered;

    public CategoryBookAdapter(ArrayList<Category> listdata, Context mcontext) {
        this.listdata = listdata;
        this.mcontext = mcontext;
        filtered = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.category_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(filtered.get(holder.getAbsoluteAdapterPosition()).getDescription());
//        holder.textView.setImageResource(listdata[position].getImgId());
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(mcontext, viewGridBooks.class);
            i.putExtra("SeeAll", filtered.get(holder.getAbsoluteAdapterPosition()).getDescription());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mcontext.startActivity(i);
        });
    }

    public void setQuery(String search) {
        filtered = new ArrayList<>();
        if (!search.equals("")) {
            for (Category cat : listdata) {
                if (cat.getDescription().toLowerCase().startsWith(search.toLowerCase())) {
                    filtered.add(cat);
                }
            }
        } else {
            filtered = listdata;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
//            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.requester);
        }
    }
}
