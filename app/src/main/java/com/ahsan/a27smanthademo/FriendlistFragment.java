package com.ahsan.a27smanthademo;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendlistFragment extends Fragment {

    private ArrayList<String> friends;//Going to be list of Frinds
    private ArrayAdapter<String> friendListAdapter;//Going to be list adapter


    public FriendlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friendlist, container, false);

        //Get intent extra data called ImageURI of selected Image from LoggedInFragment.java
        final Uri imageToSend = getActivity().getIntent().getParcelableExtra("ImageURI");



        //Create friends Array list to hold list of Friends
        friends = new ArrayList<String>();
        friendListAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                friends
        );

        //Catching our friendList list view here in friendList variable
        final ListView friendList = (ListView) view.findViewById(R.id.friendList);
        friendList.setAdapter(friendListAdapter);

        //Get the id of loggedInUser
        final String currentUserId = Backendless.UserService.loggedInUser();
        Log.i("CurrLoggedIn UserID", currentUserId);




        //Find user according to it's id
        Backendless.Persistence.of(BackendlessUser.class).findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                //Test the what name is displayed. Just user will output alot of info
                Log.i("CurrentUserData user", user.toString());
                Log.i("CurrentUserName", user.getProperty("name").toString());

                //Get data in relations table "friends"
                Object[] friendObjects = (Object[]) user.getProperty("friends");
                Log.i("friendObjects", friendObjects.toString());

                //If it does have friends
                if (friendObjects.length > 0){
                    BackendlessUser[] friendArray = (BackendlessUser[]) friendObjects;
                    for (BackendlessUser friend: friendArray){
                        String name = friend.getProperty("name").toString();//Save friend name in string name
                        Log.i("FriendNames ForLoop", name);
                        friends.add(name);//Add name to friends ArrayList
                        Toast.makeText(getContext(), name + " added to friends ArrayList", Toast.LENGTH_SHORT).show();
                    }friendListAdapter.notifyDataSetChanged();

                }







                //When someone clicks on friendlist items, send picture to selected friend and launch sendImageToFriend() method
                final String currentUserName = (String) user.getProperty("name");
                friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String friendName = (String) parent.getItemAtPosition(position);//Get name of item at postion
                        Log.i("SendImageToFriend", "Send image using sendImageToFriend() method, to selected friend:" + friendName);
                        sendImageToFriend(currentUserName, friendName, imageToSend);

                    }
                });

            }



            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getContext(), "No friends found", Toast.LENGTH_LONG).show();
                Log.i("Fault: ", fault.toString());
            }
        } );


        // Inflate the layout for this fragment
        return view;
    }







    //Send this intent with all the information the AddFriendService needs in order to send the photo
    private void sendImageToFriend(String currentUser, String toUser, Uri imageURI){
        //Start service to send the picture from currentUser to toUser.
        Intent intent = new Intent(getActivity(), AddFriendService.class);
        intent.setAction(Constants.ACTION_SEND_PHOTO);
        intent.putExtra("fromUser", currentUser);
        intent.putExtra("toUser", toUser);
        intent.putExtra("imageURI", imageURI);
        getActivity().startService(intent);

    }

}










































