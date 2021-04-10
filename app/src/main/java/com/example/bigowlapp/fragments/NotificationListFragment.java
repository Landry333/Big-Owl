package com.example.bigowlapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.ScheduleViewRespondActivity;
import com.example.bigowlapp.adapter.NotificationAdapter;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.ReceiveScheduleNotification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.view_model.NotificationActivityViewModel;

import java.util.ArrayList;

public class NotificationListFragment extends Fragment implements NotificationAdapter.OnNotificationListener {

    private RecyclerView recyclerView;
    private NotificationActivityViewModel notificationActivityViewModel;

    public NotificationListFragment() {
        // Required empty public constructor
    }

    public static NotificationListFragment newInstance() {
        return new NotificationListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_notifications);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (notificationActivityViewModel == null) {
            notificationActivityViewModel = new ViewModelProvider(this).get(NotificationActivityViewModel.class);
        }

        subscribeToData();
    }

    private void subscribeToData() {
        if (!notificationActivityViewModel.isCurrentUserSet()) {
            return;
        }

        notificationActivityViewModel.getUserNotifications().observe(getActivity(), notifications -> {
            if (notifications == null) {
                notifications = new ArrayList<>();
            }

            NotificationAdapter mAdapter = new NotificationAdapter(notifications, this);
            recyclerView.setAdapter(mAdapter);
        });
    }

    @Override
    public void onNotificationClick(int position) {
        Notification selectedNotification = notificationActivityViewModel.getUserNotifications()
                .getValue().get(position);

        if (selectedNotification.getType() == Notification.Type.SUPERVISION_REQUEST) {
            getParentFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(),
                            SupervisionResponseFragment
                                    .newInstance((SupervisionRequest) selectedNotification))
                    .addToBackStack(null)
                    .commit();
        }

        if (selectedNotification.getType() == Notification.Type.SCHEDULE_NOTIFICATION) {
            ReceiveScheduleNotification selectedReceiveScheduleNotification = (ReceiveScheduleNotification) selectedNotification;

            notificationActivityViewModel.getSenderUserData(selectedReceiveScheduleNotification.getSenderUid()).observe(this, supervisor -> {
                if (supervisor == null) {
                    return;
                }

                Intent intent = new Intent(getContext(), ScheduleViewRespondActivity.class);
                intent.putExtra("scheduleUid", selectedReceiveScheduleNotification.getScheduleUid());
                intent.putExtra("groupName", selectedReceiveScheduleNotification.getGroupName());
                intent.putExtra("supervisorName", supervisor.getFullName());
                startActivity(intent);
            });
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setNotificationActivityViewModel(NotificationActivityViewModel notificationActivityViewModel) {
        this.notificationActivityViewModel = notificationActivityViewModel;
    }
}