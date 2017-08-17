package com.ahsan.a27smanthademo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class RegisterFragment extends Fragment {

    private EditText usernameField;
    private EditText passwordField;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Register Button code
        Button button = (Button) view.findViewById(R.id.registerButton);
        usernameField = (EditText) view.findViewById(R.id.usernameField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();



                //Register a user
                BackendlessUser backendlessUser = new BackendlessUser();
                backendlessUser.setPassword(password);
                backendlessUser.setProperty("name", username);//name is the field in backendless database



                //Register user with Backendless using above details of backendlessUser
                Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>() {
                    //handleResponse is called when the opreation is successful
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(getActivity(), "You registered!", Toast.LENGTH_SHORT).show();
                    }

                    //handleFault is called when the opreation is failed
                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getActivity(), "User not registered.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}























