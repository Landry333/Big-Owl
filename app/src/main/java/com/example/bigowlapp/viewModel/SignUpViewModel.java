package com.example.bigowlapp.viewModel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.google.android.gms.tasks.Task;

public class SignUpViewModel extends ViewModel {

    private AuthRepository authRepository;
    private GroupRepository groupRepository;


    @ViewModelInject
    public SignUpViewModel(AuthRepository authRepository, GroupRepository groupRepository) {
        this.authRepository = authRepository;
        this.groupRepository = groupRepository;
    }
    
    public Task<Boolean> createUser(String email, String password, String phoneNumber, String name) {
        return authRepository.signUpUser(email, password, phoneNumber, nameExtractor(name)[0],
                nameExtractor(name)[1])
                .addOnSuccessListener(isSuccess -> {
                    Group group = new Group();
                    group.setMonitoringUserId(authRepository.getCurrentUser().getUid());
                    group.setName(name + "'s group " + "#" + randomStringGenerator());
                    groupRepository.addDocument(group);
                });

    }

    public String nameParcer(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String[] nameExtractor(String name) {
        return name.split(" ", 2);
    }

    public String randomStringGenerator() {
        StringBuilder randomStr = new StringBuilder();
        int max = 9;
        int min = 0;
        for (int i = 0; i < 4; i++) {
            randomStr.append((int) ((Math.random() * (max + 1)) + min));
        }
        return randomStr.toString();
    }

}
