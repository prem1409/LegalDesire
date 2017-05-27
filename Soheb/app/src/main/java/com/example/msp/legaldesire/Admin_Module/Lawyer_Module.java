package com.example.msp.legaldesire.Admin_Module;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msp.legaldesire.R;
import com.example.msp.legaldesire.SearchLawyer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Lawyer_Module extends Fragment {
    public static String TAG = "lawyermodule123";
    private DatabaseReference mDatabase;
    Button mEditData, mViewData;
    EditText mNameField, mEmailField, mAddressField, mRatingField, mLatitudeField, mLongitudeField;
    Spinner mTypeField;
    ListView mUserList;
    boolean isValidate;
    ArrayList<String> userData = new ArrayList<>();
    ArrayList<String> mKeys = new ArrayList<>();

    public Lawyer_Module() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Lawyers");
        // mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.lawyer__module, container, false);
        //Init List and set it's adapter
        mUserList = (ListView) view.findViewById(R.id.user_list);
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, userData);
        mUserList.setAdapter(myAdapter);
        mUserList.setItemsCanFocus(true);
        //Init all the fields-EditText
        mNameField = (EditText) view.findViewById(R.id.edit_namefield);
        mEmailField = (EditText) view.findViewById(R.id.edit_emailfield);
        mAddressField = (EditText) view.findViewById(R.id.edit_addressfield);
        mRatingField = (EditText) view.findViewById(R.id.edit_ratingfield);
        mLatitudeField = (EditText) view.findViewById(R.id.edit_latitudefield);
        mLongitudeField = (EditText) view.findViewById(R.id.edit_longitudefield);
        mTypeField = (Spinner) view.findViewById(R.id.dialog_type);
        //Init all the fields-Button
        mEditData = (Button) view.findViewById(R.id.btn_enterdata);
        mViewData = (Button) view.findViewById(R.id.btn_viewdata);

        mEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidate = true;
                hideKeyboard();
                Log.d(TAG, "insert data called");
                if (mNameField.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "Name cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "name is empty");
                    isValidate = false;
                }
                if (mEmailField.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "email cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Email is empty");
                    isValidate = false;
                }
                if (mAddressField.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "address cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "address is empty");
                    isValidate = false;
                }
                if (mRatingField.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "rating cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "rating is empty");
                    isValidate = false;
                }
                if (mLatitudeField.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "latitude cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "latitude is empty");
                    isValidate = false;
                }
                if (mLongitudeField.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "longitude cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "longitude is empty");
                    isValidate = false;
                }

                if (isValidate) {
                    String name = mNameField.getText().toString().trim();
                    String email = mEmailField.getText().toString().trim();
                    String address = mAddressField.getText().toString().trim();
                    String rating = mRatingField.getText().toString().trim();
                    String type = mTypeField.getSelectedItem().toString().trim();
                    String lat = mLatitudeField.getText().toString().trim();
                    String lng = mLongitudeField.getText().toString().trim();
                    //  Double lng = Double.parseDouble(String.valueOf(mLongitudeField.getText()));

                    HashMap<String, String> insertData = new HashMap<String, String>();
                    insertData.put("Name", name);
                    insertData.put("Email", email);
                    insertData.put("Address", address);
                    insertData.put("Rating", rating);
                    insertData.put("Type", type);
                    insertData.put("Latitude", lat);
                    insertData.put("Longitude", lng);

                    mDatabase.push().setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Data Inserted", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getContext(), "Failed to insert data", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

       /* mViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String nname = postSnapshot.child("Name").getValue(String.class);
                            String nemail = postSnapshot.child("Email").getValue(String.class);
                            String naddress = postSnapshot.child("Address").getValue(String.class);
                            String nrating = postSnapshot.child("Rating").getValue(String.class);
                           // double x = Double.parseDouble(nrating);
                            String ntype = postSnapshot.child("Type").getValue(String.class);
                            String nlat = postSnapshot.child("Latitude").getValue(String.class);
                            String nlng = postSnapshot.child("Longitude").getValue(String.class);
                            // Log.e(TAG, "======="+postSnapshot.child("Name").getValue());
                            Log.e(TAG, "======= name:" + nname);
                            Log.e(TAG, "======= email:" + nemail);
                            Log.e(TAG, "======= address:" + naddress);
                            Log.e(TAG, "======= rating:" + nrating + " ");
                            Log.e(TAG, "======= type:" + ntype);
                            Log.e(TAG, "======= latitude:" + nlat);
                            Log.e(TAG, "======= longitude:" + nlng);
                            Log.e(TAG, "===================================");

                        }
                        //   String acctname = (String)dataSnapshot.child("Name").getValue();
                        //  String acctmail = (String)dataSnapshot.child("Email").getValue();
                        //   Log.d(TAG,"name=" + acctname);
                        //    Log.d(TAG,"acctmail=" + acctmail);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });*/
        mViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Lawyer_Module2()).commit();

            }
        });




     /*   mViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "View data clicked");
                mDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Lawyer_Fields lawyer_fields =  dataSnapshot.getValue(Lawyer_Fields.class);
                        String nname = lawyer_fields.lname;
                        String nemail = lawyer_fields.lemail;
                        String naddress = lawyer_fields.laddress;
                        String nrating = lawyer_fields.lrating;
                        String nlat = lawyer_fields.llatitude;
                        String nlng = lawyer_fields.llongitude;*/




                   /*     for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Lawyer_Fields lawyer_fields =  postSnapshot.getValue(Lawyer_Fields.class);
                            String nname = lawyer_fields.lname;
                            String nemail = lawyer_fields.lemail;
                            String naddress = lawyer_fields.laddress;
                            String nrating = lawyer_fields.lrating;
                            String nlat = lawyer_fields.llatitude;
                            String nlng = lawyer_fields.llongitude;
                            Log.e(TAG, "=======" + postSnapshot.toString() + nname);
                            Log.e(TAG, "=======" + postSnapshot.toString() + nemail);
                            Log.e(TAG, "=======" + postSnapshot.toString() + naddress);
                            Log.e(TAG, "=======" + postSnapshot.toString() + nrating);
                            //  Log.e(TAG, "======="+postSnapshot.child("Name").getValue());
                        }*/
        //  String value = dataSnapshot.getValue(String.class);
        //   String key = dataSnapshot.getKey();
        //  userData.add(value);
        //   mKeys.add(key);
        //   myAdapter.notifyDataSetChanged();
            /*        }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        String value = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();
                        int index = mKeys.indexOf(key);
                        userData.set(index, value);
                        myAdapter.notifyDataSetChanged();
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

            }
        });*/
        return view;
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
