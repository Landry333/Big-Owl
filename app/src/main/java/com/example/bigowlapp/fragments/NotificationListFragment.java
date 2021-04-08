package com.example.bigowlapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.ScheduleViewRespondActivity;
import com.example.bigowlapp.adapter.NotificationAdapter;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.ReceiveScheduleNotification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;

import java.util.ArrayList;
import java.util.List;

public class NotificationListFragment extends Fragment implements NotificationAdapter.OnNotificationListener {

    private RecyclerView recyclerView;
    private LiveData<List<Notification>> notificationListData;
    private RepositoryFacade repositoryFacade;

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        repositoryFacade = RepositoryFacade.getInstance();
        notificationListData = repositoryFacade.getCurrentUserNotificationRepository()
                .getNotificationsByAscendingOrder(Notification.class);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        subscribeToData();
    }

    private void subscribeToData() {
        notificationListData.observe(getActivity(), notifications -> {
            if (notifications == null) {
                notifications = new ArrayList<>();
            }
            NotificationAdapter mAdapter = new NotificationAdapter(notifications, this);
            recyclerView.setAdapter(mAdapter);
        });
    }

    @Override
    public void onNotificationClick(int position) {
        Notification selectedNotification = notificationListData.getValue().get(position);


        if (selectedNotification.getType() == Notification.Type.SUPERVISION_REQUEST) {
            getParentFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(),
                            new SupervisionResponseFragment((SupervisionRequest) selectedNotification))
                    .addToBackStack(null)
                    .commit();
        }

        if (selectedNotification.getType() == Notification.Type.SCHEDULE_NOTIFICATION) {
            ReceiveScheduleNotification selectedNotification2 = (ReceiveScheduleNotification) selectedNotification;
            repositoryFacade.getUserRepository().getDocumentByUid(selectedNotification2.getSenderUid(), User.class).observe(this, supervisor -> {
                if (supervisor == null) {
                    return;
                }
                Intent intent = new Intent(getContext(), ScheduleViewRespondActivity.class);
                intent.putExtra("scheduleUid", selectedNotification2.getScheduleUid());
                intent.putExtra("groupName", selectedNotification2.getGroupName());
                intent.putExtra("supervisorName", supervisor.getFullName());
                startActivity(intent);
            });


        }
    }
}