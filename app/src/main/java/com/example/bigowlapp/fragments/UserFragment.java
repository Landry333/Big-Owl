package com.example.bigowlapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.UserRecyclerViewAdapter;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.UserRecyclerViewListener;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFragment extends Fragment implements UserRecyclerViewListener {

    private SetScheduleViewModel setScheduleViewModel;
    private List<User> listOfUsers;
    private Map<User, Boolean> listOfSelectedUsers;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;


    public UserFragment(SetScheduleViewModel viewModel) {
        setScheduleViewModel = viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_item_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            listOfUsers = new ArrayList<>();
            listOfSelectedUsers = new HashMap<>();
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(listOfUsers);
            userRecyclerViewAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(userRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void subscribeToData() {
//        setScheduleViewModel.getListOfUserInGroupData().observe(this, users -> {
//            listOfUsers.clear();
//            listOfUsers.addAll(users);
//            userRecyclerViewAdapter.notifyDataSetChanged();
//
//            listOfSelectedUsers = new HashMap<>();
//            for (User listOfUser : listOfUsers) {
//                listOfSelectedUsers.put(listOfUser, false);
//            }
//        });

        setScheduleViewModel.getSelectableUsersData().observe(this, selectableUsers -> {
            listOfUsers.clear();
            listOfUsers.addAll(new ArrayList<>(selectableUsers.keySet()));
            listOfSelectedUsers.clear();
            listOfSelectedUsers.putAll(selectableUsers);
            userRecyclerViewAdapter.setmSelectableValues(listOfSelectedUsers);
            userRecyclerViewAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onClickedUser(User user) {
        listOfSelectedUsers.put(user, !listOfSelectedUsers.get(user));
    }
}