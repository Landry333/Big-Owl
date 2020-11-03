package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.viewModel.SupervisedGroupListViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SupervisedGroupListActivity extends AppCompatActivity {
    private ListView lv;
    private SupervisedGroupListViewModel supervisedGroupListViewModel;
    private LiveData<List<Group>> listOfGroupsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_group_list);
        initialize();
    }

    protected void initialize() {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                supervisedGroupListViewModel = new SupervisedGroupListViewModel();
                listOfGroupsLiveData = supervisedGroupListViewModel.setListOfDocumentByArrayContains();

                listOfGroupsLiveData.observe(this, groups -> {
                    ArrayList<String> arrayGroupName = new ArrayList<>();
                    for (Group group : groups) {
                        arrayGroupName.add(group.getName());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, arrayGroupName);
                    lv = findViewById(R.id.listView_supervisedGroup);
                    lv.setAdapter(arrayAdapter);
                    // argument position gives the index of item which is clicked
                    lv.setOnItemClickListener((arg0, v, position, arg3) -> {
                        Intent intent = new Intent(getBaseContext(), SupervisedGroupPageActivity.class);
                        startActivity(intent);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

