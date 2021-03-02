package com.example.bigowlapp.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.NotificationAdapter;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationListFragment extends Fragment implements NotificationAdapter.OnNotificationListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private NotificationRepository notificationRepository;
    private LiveData<List<Notification>> notificationListData;
    private AuthRepository authRepository;

    public NotificationListFragment() {
        // Required empty public constructor
    }

    public static NotificationListFragment newInstance() {
        NotificationListFragment fragment = new NotificationListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_notifications);

        authRepository = new AuthRepository();
        notificationRepository = new NotificationRepository();
        notificationListData = notificationRepository.getListOfDocumentByAttribute("receiverUid", authRepository.getCurrentUser().getUid(), Notification.class);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        notificationListData.observe(getActivity(), notifications -> {
            if (notifications == null) {
                notifications = new ArrayList<>();
            }
            mAdapter = new NotificationAdapter(notifications, this);
            recyclerView.setAdapter(mAdapter);
        });

        return view;
    }

    @Override
    public void onNotificationClick(int position) {
        if(notificationListData.getValue().get(position).getType() == Notification.Type.SCHEDULE_REQUEST){

        }
    }
}