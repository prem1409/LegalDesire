package com.example.msp.legaldesire;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.msp.legaldesire.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Regular_User_Registration extends Fragment {
    EditText mEmail, mName, mContactNo;
    Button mSubmit;
    DatabaseReference mDatabase;

    public Regular_User_Registration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_regular__user__registration, container, false);
        mEmail = (EditText) view.findViewById(R.id.edit_regular_email);
        mName = (EditText) view.findViewById(R.id.edit_regular_name);
        mContactNo = (EditText) view.findViewById(R.id.edit_regular_contact_no);
        mSubmit = (Button) view.findViewById(R.id.btn_regular_submit);


        SharedPreferences preferences = getContext().getSharedPreferences("store_name_and_email", Context.MODE_PRIVATE);
        String personName = preferences.getString("person_name", null);
        final String personEmail = preferences.getString("person_email", null);
//        final URI personPhoto = URI.create(preferences.getString("person_photo",null));

        mEmail.setText(personEmail);
        mEmail.setEnabled(false);
        mEmail.setFocusable(false);
        mName.setText(personName);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mName.getText().toString().trim().length() == 0 || mContactNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "One of the field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
                    HashMap<String, String> insertData = new HashMap<String, String>();
                    insertData.put("Email", mEmail.getText().toString().trim());
                    insertData.put("Name", mName.getText().toString().trim());
                    insertData.put("ContactNo", mContactNo.getText().toString().trim());
                //    insertData.put("profile_pic",personPhoto.toString());

                    mDatabase.push().setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));

                            } else {
                                Toast.makeText(getContext(), "Failed to Register", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }

            }
        });

        return view;
    }

    public void convertImage(){

    }

}
