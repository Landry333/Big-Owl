package com.example.bigowlapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;

public class SupervisionResponseFragment extends Fragment {

    private Button acceptBtn;
    private Button rejectBtn;
    private TextView groupName;
    private TextView groupSupervisor;
    private RepositoryFacade repositoryFacade;
    private SupervisionRequest supervisionRequest;

    public SupervisionResponseFragment() {

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

        acceptBtn = view.findViewById(R.id.button_accept);
        rejectBtn = view.findViewById(R.id.button_reject);
        groupName = view.findViewById(R.id.text_view_group_name);
        groupSupervisor = view.findViewById(R.id.text_view_group_supervisor_name);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToData();
    }

    private void subscribeToData() {
        repositoryFacade = RepositoryFacade.getInstance();

        LiveDataWithStatus<User> userLiveData = repositoryFacade.getUserRepository()
                .getDocumentByUid(supervisionRequest.getSenderUid(), User.class);

        userLiveData.observe(getActivity(), supervisor -> {
            groupSupervisor.setText(supervisor.getFullName());
            LiveDataWithStatus<Group> groupLiveData = repositoryFacade.getGroupRepository()
                    .getDocumentByAttribute("supervisorId", supervisor.getUid(), Group.class);

            groupLiveData.observe(getActivity(), group -> {
                groupName.setText(group.getName());

                setupAcceptBtn(group);
                setupRejectBtn();
            });
        });
    }

    private void removeNotification() {
        repositoryFacade.getCurrentUserNotificationRepository()
                .removeDocument(supervisionRequest.getUid());

        getActivity().onBackPressed();
    }

    private void setupRejectBtn() {
        rejectBtn.setOnClickListener(view -> removeNotification());
    }

    private void setupAcceptBtn(Group group) {
        acceptBtn.setOnClickListener(view -> {
            group.getMemberIdList().add(supervisionRequest.getReceiverUid());

            repositoryFacade.getUserRepository().getDocumentByUid(supervisionRequest.getReceiverUid(), User.class).observe(getActivity(), receiver -> {
                receiver.getMemberGroupIdList().add(group.getUid());

                repositoryFacade.getGroupRepository().updateDocument(group.getUid(), group);
                repositoryFacade.getUserRepository().updateDocument(receiver.getUid(), receiver);

                removeNotification();
            });
        });
    }
}