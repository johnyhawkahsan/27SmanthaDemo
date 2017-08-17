package com.ahsan.a27smanthademo;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    //Required by onActivityResult==We need it to know which activity we are getting back from
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    //Store imageFilePath in this variable so we can use later on
    private String imageFilePath;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        //Initialize imageFile to null
        File imageFile = null;

        try {
            //Create imaegFile using our function
            imageFile = createImageFile();
            //Storing path of file, by extracting from image file
            imageFilePath = imageFile.getAbsolutePath();

        } catch (IOException e){
            e.printStackTrace();
        }

        if (imageFile != null){
            //Implicit intent to use camera of mobile
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Put extra details about our imageFile
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));

            //Now resolveActivity function resolves and checks the appropriate app to capture image
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                //Start activity takePictureIntent in this if statement
                //startActivity(takePictureIntent);

                //Now instead of using above code, we use this one to know where it was called from
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }




        // Inflate the layout for this fragment
        return view;
    }



    //Create a method to save the captured camera image
    private File createImageFile() throws IOException {

        //Creating new timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(fileName, ".jpg", storageDir);

        return image;
    }


    //Creating method to add our captured image to gallery-By default it wasn't available in gallery
    private void addPhotoToGallery(String filePath){

        //Means make this available to gallery
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);//Uri is like URL, a way to locate a file
        mediaScanIntent.setData(uri);

        //We are broadcasting our photo to gallery
        getActivity().sendBroadcast(mediaScanIntent);

    }



    //To make this file accessible from Photo app or gallery app
    //Fired whenever we come back from taking picture activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK){
                //Toast.makeText(getActivity(), "You took a picture!", Toast.LENGTH_SHORT).show();
                addPhotoToGallery(imageFilePath);//This code makes file avalailble to photo gallery
            } else if (resultCode == Activity.RESULT_CANCELED){
                //Toast.makeText(getActivity(), "You cancelled :( !", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
















