package com.code10.libsys.Admin;

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

import com.code10.libsys.CodeScanner;
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
                holder.icon.setImageResource(R.drawable.img_books);
                holder.option.setText("View All books");
                break;
            }
            case 1: {
                holder.icon.setImageResource(R.drawable.img_books);
                holder.option.setText("View All Users");
                break;
            }
            case 2: {
                holder.icon.setImageResource(R.drawable.img_books);
                holder.option.setText("Add New Book");
                break;
            }
            case 3: {
                holder.icon.setImageResource(R.drawable.img_books);
                holder.option.setText("Return Book");
                break;
            }
            case 4: {
                holder.icon.setImageResource(R.drawable.img_books);
                holder.option.setText("Remove Book");
                break;
            }
            default: {
                holder.icon.setImageResource(R.drawable.img_books);
                holder.option.setText("Settings");
                break;
            }
        }

        holder.itemView.setOnClickListener(view -> {
            switch (holder.getAdapterPosition()) {
                case 0: {
                    break;

                }
                case 1: {
                    break;
                }
                case 2: {

//                    AlertDialog.Builder alaBuilder = new AlertDialog.Builder(view.getContext());
//                    alaBuilder.setTitle("Enter Title or Author or ISBN");
//
//                    View view1 = LayoutInflater.from(view.getContext()).inflate(R.layout.alart_dialog_enter_title_author_isbn, null);
//                    alaBuilder.setView(view1);
//
//                    alertDialog = alaBuilder.create();
//                    alertDialog.show();
//
//                    EditText book = view1.findViewById(R.id.idEdtSearchBooks);
//                    ImageButton iv = view1.findViewById(R.id.idBtnSearch);
//                    iv.setOnClickListener(view2 -> {
//                        Intent intent = new Intent(view.getContext(), AddBooks.class);
//                        intent.putExtra("Search", book.getText().toString());
//                        view.getContext().startActivity(intent);
//                        alertDialog.dismiss();
//                    });

                    Intent intent = new Intent(view.getContext(), CodeScanner.class);
                    view.getContext().startActivity(intent);

                    break;
                }
                case 3: {
                    break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6;
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
