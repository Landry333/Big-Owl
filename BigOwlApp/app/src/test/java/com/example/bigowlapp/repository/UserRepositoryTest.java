package com.example.bigowlapp.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.bigowlapp.model.User;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserRepositoryTest {

    @Ignore
    @Test
    public void getUserByPhoneNumber() throws InterruptedException {
        UserRepository r = new UserRepository();
        MutableLiveData<User> userData = r.getUserByPhoneNumber("+16505554567");
        User user = userData.getValue();

        Thread.sleep(5000);

        System.out.println(user);
        assertEquals("+16505554567", user.getPhoneNumber());
    }
}