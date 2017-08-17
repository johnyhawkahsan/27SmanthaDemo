package com.ahsan.a27smanthademo;


import android.content.Intent;
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
public class LoginFragment extends Fragment {

    private EditText usernameField;
    private EditText passwordField;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameField = (EditText) view.findViewById(R.id.usernameField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);

        Button button = (Button) view.findViewById(R.id.loginButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"You clicked login!", Toast.LENGTH_SHORT ).show();
                //Login user

                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                Toast.makeText(getActivity(), "Trying to Log you in.....",Toast.LENGTH_SHORT).show();

                //AsyncCallBack tries to login user Asycynchronously in the background
                Backendless.UserService.login(username, password, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(getActivity(), "You Logged in", Toast.LENGTH_SHORT).show();
                        //If user successfully logs in, it will relaunch main activity
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getActivity(), "Error logging in", Toast.LENGTH_SHORT).show();
                    }
                },
                true);//NOTE: NOTE: This true lets the user keep logged in
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
