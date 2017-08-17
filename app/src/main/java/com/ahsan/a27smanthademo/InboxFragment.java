package com.ahsan.a27smanthademo;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




public class InboxFragment extends Fragment {

    private List<String> fromFriends; //Pictures from friends
    private List<SentPicture> incomingPhotos;//Contains all of the sent picture-So we can figure out files we want to download
    private ArrayAdapter<String> fromFriendsAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        fromFriends = new ArrayList<String>();
        fromFriendsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                fromFriends);
        incomingPhotos = new ArrayList<SentPicture>();//Don't forget to initialize

        ListView friendList = (ListView) view.findViewById(R.id.incomingPhotos);
        friendList.setAdapter(fromFriendsAdapter);
        //fromFriends.add("Test item");

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Call method to download image sent by this very friend
                displayImageFromFriend(position);
                Log.i("Friend clicked:", String.valueOf(position));
            }
        });


        //Get id of currentUser
        String currentUser = Backendless.UserService.loggedInUser();
        Backendless.Persistence.of(BackendlessUser.class).findById(currentUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                String currentUserName = (String) user.getProperty("name");//Get name of current loggedInUser
                Log.i("CurrentUserName: ", currentUserName);

                //Run this method inside main method=It gets currentUserName and displays it's friends as a list
                getPhotosSentTo(currentUserName);

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("FindUserFault:", fault.toString());
            }
        });


        return view;
    }





    //Method to find photos sent to logged in user
    private void getPhotosSentTo(String username){
        Log.i("getPhotosSent:" , "Launching getPhotosSentTo()method");

        DataQueryBuilder query = DataQueryBuilder.create();
        //Query to search for sentPictures table for sent pictures to our current user
        query.setWhereClause(String.format("toUser = '%s'", username));

        //If within SentPicture table, toUser value is our current user, means our current user got image from someone
        Backendless.Persistence.of(SentPicture.class).find(query, new AsyncCallback<List<SentPicture>>() {
            @Override
            public void handleResponse(List<SentPicture> photos) {
                Log.i("whereQuery:" , photos.toString());

                //Iterate through tables
                for (SentPicture photo: photos){
                    //We're calling our getter method isViewed to see if we've looked at our photo already=If we haven't looked !photo
                    //if (!photo.isViewed()) removed
                    //This will add it to our list to display
                    fromFriends.add(photo.getFromUser());   //Call getter method to find who sent the photo and add to list
                    incomingPhotos.add(photo);
                    Log.i("fromFriends:", fromFriends.toString());
                    Log.i("incomingPhotos:", incomingPhotos.toString());


                }
                fromFriendsAdapter.notifyDataSetChanged();

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i("DataQueryFault:", fault.toString());
            }
        });
    }


    //Download image after we click on user name- It gets position of user and display image
    private void displayImageFromFriend(int position){
        Log.i("displayImageFromFriend", "displayImageFromFriend(int position) method running");
        String imageLocation = incomingPhotos.get(position).getImageLocation();
        Log.i("imageLocationClicked", imageLocation);
        try {
            //TODO: Note same error like was in instagram clone app, sentPics was displayed in link twice
            URL url = new URL("https://api.backendless.com/0513C94E-AF3C-B862-FFE6-2822D9E35300/21CCA5B9-1582-AC69-FFB3-6791F17D9E00/files/" + imageLocation);
            DownloadFilesTask task = new DownloadFilesTask();
            Log.i("URL", url.toString());
            task.execute(url);

            Log.i("DownloadFileTask:", task.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }



    //Display popup dialog with downloaded image
    private void displayPopupImage(Bitmap bitmap){
        AlertDialog.Builder imagedialog = new AlertDialog.Builder(getActivity());
        imagedialog.setMessage("Incoming Photo");

        imagedialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User cancelled. Do nothing
                Toast.makeText(getActivity(), "You cancelled!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(bitmap);
        imagedialog.setView(imageView);

        imagedialog.create();
        imagedialog.show();
    }




    //Image download task from Backendless in background
    private class DownloadFilesTask extends AsyncTask<URL, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(URL... params) {
            Log.i("DownloadFileBcg", "DownloadFileTask running Download Bitmap in Background");

            for (URL url : params){
                try {
                    //Setup HttpURLConnection
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK){ //You connected properly
                        Log.i("responseCodeHTTP_OK", "if statement for Connection HTTP_OK");
                        InputStream inputStream = httpURLConnection.getInputStream();
                        Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);//Translate input stream to bitmap
                        inputStream.close();
                        return bitmapImage;

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }



        //We overrided this method to run displayPopupImage function
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                Log.i("onPostExecute", bitmap.toString());
                //imageView.setImageBitmap(bitmap);
                displayPopupImage(bitmap);
            } else {
                Log.i("onPostExecute", "bitmap is null");
            }
        }
    }

}





