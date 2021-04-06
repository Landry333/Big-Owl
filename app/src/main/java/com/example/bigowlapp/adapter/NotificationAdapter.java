package com.example.bigowlapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> mNotificationTitles;
    private OnNotificationListener mOnNotificationListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_of_notification, parent, false);
        return new ViewHolder(v, mOnNotificationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mNotificationTitles.isEmpty()) {
            return;
        }

        Notification notification = mNotificationTitles.get(position);
        holder.getNameTextView().setText(notification.getTitle());
        holder.getContentTextView().setText(notification.getMessage() != null ? notification.getMessage() : "");
        holder.getTimeTextView().setText(notification.getCreationTime().toDate().toString());
    }

    @Override
    public int getItemCount() {
        return mNotificationTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTextView;
        private final TextView contentTextView;
        private final TextView timeTextView;
        private final OnNotificationListener onNotificationListener;

        public ViewHolder(View v, OnNotificationListener onNotificationListener) {
            super(v);
            nameTextView = v.findViewById(R.id.notification_name);
            contentTextView = v.findViewById(R.id.notification_content);
            timeTextView = v.findViewById(R.id.notification_time);
            this.onNotificationListener = onNotificationListener;

            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            onNotificationListener.onNotificationClick(getAdapterPosition());
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getContentTextView() {
            return contentTextView;
        }

        public TextView getTimeTextView() {
            return timeTextView;
        }
    }

    public interface OnNotificationListener {
        void onNotificationClick(int position);
    }

    public NotificationAdapter(List<Notification> notificationTitles, OnNotificationListener onNotificationListener) {
        mNotificationTitles = notificationTitles;
        this.mOnNotificationListener = onNotificationListener;
    }
}