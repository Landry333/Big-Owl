package com.example.bigowlapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private final List<User> users;
    private List<User> selectedUsers;

    public UserRecyclerViewAdapter(List<User> users, List<User> selectedUsers) {
        this.users = users;
        this.selectedUsers = selectedUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setmUser(users.get(position));
        holder.mIdView.setText(String.valueOf(position + 1));
        String strContent = users.get(position).getFullName() + "\n"
                + users.get(position).getEmail();
        holder.mContentView.setText(strContent);
        holder.mView.setOnClickListener(holder);
        holder.mView.setBackgroundColor(selectedUsers.contains(holder.mUser) ? Color.GRAY : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        private User mUser;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        public void setmUser(User mUser) {
            this.mUser = mUser;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if (selectedUsers.contains(mUser)) {
                selectedUsers.remove(mUser);
                v.setBackgroundColor(Color.WHITE);
            } else {
                selectedUsers.add(mUser);
                v.setBackgroundColor(Color.GRAY);
            }
        }
    }
}