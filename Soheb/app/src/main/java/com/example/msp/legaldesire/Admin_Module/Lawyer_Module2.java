package com.example.msp.legaldesire.Admin_Module;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.msp.legaldesire.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Lawyer_Module2 extends Fragment {
    public static final String TAG = "lawyermodule2";
    ListView mListView;
    Lawyer_Module2_Adapter lawyer_module2_adapter;
    private DatabaseReference mDatabase;

    ArrayList<String> key = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<Double> latitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();

    public Lawyer_Module2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Lawyers");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.lawyer__module2, container, false);
        mListView = (ListView) view.findViewById(R.id.list_viewdata);
        lawyer_module2_adapter = new Lawyer_Module2_Adapter(getContext(), name, email, address, rating, type, latitude, longitude);
        mListView.setAdapter(lawyer_module2_adapter);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot postSnapshot, String s) {
                name.add(postSnapshot.child("Name").getValue(String.class));
                email.add(postSnapshot.child("Email").getValue(String.class));
                address.add(postSnapshot.child("Address").getValue(String.class));
                rating.add(Double.parseDouble(postSnapshot.child("Rating").getValue(String.class)));
                type.add(postSnapshot.child("Type").getValue(String.class));
                latitude.add(Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class)));
                longitude.add(Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class)));
                key.add(postSnapshot.getKey());
                lawyer_module2_adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String mKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    /*    mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    name.add(postSnapshot.child("Name").getValue(String.class));
                    email.add(postSnapshot.child("Name").getValue(String.class));
                    address.add(postSnapshot.child("Address").getValue(String.class));
                    rating.add(Double.parseDouble(postSnapshot.child("Rating").getValue(String.class)));
                    type.add(postSnapshot.child("Type").getValue(String.class));
                    latitude.add(Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class)));
                    longitude.add(Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class)));
                    lawyer_module2_adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        return view;
    }

}
