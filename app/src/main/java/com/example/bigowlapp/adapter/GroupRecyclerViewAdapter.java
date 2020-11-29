package com.example.bigowlapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.utils.GroupRecyclerViewListener;

import java.util.List;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private final List<Group> listOfGroup;
    private GroupRecyclerViewListener groupRecyclerViewListener;

    public GroupRecyclerViewAdapter(List<Group> items) {
        listOfGroup = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_group_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = listOfGroup.get(position);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mContentView.setText(listOfGroup.get(position).getName());
        holder.mView.setOnClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return listOfGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Group mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            groupRecyclerViewListener.onClickedGroup(mItem);
        }
    }

    public void setListener(GroupRecyclerViewListener listener) {
        this.groupRecyclerViewListener = listener;
    }
}