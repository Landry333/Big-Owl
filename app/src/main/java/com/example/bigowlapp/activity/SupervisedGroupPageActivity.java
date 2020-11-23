package com.example.bigowlapp.activity;

import android.os.Bundle;

import com.example.bigowlapp.R;

public class SupervisedGroupPageActivity extends BigOwlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_supervised_group_page;
    }
}