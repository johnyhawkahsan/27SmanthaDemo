package com.ahsan.a27smanthademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Defining RegisterFragment in Register Activity
        //id registerContainer is activity_register
        RegisterFragment registerFragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.registerContainer, registerFragment).commit();//Add MenuFragment to container which is activity_main

    }
}
