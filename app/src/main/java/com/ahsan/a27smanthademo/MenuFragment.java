package com.ahsan.a27smanthademo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {


    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //loginMenuItems is a String Array with 3 items=We could use Array List here which would've been better
        String[] loginMenuItems = {"Register",
                "Log in"
        };

        //Setting ListView in fragment_menu==Note: We used view.findViewById because its fragment/Or we could use getView().
        ListView listView = (ListView) view.findViewById(R.id.mainMenu);

        //Defining ArrayAdapter for loginMenuItems
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                loginMenuItems
        );

        //Setting this adapter to our
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    Toast.makeText(getActivity(),"Pos=0=You will be redirected to RegisterFragment", Toast.LENGTH_LONG).show();
                    //Now we need to open new activity when a person clicks here
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(intent);
                } else if (position == 1){
                    Toast.makeText(getActivity(),"Pos=1=You will be redirected to LoginFragment", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                }

            }
        });

        // Inflate the layout for this fragment
        return view;


    }

}



