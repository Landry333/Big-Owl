package com.example.bigowlapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SupervisedGroupListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<GroupNameSupervisor> groupNameSupervisorsList;
    private List<QueryDocumentSnapshot> qds = new ArrayList<QueryDocumentSnapshot>();
    private List<String> fullNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_group_list);
        initialize();
    }

    protected void initialize() {
        try {
            groupNameSupervisorsList = new ArrayList<>();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Query query = db.collection("groups").whereArrayContains("supervisedUserId", currentUser.getUid());

                query.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                    if (task.isSuccessful()) {
                        ArrayList<GroupNameSupervisor> arrayOfUsers = new ArrayList<GroupNameSupervisor>();
                        GroupNameSupervisorAdapter adapter = new GroupNameSupervisorAdapter(getBaseContext(), arrayOfUsers);
                        GroupNameSupervisor groupNameSupervisor = new GroupNameSupervisor();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            qds.add(document);
                            groupNameSupervisor.groupName = document.getString("name");
                            DocumentReference docRef = db.collection("users").document(document.getString("monitoringUserId"));
                            docRef.get().addOnCompleteListener(task1 -> {
                                DocumentSnapshot document1 = task1.getResult();
                                document1.getString("firstName");
                                document1.getString("lastName");
                                fullNameList.add(document1.getString("firstName") + " " + document1.getString("lastName"));
                            });
                        }
                        adapter.add(groupNameSupervisor);
                        ListView listView = (ListView) findViewById(R.id.listView_supervisedGroup);
                        listView.setAdapter(adapter);
                    }
                });
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private class GroupNameSupervisor {
        public String groupName;
        public String groupSupervisor;

        public GroupNameSupervisor(String groupName, String groupSupervisor) {
            this.groupName = groupName;
            this.groupSupervisor = groupSupervisor;
        }

        public GroupNameSupervisor() {
            this.groupName = "";
            this.groupSupervisor = "";
        }
    }

    private class GroupNameSupervisorAdapter extends ArrayAdapter<GroupNameSupervisor> {
        public GroupNameSupervisorAdapter(@NonNull Context context, ArrayList<GroupNameSupervisor> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            GroupNameSupervisor groupNameSupervisor = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_supervised_group_list, parent, false);
            }
            // Lookup view for data population
            TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
            TextView groupSupervisor = (TextView) convertView.findViewById(R.id.groupSupervisor);
            // Populate the data into the template view using the data object
            groupName.setText(groupNameSupervisor.groupName);
            groupSupervisor.setText(groupNameSupervisor.groupSupervisor);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

