package com.example.bigowlapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.UserRecyclerViewAdapter;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.UserFragmentListener;

import java.util.List;

public class UserFragment extends Fragment {

    private List<User> listOfUsers;
    private List<User> listOfSelectedUsers;

    private Button cancelButton;
    private Button confirmButton;

    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private UserFragmentListener userFragmentListener;

    public UserFragment() {
    }

    public UserFragment(UserFragmentListener listener,
                        List<User> listOfUsers, List<User> listOfSelectedUsers) {
        this.listOfUsers = listOfUsers;
        this.listOfSelectedUsers = listOfSelectedUsers;
        this.userFragmentListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.user_item_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(listOfUsers, listOfSelectedUsers);
        recyclerView.setAdapter(userRecyclerViewAdapter);

        cancelButton = view.findViewById(R.id.user_item_list_cancel);
        confirmButton = view.findViewById(R.id.user_item_list_confirm);
        setupButtons();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        // TODO: Use the ViewModel
    }

    public void setupButtons() {
        cancelButton.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
        confirmButton.setOnClickListener(view -> {
            userFragmentListener.userFragmentOnSubmitClicked(listOfSelectedUsers);
            getParentFragmentManager().popBackStack();
        });
    }

}