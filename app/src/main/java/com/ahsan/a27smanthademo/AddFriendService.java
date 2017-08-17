package com.ahsan.a27smanthademo;


import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.DataQueryBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


//NOTE: We deleted most of the code for it, but INTENT Service is a service that we can start with an intent.
//We are getting intent from AddFriendFragment.java method addFriend() method
public class AddFriendService extends IntentService {



    public AddFriendService() {
        super("AddFriendService");
    }



    //This is INTENT SERVICE, specifically made to receive intents
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //We sent action from AddFriendFragment.java addFriend function
            String action = intent.getAction();
            Log.i("AddFriendService", "Value of intent action is " + action );

            if (action.equals(Constants.ACTION_ADD_FRIEND)){
                //Get firstUserName from AddFriendFragment.java function addFriend
                String firstUserName = intent.getStringExtra("firstUserName");//Current user
                String secondUserName = intent.getStringExtra("secondUserName");

                //To test if we are getting users from AddFriendFragment to AddFriendService properly
                Log.i("AddFriendService", "We are adding friend.First user: " + firstUserName + ".Second user: " + secondUserName);

/*
                //TODO: TO test setWhereClause=NOTE: Not working
                DataQueryBuilder query = DataQueryBuilder.create();
                query.setWhereClause("name = testuser");
                Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<List<BackendlessUser>>() {
                    @Override
                    public void handleResponse(List<BackendlessUser> response) {
                        Log.i("Response of Where: " , response.toString());
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.i("BackendlessFault: " , fault.toString());
                    }
                });

*/

/*
                //TODO: TO test setWhereClause from Offcial Documentation=NOTE: Not working
                IDataStore<BackendlessUser> users = Backendless.Persistence.of(BackendlessUser.class);

                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause( "username = testuser" );
                users.getObjectCount( queryBuilder, new AsyncCallback<Integer>()
                {
                    @Override
                    public void handleResponse( Integer objectCount )
                    {
                        Log.i( "MYAPP", "There are " + objectCount + " objects matching the query" );
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        Log.e( "MYAPP", "Server reported an error - " + fault.getMessage() );
                    }
                } );
*/
                //Run the adFriends method
                //TODO: Change it later=
                addFriends(firstUserName, secondUserName);

            } else if (action.equals(Constants.ACTION_SEND_FRIEND_REQUEST)){
                String fromUser = intent.getStringExtra("fromUser");//Current user
                String toUser = intent.getStringExtra("toUser");
                Log.i("AddFriendService", "Send Friend Request To " + toUser + " from " + fromUser);
                sendFriendRequest(fromUser, toUser);

            } else if (action.equals(Constants.ACTION_SEND_PHOTO)){
                //All this intent data is coming from FriendListFragment's function sendImageToFriend()
                Toast.makeText(getApplicationContext(),"ActionSendPhoto",Toast.LENGTH_SHORT).show();
                String toUser = intent.getStringExtra("toUser");
                String fromUser = intent.getStringExtra("fromUser");
                Uri imageURI = intent.getParcelableExtra("imageURI");

                Log.i("getExtrasInService", "imageURI:" + imageURI.toString() + ", toUser:" + toUser + ", fromUser" + fromUser);

                //Run sendPhoto()
                sendPhoto(fromUser, toUser, imageURI);

            }

        }
    }








    //This method will save the photo to BackendLess
    private void sendPhoto(String fromUser, String toUser, Uri imageUri){

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);//Bitmap representation
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timestamp + "_.jpg";
            String imageDirectory = "sentPics";//Image directory name in Backendless Files


            //Created class SentPicture.java=Now we create sent picture representation of this file
            final SentPicture sentPicture = new SentPicture();
            sentPicture.setToUser(toUser);
            sentPicture.setFromUser(fromUser);
            //File path in Backendless
            sentPicture.setImageLocation(imageDirectory + "/" + imageFileName);

            //Upload this file to Backendless
            Backendless.Files.Android.upload(
                    bitmap,
                    Bitmap.CompressFormat.JPEG,
                    100,
                    imageFileName,
                    imageDirectory,
                    new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {

                            Log.i("sendPhoto", "Photo saved to Backendless");

                            //This will create a new table called sentPicture with the details of toUser,fromUser,viewed and imageLocation
                            Backendless.Persistence.save(sentPicture, new AsyncCallback<SentPicture>() {
                                @Override
                                public void handleResponse(SentPicture response) {
                                    Log.i("sentPictureSuccess:", sentPicture.toString());
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.i("TableCreateFault", fault.toString());
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.i("FileUploadFault", fault.toString());
                        }
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }








    //Send Friend Request Method
    private void sendFriendRequest(final String fromUser, final String toUser){

        //Make sure toUser exists
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setWhereClause(String.format("name = '%s'", toUser));

        Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> response) {
                Log.i("Response o/p: ", response.toString());
                if (response.size() ==0){
                    //Broadcast Failure
                    broadcastFriendRequestFailure();
                } else {
                    //Create a friend Request-----Call our method
                    FriendRequest friendRequest = new FriendRequest();
                    //Using setter methods, set toUser, fromUser and Accepted
                    friendRequest.setToUser(toUser);
                    friendRequest.setFromUser(fromUser);
                    friendRequest.setAccepted(false);

                    //Save friend request to Backendless
                    //What's interesting is that FriendRequest table doesn't exist yet, but once this command executes
                    //The table FriendRequests is created
                    Backendless.Persistence.save(friendRequest, new AsyncCallback<FriendRequest>() {
                        @Override
                        public void handleResponse(FriendRequest response) {
                            Toast.makeText(getApplicationContext(), "Table Created=Friend Request", Toast.LENGTH_SHORT).show();
                            //Broadcast Success
                            broadcastFriendRequestSuccess();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            //Broadcast Failure
                            broadcastFriendRequestFailure();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //Broadcast Failure
                broadcastFriendRequestFailure();
            }
        });



    }
















    //Method to add 2 friends, 1 that is adding user and second which is being added
    private void addFriends (String firstUserName, String secondUserName){
        //Find both users-Return all users where the name equals firstUserName and name equals secondUserName

        Log.i("addFriends()", "addFriendsMethod is Running in AddFriendService.java");

        //TODO: NOTE: BackendlessDataQuery has been replaced with DataQueryBuilder-Check out documentation
        //Here's my question on StackOverflow
        //https://stackoverflow.com/questions/44492945/backendless-collection-not-availabe-and-cannot-be-accessed/44495674#44495674
        //https://backendless.com/docs/android/doc.html
        //Use this instead of
        //BackendlessDataQuery query = new BackendlessDataQuery();
        DataQueryBuilder query = DataQueryBuilder.create();

        //%s is format symbol for STRING-Therefore we are using String.format() method
        query.setWhereClause(String.format("name = '%s' or name = '%s'", firstUserName, secondUserName));
        //query.setWhereClause("name = testuser");

        Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> response) {

                Log.i("2UsersQueryResponse" , response.toString());
                Log.i("QResponse.size()", String.valueOf(response.size()));


                //TODO: Working
                //Note response.getData() because response is already a List in Backendless 4

                final BackendlessUser user1 = response.get(0);//Second user we want to add
                //BackendlessUser user1 = response.get(0).setProperty("friends",);//Just to show this is also a possibility
                final BackendlessUser user2 = response.get(1);//Active user

                Log.i("user1" , user1.toString());//TODO: note this is the friend we're adding-FUNNY
                Log.i("user2" , user2.toString());//This is the logged in user


                //If more than 2 users are showed or less than 2 users are showed, then show error message
                if (response.size() !=2){

                    Log.i("ifResoponse!=2: ", "ERROR:The returned users are not equal to 2.Response code is: "+response.toString());
                    broadcastAddFriendFailure();
                    //That means we have more that one users or one of users wasn't found

                    //If else is executed, that means response size = 2 which is exactly what we're looking for, i.e 2 users
                } else {

                    Log.i("Else Response ", "Search Successful!! 2 Users are returned during search.");


                    ArrayList<BackendlessUser> allFriends = new ArrayList<BackendlessUser>();
                    allFriends.add(user1);//Add friend we want to add to this ArrayList

                    //TODO: I was using setRelation method, the problem was it replaced old items every time, so I emailed Mark Pillar and he said I should use addRelation instead.
                    //friends is column name and Users is the table and n means 1:n (1 to many)
                    //What it did was added a parent to my logged in user "alice", when I though it would add "alice" as a parent to the other one
                    Backendless.Persistence.of(BackendlessUser.class).addRelation(user2, "friends:Users:n", allFriends, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            Log.i("addRelationUser2", "Relation has been set successfully, response: " + response);
                            broadcastAddFriendSuccess();

                            //Opposite to the above query, this below query will add logged in user as a parent to the other user we're trying to add
                            ArrayList<BackendlessUser> newFriend2 = new ArrayList<BackendlessUser>();
                            newFriend2.add(user2);
                            Backendless.Persistence.of(BackendlessUser.class).addRelation(user1, "friends:Users:n", newFriend2, new AsyncCallback<Integer>() {
                                @Override
                                public void handleResponse(Integer response) {
                                    Log.i( "addRelationUser1", "Relation has been set successfully, response: " + response );
                                    broadcastAddFriendSuccess();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e( "addRelationUser1", "server reported an error - " + fault.getMessage() );
                                    broadcastAddFriendFailure();
                                }
                            });


                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e("addRelationUser2", "server reported an error - " + fault.getMessage());
                            broadcastAddFriendFailure();
                        }
                    });



                }









//TODO: Below old code did not work, I think because of old Backendless, I used addRelation/setRelation method above and it worked fine.
/*
                //If more than 2 users are showed or less than 2 users are showed, then show error message
                if (response.size() !=2){

                    Log.i("ifResoponse!=2: ", response.toString());
                    broadcastAddFriendFailure();
                    //That means we have more that one users or one of users wasn't found

                //If else is executed, that means response size = 2 which is exactly what we're looking for, i.e 2 users
                } else {

                    Log.i("ElseResoponse!=2: ", response.toString() );

                    BackendlessUser user1 = response.get(0);//First user-It works-Tested Via Logs
                    //TestingOutput
                    Log.i("user1", user1.toString());
                    final BackendlessUser user2 = response.get(1);//Second user-It works-Tested Via Logs
                    //TestingOutput
                    Log.i("user2", user2.toString());

                    //Update first user, adding second user as a friend.
                    //updateFriendsList(user1,user2);

                    Backendless.UserService.update(user1, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser user) {
                            //Successfully updated user1
                            Log.i("UpdatedUser1", user.toString());
                            //Now Update second user adding first user as a friend
                            updateFriendsList(user2, user);//user is the updated version of user1

                            Backendless.UserService.update(user2, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser response) {
                                    broadcastAddFriendSuccess();
                                    Log.i("UpdatedUser2", response.toString());
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    broadcastAddFriendFailure();
                                    Log.i("FailUpdateUser2", fault.toString());
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            broadcastAddFriendFailure();
                            Log.i("FailUpdateUser1", fault.toString());
                        }
                    });
                }
*/
            }



            @Override
            public void handleFault(BackendlessFault fault) {
                broadcastAddFriendFailure();
            }
        });


    }




    //Method to update Friend list-Note: Not in use because I'm using simple Backendless 4 method addRelation
    private void updateFriendsList(BackendlessUser user, BackendlessUser friend){
        Toast.makeText(this,"Launching updateFriendsList method in AddFriendService", Toast.LENGTH_SHORT).show();
        //TestingOutput
        Log.i("updateFriendsList", "UpdateFriendsList Method Running");

        //newFriends is the type of BackendUser array
        BackendlessUser[] newFriends;

        //Get current friends of the user=This is getting id's of current friends
        Object[] currentFriendObjects = (Object[]) user.getProperty("friends");
        //TestingOutput
        Log.i("CurrentFriendObjects", currentFriendObjects.toString());

        //If there are some friends present in the list
        if (currentFriendObjects.length > 0){
            Log.i("currentFriendObjects", "If=Length > 0 -");

            //We are casting currentFriendsObjects from generic objects to the array of BackendlessUsers
            BackendlessUser[] currentFriends = (BackendlessUser[]) currentFriendObjects;

            //Initialize newFriends array to the length of current friends length + 1, because we're adding 1 new friend
            newFriends = new BackendlessUser[currentFriends.length + 1];

            Log.i("currentFriends.length", String.valueOf(currentFriends.length));

            for (int i = 0; i < currentFriends.length; i++){
                //This will copy each friend in currentFriends into new friends
                newFriends[i] = currentFriends[i];
                //TestingOutput
                Log.i("newFriends", newFriends.toString());
            }
            //Now copy our new Friend to newFriends array
            newFriends[newFriends.length - 1] = friend;

        } else {
            Log.i("currentFriendObjects", "Else=Length !>0 -");
            newFriends = new BackendlessUser[]{
                    friend
            };
        }

        //Set friends property with newFriends
        user.setProperty("friends", newFriends);


    }









    //Method to send BROADCAST
    private void broadcastAddFriendSuccess(){
        Intent intent = new Intent(Constants.BROADCAST_ADD_FRIEND_SUCCESS);
        sendBroadcast(intent);
    }

    private void broadcastAddFriendFailure(){
        Intent intent = new Intent(Constants.BROADCAST_ADD_FRIEND_FAILURE);
        sendBroadcast(intent);
    }

    private void broadcastFriendRequestSuccess(){
        Intent intent = new Intent(Constants.BROADCAST_FRIEND_REQUEST_SUCCESS);
        sendBroadcast(intent);
    }

    private void broadcastFriendRequestFailure(){
        Intent intent = new Intent(Constants.BROADCAST_FRIEND_REQUEST_FAILURE);
        sendBroadcast(intent);
    }



}














































































































































