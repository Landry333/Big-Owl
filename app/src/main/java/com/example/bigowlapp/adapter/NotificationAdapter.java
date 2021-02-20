package com.example.bigowlapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";
    private List<Notification> mNotificationTitles;
//    private Context mContext;
    private OnNotificationListener mOnNotificationListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_of_notification, parent, false);
        return new ViewHolder(v, mOnNotificationListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!mNotificationTitles.isEmpty()) {
            holder.nameTextView.setText(mNotificationTitles.get(position).getType().toString());
            holder.timeTextView.setText(mNotificationTitles.get(position).getTime().toDate().toString());
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView contentTextView;
        public TextView timeTextView;
        public ConstraintLayout parentLayout;
        OnNotificationListener onNotificationListener;

        public ViewHolder(View v, OnNotificationListener onNotificationListener) {
            super(v);
            nameTextView = v.findViewById(R.id.notification_name);
            contentTextView = v.findViewById(R.id.notification_content);
            timeTextView = v.findViewById(R.id.notification_time);
            parentLayout = v.findViewById(R.id.parent_layout);
            this.onNotificationListener = onNotificationListener;

            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            onNotificationListener.onNotificationClick(getAdapterPosition());
        }
    }

    public interface  OnNotificationListener {
        void onNotificationClick(int position);
    }

    public NotificationAdapter(List<Notification> notificationTitles, OnNotificationListener onNotificationListener) {
        mNotificationTitles = notificationTitles;
        this.mOnNotificationListener = onNotificationListener;
    }
}