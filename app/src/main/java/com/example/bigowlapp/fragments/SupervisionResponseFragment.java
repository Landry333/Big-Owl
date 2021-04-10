package com.example.bigowlapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.view_model.NotificationActivityViewModel;

public class SupervisionResponseFragment extends Fragment {

    private Button acceptBtn;
    private Button rejectBtn;
    private TextView groupName;
    private TextView groupSupervisor;

    private SupervisionRequest supervisionRequest;
    private NotificationActivityViewModel notificationActivityViewModel;

    public SupervisionResponseFragment() {
        // Fragments require a public empty Constructor
        // TODO: Verify this is the case
    }

    public SupervisionResponseFragment(SupervisionRequest sr) {
        this.supervisionRequest = sr;
    }

    public static SupervisionResponseFragment newInstance(SupervisionRequest sr) {
        return new SupervisionResponseFragment(sr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervision_response, container, false);

        acceptBtn = view.findViewById(R.id.button_accept_supervision_req);
        rejectBtn = view.findViewById(R.id.button_reject_supervision_req);
        groupName = view.findViewById(R.id.text_view_group_name);
        groupSupervisor = view.findViewById(R.id.text_view_group_supervisor_name);

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

        notificationActivityViewModel.getSenderUserData(supervisionRequest.getSenderUid()).observe(getActivity(), supervisor ->
                notificationActivityViewModel.getGroupData(supervisor.getUid()).observe(getActivity(), group -> {
                    groupSupervisor.setText(supervisor.getFullName());
                    groupName.setText(group.getName());
                    setupAcceptBtn(group);
                    setupRejectBtn();
                }));
    }

    private void setupAcceptBtn(Group group) {
        acceptBtn.setOnClickListener(view -> {
            notificationActivityViewModel.joinGroup(group, supervisionRequest.getUid());
            getActivity().onBackPressed();
        });
    }

    private void setupRejectBtn() {
        rejectBtn.setOnClickListener(view -> {
            notificationActivityViewModel.deleteNotification(supervisionRequest.getUid());
            getActivity().onBackPressed();
        });
    }
}