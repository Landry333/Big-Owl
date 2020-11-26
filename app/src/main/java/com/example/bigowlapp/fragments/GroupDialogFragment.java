package com.example.bigowlapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.GroupRecyclerViewAdapter;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.utils.GroupRecyclerViewListener;

import java.util.List;

public class GroupDialogFragment extends DialogFragment implements GroupRecyclerViewListener {

    private GroupRecyclerViewListener groupRecyclerViewListener;
    private List<Group> listOfGroup;

    public GroupDialogFragment() {
    }

    public GroupDialogFragment(GroupRecyclerViewListener listener, List<Group> listOfGroup) {
        this.groupRecyclerViewListener = listener;
        this.listOfGroup = listOfGroup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_item_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            GroupRecyclerViewAdapter adapter = new GroupRecyclerViewAdapter(listOfGroup);
            adapter.setListener(this);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onClickedGroup(Group group) {
        groupRecyclerViewListener.onClickedGroup(group);
        dismiss();
    }
}