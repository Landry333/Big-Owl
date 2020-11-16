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

import org.w3c.dom.Text;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";
    private List<Notification> mNotificationTitles;
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!mNotificationTitles.isEmpty()) {
            holder.nameTextView.setText(mNotificationTitles.get(position).getType());
            holder.timeTextView.setText(mNotificationTitles.get(position).getTime().toDate().toString());
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView contentTextView;
        public TextView timeTextView;
        public ConstraintLayout parentLayout;

        public ViewHolder(View v) {
            super(v);
            nameTextView = v.findViewById(R.id.notification_name);
            contentTextView = v.findViewById(R.id.notification_content);
            timeTextView = v.findViewById(R.id.notification_time);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    public NotificationAdapter(List<Notification> notificationTitles, Context context) {
        mNotificationTitles = notificationTitles;
        mContext = context;
    }
}