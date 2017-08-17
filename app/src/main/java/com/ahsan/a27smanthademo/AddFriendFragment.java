package com.ahsan.a27smanthademo;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment {

    String currentUserName;//Global variable for currentUserName- It either holds data from addFriend() OR sendFriendRequest()
    String currentUserId;//Get ID of the logged in user so it can add more people
    String currentUserFriends;//Global variable for currentUserFriends

    public AddFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        //Save currentUserId in global variable
        currentUserId = Backendless.UserService.loggedInUser();

        //Add Friend Button Code
        Button addFriendButton = (Button) view.findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a Dialoge when someone clicks on Add Friend button
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Add a Friend.");//Title text of dialog box

                //Type the name of the user you want to add to your friends list
                final EditText inputField = new EditText(getActivity());//Setting EditText field
                alertDialog.setView(inputField);

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User cancelled. Do nothing
                        Toast.makeText(getActivity(), "You cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //TODO: Here we call addFriend() method instead of sendFriendRequest()
                        //call addFriend() method and pass String friendName that user entered in text field
                        Toast.makeText(getActivity(), "Add " + inputField.getText().toString() + " as a Friend!", Toast.LENGTH_SHORT).show();
                        addFriend(inputField.getText().toString());//Not using right now

                        //TODO: Here we call sendFriendRequest() instead of addFriend()
                        //call sendFriendRequest() method and pass String friendName that user entered in text field
                        //sendFriendRequest(inputField.getText().toString());
                        //Toast.makeText(getActivity(), "Send Friend Request to " + inputField.getText().toString() + "!", Toast.LENGTH_SHORT).show();
                    }
                });

                //Create and Show this dialog
                alertDialog.create();
                //Now we want to create a service called AddFriendService
                alertDialog.show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }







    //This method runs when someone clicks Add button-Either this addFriend() method runs or sendFriendRequest() runs
    //It eventually sends intent data like current firstUserName, secondUserName and Launches AddFriendService.class
    private void addFriend(final String friendName){

        Log.i("addFriend()", "addFriend() method running in AddFriendFragment");

        //Find details of logged in user
        Backendless.Persistence.of(BackendlessUser.class).findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currUser) {

                Log.i("currentUserData", currUser.toString());
                //Save value of current user in global variable
                currentUserName = currUser.getProperty("name").toString();
                Log.i("CurrentUserName", currentUserName);

                currentUserFriends = currUser.getProperty("friends").toString();
                Log.i("CurrentUserFriends", currentUserFriends);

                Toast.makeText(getContext(), "Current User Name in addFriend(): " + currentUserName,Toast.LENGTH_SHORT).show();

                //Sending this intent to AddFriendService.java
                Intent intent = new Intent(getActivity(), AddFriendService.class);
                intent.setAction(Constants.ACTION_ADD_FRIEND);//We will use this action to identify our intent

                //Send extra information in intent, which is the name property of the LoggedinUser
                intent.putExtra("firstUserName", currentUserName);
                intent.putExtra("secondUserName", friendName);


                //Now we are starting our service
                getActivity().startService(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("FaultGetting currUser: ", fault.toString());
            }
        });
    }





    //This method runs when someone clicks Add button-Either sendFriendRequest() runs or addFriend() runs
    //It eventually sends intent data like current toUser, fromUser and Launches AddFriendService.class
    private void sendFriendRequest(final String friendName){

        Log.i("sendFriendRequest()", "sendFriendRequest() method running in AddFriendFragment");

        Backendless.Persistence.of(BackendlessUser.class).findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currUser) {

                Log.i("currentUserData", currUser.toString());
                //Save value of current user in global variable
                currentUserName = currUser.getProperty("name").toString();
                Log.i("CurrentUserName", currentUserName);

                Toast.makeText(getContext(), "Current User Name in sendFriendRequest(): " + currentUserName,Toast.LENGTH_SHORT).show();

                //Sending this intent to AddFriendService.java
                Intent intent = new Intent(getActivity(), AddFriendService.class);
                intent.setAction(Constants.ACTION_SEND_FRIEND_REQUEST);

                //Send extra information in intent, which is the name property of the LoggedinUser
                intent.putExtra("fromUser", currentUserName);
                intent.putExtra("toUser", friendName);


                //Now we are starting our service
                getActivity().startService(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("FaultGetting currUser: ", fault.toString());
            }
        });
    }


}

















