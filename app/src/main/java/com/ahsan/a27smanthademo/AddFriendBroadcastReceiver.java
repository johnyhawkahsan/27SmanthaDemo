package com.ahsan.a27smanthademo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AddFriendBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Get action from AddFriendService methods broadcastAddFriendSuccess() and broadcastAddFriendFailure()
        String action = intent.getAction();
        if (action.equals(Constants.BROADCAST_ADD_FRIEND_SUCCESS)){
            Toast.makeText(context, "Added friend!", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Constants.BROADCAST_ADD_FRIEND_FAILURE)){
            Toast.makeText(context, "Failed to add friend!", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Constants.BROADCAST_FRIEND_REQUEST_SUCCESS)){
            Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Constants.BROADCAST_FRIEND_REQUEST_FAILURE)){
            Toast.makeText(context, "Failed to send Friend Request", Toast.LENGTH_SHORT).show();
        }

    }
}