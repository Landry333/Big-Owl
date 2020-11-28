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
import java.util.Map;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private  Map<User, Boolean> mSelectableValues;

    public UserRecyclerViewAdapter(List<User> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(position + 1));
        String strContent = mValues.get(position).getFullName() + "\n"
                + mValues.get(position).getEmail();
        holder.mContentView.setText(strContent);

        holder.mView.setBackgroundColor(Color.GRAY);

        if (mSelectableValues == null) {
            holder.mView.setBackgroundColor(Color.GREEN);
        } else if (mSelectableValues.get(mValues.get(position))) {
            holder.mView.setBackgroundColor(Color.GRAY);
        } else {
            holder.mView.setBackgroundColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setmSelectableValues(Map<User, Boolean> mSelectableValues) {
        this.mSelectableValues = mSelectableValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}