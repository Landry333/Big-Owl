package com.example.bigowlapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.example.bigowlapp.viewModel.WelcomeViewModel;

public class WelcomeFragment extends Fragment {

    private WelcomeViewModel mViewModel;

    private TextView textView;

    private UserRepository userRepository;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.message);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(WelcomeViewModel.class);
        // TODO: Use the ViewModel

        mViewModel.setUserByPhoneNumber("+16505554567");
        //userLiveData = userRepository.getUserByUId("test");
        mViewModel.getUserData().observe(this, user -> textView.setText(user.getEmail()));

    }

}