package com.example.bigowlapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SupervisionRequest;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";
    private List<Notification> mNotificationTitles;
    private Context mContext;



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mNotificationTitles.get(position).getType() + "\t" + mNotificationTitles.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mNotificationTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ConstraintLayout parentLayout;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.notification_name);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    public NotificationAdapter(List<Notification> notificationTitles, Context context) {
        mNotificationTitles = notificationTitles;
        mContext = context;
    }
}