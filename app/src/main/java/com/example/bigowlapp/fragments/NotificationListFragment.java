package com.example.bigowlapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.NotificationAdapter;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.repository.RepositoryFacade;

import java.util.ArrayList;
import java.util.List;

public class NotificationListFragment extends Fragment implements NotificationAdapter.OnNotificationListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LiveData<List<Notification>> notificationListData;

    public NotificationListFragment() {
        // Required empty public constructor
    }

    public static NotificationListFragment newInstance() {
        NotificationListFragment fragment = new NotificationListFragment();
        return fragment;
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

        RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

        notificationListData = repositoryFacade.getNotificationRepository().getListOfDocumentByAttribute("receiverUid", repositoryFacade.getAuthRepository().getCurrentUser().getUid(), Notification.class);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        subscribeToData();
    }

    private void subscribeToData() {
        notificationListData.observe(getActivity(), notifications -> {
            if (notifications == null) {
                notifications = new ArrayList<>();
            }
            mAdapter = new NotificationAdapter(notifications, this);
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
    }
}