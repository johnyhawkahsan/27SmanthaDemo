package com.ahsan.a27smanthademo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;


public class FriendRequestsFragment extends Fragment {

    private ArrayList<String> fromUsers;
    private ArrayList<FriendRequest> friendRequests;
    private ArrayAdapter<String> friendRequestsAdapter;


    public FriendRequestsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        fromUsers = new ArrayList<String>();
        friendRequests = new ArrayList<FriendRequest>();
        friendRequestsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                fromUsers
        );

        //FriendRequests list
        ListView friendList = (ListView) view.findViewById(R.id.friendRequestsList);
        friendList.setAdapter(friendRequestsAdapter);
        //fromUsers.add("Incoming Friend Requests:");//This was throwing out of bound exception error

        //To be able to accept friend requests
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAcceptDialog(position);
            }
        });



        //Get name of logged in user
        String userId = Backendless.UserService.loggedInUser();//Get id of loggedInUser
        Backendless.UserService.findById(userId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                //Get name of the current logged in user
                String currentUserName = response.getProperty("name").toString();

                //Query FriendRequest table for requests to logged in user
                getIncomingFriendRequests(currentUserName);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("FindCurrUserFault:", fault.toString());
            }
        });


        return view;
    }





    //Method for when someone clicks on friend request to show dialog to accept friend request
    private void showAcceptDialog(final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Accept Friend Request from " + fromUsers.get(position) + "?");

        dialog.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptRequest(friendRequests.get(position));
            }
        });

        dialog.create();
        dialog.show();
    }






    //To go in YES button in Dialoge-This function will save the data to backendless when we click yes
    private void acceptRequest(final FriendRequest request){    //FriendRequest is an ArrayList
        Toast.makeText(getActivity(), "Accept request!", Toast.LENGTH_SHORT).show();
        //Set status of table "accepted" to true using setter
        request.setAccepted(true);
        Backendless.Persistence.save(request, new AsyncCallback<FriendRequest>() {
            @Override
            public void handleResponse(FriendRequest response) {
                //Start our old AddFriendService
                Intent intent = new Intent(getActivity(), AddFriendService.class);
                intent.setAction(Constants.ACTION_ADD_FRIEND);
                intent.putExtra("firstUserName",request.getFromUser());
                intent.putExtra("secondUserName", request.getToUser());
                getActivity().startService(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("FaultAcceptRequest:", fault.toString());
            }
        });
    }



    //Requets received by LoggedInUser present in FriendRequests Table
    private void getIncomingFriendRequests(String username){
        DataQueryBuilder query = DataQueryBuilder.create();
        //Query to search for FriendRequests table "toUser", if to user is the current user, get all the names "fromUser"
        query.setWhereClause(String.format("toUser = '%s'", username));

        Backendless.Persistence.of(FriendRequest.class).find(query, new AsyncCallback<List<FriendRequest>>() {
            @Override
            public void handleResponse(List<FriendRequest> response) {
                //For Each loop to iterate through our list
                for (FriendRequest request: response){
                    //If request status is not accepted=! not request
                    if (!request.isAccepted()){
                        fromUsers.add(request.getFromUser());//add names of FromUsers calling getter method to fromUsers list
                        friendRequests.add(request);//List of Friend Requests
                    }
                }
                friendRequestsAdapter.notifyDataSetChanged();//Update Adapter
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("FaultGetIncomgRequests:", fault.toString());
            }
        });

    }

}




