package com.example.msp.legaldesire;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.msp.legaldesire.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.util.HashMap;


public class Lawyer_User_registration extends Fragment {

    public static String TAG = "lawyermodule123";
    private DatabaseReference mDatabase;
    Button mSubmit;
    EditText mNameField, mEmailField, mAddressField, mRatingField, mLatitudeField, mLongitudeField;
    Spinner mTypeField;
    GoogleMap mGoogleMap;
    MapView mMapView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lawyer__user_registration, container, false);
        //Init all the fields-EditText
        mNameField = (EditText) view.findViewById(R.id.edit_lawyer_name);
        mEmailField = (EditText) view.findViewById(R.id.edit_lawyer_email);
        mAddressField = (EditText) view.findViewById(R.id.edit_lawyer_address);
        mRatingField = (EditText) view.findViewById(R.id.edit_lawyer_rating);
        mLatitudeField = (EditText) view.findViewById(R.id.edit_lawyer_latitude);
        mLongitudeField = (EditText) view.findViewById(R.id.edit_lawyer_longitude);
        mTypeField = (Spinner) view.findViewById(R.id.dialog_lawyer_type);
        mMapView = (MapView) view.findViewById(R.id.map_View);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + "," + latLng.longitude);
                        double lat = latLng.latitude;
                        double lng = latLng.longitude;
                        String slat = String.valueOf(lat);
                        String slng = String.valueOf(lng);
                        mLatitudeField.setText(slat);
                        mLongitudeField.setText(slng);
                        mGoogleMap.clear();
                        mGoogleMap.addMarker(markerOptions);
                    }
                });
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //User has previously accepted this permission
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Not in api-23, no need to prompt
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });


        //Init all the fields-Button

        mSubmit = (Button) view.findViewById(R.id.btn__lawyer_enterdata);

        SharedPreferences preferences = getContext().getSharedPreferences("store_name_and_email", Context.MODE_PRIVATE);
        String personName = preferences.getString("person_name", null);
        final String personEmail = preferences.getString("person_email", null);
        final URI personPhoto = URI.create(preferences.getString("person_photo", null));


        mEmailField.setText(personEmail);
        mEmailField.setEnabled(false);
        mEmailField.setFocusable(false);
        mNameField.setText(personName);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> insertData = new HashMap<String, String>();
                insertData.put("Email", mEmailField.getText().toString().trim());
                insertData.put("Name", mNameField.getText().toString().trim());
                insertData.put("Address", mAddressField.getText().toString().trim());
                insertData.put("Rating", mRatingField.getText().toString().trim());
                insertData.put("Type", mTypeField.getSelectedItem().toString().trim());
                insertData.put("Latitude", mLatitudeField.getText().toString().trim());
                insertData.put("Longitude", mLongitudeField.getText().toString().trim());
                insertData.put("profile_pic", personPhoto.toString());


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
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //
                    //     Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
