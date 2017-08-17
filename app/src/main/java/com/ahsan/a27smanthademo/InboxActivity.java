package com.ahsan.a27smanthademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        //Add InboxFragment to InboxActivity
        InboxFragment inboxFragment = new InboxFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.inboxActivity, inboxFragment).commit();

    }
}
