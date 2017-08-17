package com.ahsan.a27smanthademo;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;

import static com.ahsan.a27smanthademo.ChoosePhotoFragment.REQUEST_CHOOSE_PHOTO;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoggedinFragment extends Fragment {


    private ArrayList<String> menuItems;
    private ArrayAdapter<String> menuItemsAdapter;

    public LoggedinFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loggedin, container, false);



        menuItems = new ArrayList<String>();

        menuItems.add("Friends List");
        menuItems.add("Logout");
        menuItems.add("Add Friend");
        menuItems.add("Friend Requests");
        menuItems.add("Send Picture");
        menuItems.add("Incoming Photos");

        menuItemsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItems
        );

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(menuItemsAdapter);

        //When someone clicks on item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    Toast.makeText(getActivity(),"Display friend list", Toast.LENGTH_SHORT).show();
                    //Now we need to open new activity when a person clicks here
                    Intent intent = new Intent(getActivity(), FriendlistActivity.class);
                    startActivity(intent);
                } else if (position == 1){
                    Toast.makeText(getActivity(),"You're logging out...", Toast.LENGTH_LONG).show();

                    //Run LOGOUT code
                    Backendless.UserService.logout(new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                            Toast.makeText(getActivity(), "You logged out!",Toast.LENGTH_SHORT).show();
                            //Start Main activity once a person is logged out
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getActivity(), "Failed to Logout :( ",Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (position == 2){
                    Toast.makeText(getActivity(), "Launching Add Friend Activity!",Toast.LENGTH_SHORT).show();
                    //Start Main activity once a person is logged out
                    Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                    startActivity(intent);

                } else if (position == 3){
                    Toast.makeText(getActivity(), "Launching Friend Requests Activity!",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), FriendRequestsActivity.class);
                    startActivity(intent);

                } else if (position == 4){
                    Toast.makeText(getActivity(), "Launching Send Picture Method from LoggedInFragment=Going to Gallery!",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.setType("image/*");//All image types
                    intent.setAction(Intent.ACTION_GET_CONTENT);//Go to gallery
                    startActivityForResult(Intent.createChooser(intent, "Select Picture!"),
                            REQUEST_CHOOSE_PHOTO);
                } else if (position == 5){
                    Toast.makeText(getActivity(), "Launching InboxActivity=IncomingPhotos",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), InboxActivity.class);
                    startActivity(intent);
                }

            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_PHOTO){
            if (resultCode == Activity.RESULT_OK){
                Uri uri = data.getData();
                //Intent to launch new activity, we are launching FriendsList because once we
                //select a photo, we want to send it to our friend
                Intent intent = new Intent(getActivity(), FriendlistActivity.class);
                intent.putExtra("ImageURI", uri);//Send uri of chosen image
                startActivity(intent);

            }
        }
    }
}












