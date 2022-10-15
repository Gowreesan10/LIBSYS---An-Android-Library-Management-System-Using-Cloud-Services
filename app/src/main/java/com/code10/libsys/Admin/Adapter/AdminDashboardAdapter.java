package com.code10.libsys.Admin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Activity.AddCopies;
import com.code10.libsys.Admin.Activity.CodeScanner;
import com.code10.libsys.Admin.Activity.QRCodeGenerator;
import com.code10.libsys.Admin.Activity.ReturnBooksActivity;
import com.code10.libsys.Admin.Activity.SelectCategoryActivity;
import com.code10.libsys.Admin.Activity.UserDetailsView;
import com.code10.libsys.R;


public class AdminDashboardAdapter extends RecyclerView.Adapter<AdminDashboardAdapter.ViewHolder> {

    Context mcontext;
    AlertDialog alertDialog;

    public AdminDashboardAdapter(Context context) {
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_dashboard_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        switch (holder.getAdapterPosition()) {
            case 0: {
                holder.icon.setImageResource(R.drawable.books);
                holder.option.setText("View All books");
                break;
            }
            case 1: {
                holder.icon.setImageResource(R.drawable.useraccbig);
                holder.option.setText("View All Users");
                break;
            }
            case 2: {
                holder.icon.setImageResource(R.drawable.addbook);
                holder.option.setText("Add New Book");
                break;
            }
            case 3: {
                holder.icon.setImageResource(R.drawable.icons8returnbook100);
                holder.option.setText("Return Book");
                break;
            }
            case 4: {
                holder.icon.setImageResource(R.drawable.newcopy);
                holder.option.setText("Add A Copy");
                break;
            }
            default: {
                holder.icon.setImageResource(R.drawable.qrcode);
                holder.option.setText("QR Code");
                break;
            }
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = null;
            int adapterPosition = holder.getAdapterPosition();
            switch (adapterPosition) {
                case 0:
                    intent = new Intent(view.getContext(), SelectCategoryActivity.class);
                    break;
                case 1:
                    intent = new Intent(view.getContext(), UserDetailsView.class);
                    break;
                case 2:
                    intent = new Intent(view.getContext(), CodeScanner.class);
                    break;
                case 3:
                    intent = new Intent(view.getContext(), ReturnBooksActivity.class);
                    break;
                case 4:
                    dialog();
                    break;
                default:
                    intent = new Intent(view.getContext(), QRCodeGenerator.class);
                    break;
            }
            if (intent != null) {
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void dialog() {
        AlertDialog.Builder alaBuilder = new AlertDialog.Builder(mcontext);
        alaBuilder.setTitle("Alert!");
        alaBuilder.setMessage("Scan A Copy Of The Book");
        alaBuilder.setPositiveButton("Ok", (dialog, which) -> {
            Intent i = new Intent(mcontext, AddCopies.class);
            mcontext.startActivity(i);
            alertDialog.dismiss();
        });
        alaBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            alertDialog.dismiss();
        });
        alertDialog = alaBuilder.create();
        alertDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            option = itemView.findViewById(R.id.option);
        }
    }

}

