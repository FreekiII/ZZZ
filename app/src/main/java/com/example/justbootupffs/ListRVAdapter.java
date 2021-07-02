package com.example.justbootupffs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justbootupffs.Activity.ProfileActivity;
import com.example.justbootupffs.Entity.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListRVAdapter extends RecyclerView.Adapter<ListRVAdapter.ViewHolder> {
    private ArrayList<User> users = new ArrayList<>();
    private Context context;

    public ListRVAdapter(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ListRVAdapter.ViewHolder holder, int position) {
        holder.textView.setText(users.get(position).name + " " + users.get(position).surname);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfile = new Intent(context, ProfileActivity.class);
                intentProfile.putExtra("user_id", users.get(position).id);
                intentProfile.putExtra("edit", "0");
                context.startActivity(intentProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
            constraintLayout = itemView.findViewById(R.id.parentItem);
        }
    }
}
