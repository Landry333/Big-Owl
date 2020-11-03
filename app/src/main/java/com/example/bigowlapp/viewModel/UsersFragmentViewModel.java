package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;

public class UsersFragmentViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final GroupRepository groupRepository;

    public UsersFragmentViewModel() {
        authRepository = new AuthRepository();
        groupRepository = new GroupRepository();
    }
    
}
