package com.ahsan.a27smanthademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FriendlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        //Add Friendlist Fragment to Friendlist Container activity
        FriendlistFragment friendlistFragment = new FriendlistFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.friendlistActivity, friendlistFragment).commit();
    }
}
