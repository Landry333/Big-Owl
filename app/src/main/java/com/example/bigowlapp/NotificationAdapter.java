package com.example.bigowlapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";
    private ArrayList<String> mNotificationTitles = new ArrayList<>();
    private Context mContext;



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView)LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.textView.setText(mNotificationTitles.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotificationTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ConstraintLayout parentLayout;

        public ViewHolder(TextView v) {
            super(v);
            textView = v.findViewById(R.id.notification_name);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    public NotificationAdapter(ArrayList<String> notificationTitles, Context context) {
        mNotificationTitles = notificationTitles;
        mContext = context;
    }
}
