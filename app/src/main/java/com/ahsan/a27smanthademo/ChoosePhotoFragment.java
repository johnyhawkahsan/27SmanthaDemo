package com.ahsan.a27smanthademo;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoosePhotoFragment extends Fragment {


    //To identify the activity using requestCode in onActivityResult method
    public static final int REQUEST_CHOOSE_PHOTO = 2;


    public ChoosePhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choose_photo, container, false);

        //NOTE: getView() worked to get findViewById==Not using anymore because App crashed
        //ImageView imageView = (ImageView) getView().findViewById(R.id.imageView);

        //Activity Action: Allow the user to select a particular kind of data and return it.
        //An ACTION_GET_CONTENT could allow the user to create the data as it runs
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //Choose all image types
        choosePhotoIntent.setType("image/*");

        //ResolveActivity for PhotoIntent, means it will decide itself which application to use to show image
        if (choosePhotoIntent.resolveActivity(getActivity().getPackageManager()) !=null){
            //startActivity(choosePhotoIntent);
            startActivityForResult(choosePhotoIntent, REQUEST_CHOOSE_PHOTO);
        }


        // Inflate the layout for this fragment
        return view;
    }


    //Function to display image in a popup box
    private void displayPopupImage(Bitmap imageBitmap) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());//Build alert dialog
        alertDialog.setMessage("You selected this image!");//set dialog box message

        ImageView imageView = new ImageView(getActivity());//Set imageView in dialog box
        imageView.setImageBitmap(imageBitmap);

        alertDialog.setView(imageView);
        alertDialog.create();
        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PHOTO){
            if (resultCode == Activity.RESULT_OK){
                //Do something
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    displayPopupImage(bitmap);//Call DisplayPopuImage and pass our bitmap to it

                    //Instead of using Diaglog box to display image, I'm using imageView=Not using anymore because App crashed
                    //imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}











