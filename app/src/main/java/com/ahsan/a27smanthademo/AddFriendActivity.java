package com.ahsan.a27smanthademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //Add addFriendFragment to FriendListContainer which is the id of AddFriendActivity
        AddFriendFragment addFriendFragment = new AddFriendFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.addFriendActivity, addFriendFragment).commit();
    }
}
