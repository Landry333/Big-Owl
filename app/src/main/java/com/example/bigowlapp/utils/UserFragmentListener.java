package com.example.bigowlapp.utils;

import com.example.bigowlapp.model.User;

import java.util.List;

public interface UserFragmentListener {
    void userFragmentOnSubmitClicked(List<User> selectedUsers);
}
