package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.repository.GroupRepository;

public class SupervisedGroupListViewModel {

    private GroupRepository groupRepository;

    public SupervisedGroupListViewModel(){
        groupRepository = new GroupRepository();
    }
}
