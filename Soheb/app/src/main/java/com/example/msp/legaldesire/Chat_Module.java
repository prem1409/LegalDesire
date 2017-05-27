package com.example.msp.legaldesire;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Module extends Fragment {

    public static final String TAG = "chatmodul123";

    private Button add_room;
    private EditText room_name;
    private ListView listview;
    private boolean mIfChatExist = false;
    //  private ArrayAdapter<String> arrayAdapter;
    private Chat_List_Adapter arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private boolean isLawyer = false;
    private String name, email;
    Set<String> set = new HashSet<String>();
    ArrayList<String> storeKeys = new ArrayList<>();
    ArrayList<String> mName = new ArrayList<>();
    ArrayList<Boolean> mNewMessage = new ArrayList<>();
    ArrayList<Date> mLastActive = new ArrayList<>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");

    String mLawyerEmail, mUserEmail;

    Bundle bundle;

    public Chat_Module() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();
        if (bundle != null) {
            mIfChatExist = bundle.getBoolean("chat_exist");
            if (mIfChatExist) {
                Log.d(TAG, "CHAT EXISTS");
            } else {
                Log.d(TAG, "CHAT DOESN'T EXIST");
            }
            mLawyerEmail = bundle.getString("lawyer_email");
            mUserEmail = bundle.getString("user_email");
            Log.d(TAG, "lawyer:" + mLawyerEmail + "," + "User:" + mUserEmail);
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lawyer_email = postSnapshot.child("Lawyer Email").getValue(String.class);
                        String user_email = postSnapshot.child("User Email").getValue(String.class);
                        Log.d(TAG, "Lawyer name:" + lawyer_email);
                        Log.d(TAG, "User name:" + user_email);
                        Log.d(TAG, email);
                        if (mLawyerEmail.equals(lawyer_email) && mUserEmail.equals(user_email)) {
                            Log.d(TAG, "Checking for emails");
                            String key = postSnapshot.getKey();
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("key", key);
                            Log.d(TAG, "key:" + key);
                            // chat_module.setArguments(bundle);
                            Chat_Room chat_room = new Chat_Room();
                            chat_room.setArguments(bundle2);
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, chat_room).commit();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat__module, container, false);
        SharedPreferences preferences = getContext().getSharedPreferences("store_name_and_email", Context.MODE_PRIVATE);
        name = preferences.getString("person_name", null);
        email = preferences.getString("person_email", null);

        listview = (ListView) view.findViewById(R.id.chats);

        //  arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_rooms);
        arrayAdapter = new Chat_List_Adapter(getContext(), mName, mNewMessage);
        listview.setAdapter(arrayAdapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String lawyer_email = postSnapshot.child("Lawyer Email").getValue(String.class);
                    String user_email = postSnapshot.child("User Email").getValue(String.class);
                    Log.d(TAG, "lawyer email:" + lawyer_email);
                    Log.d(TAG, "user email:" + user_email);
                    Log.d(TAG, email);
                    if (email.equals(lawyer_email) || email.equals(user_email)) {
                        if (email.equals(lawyer_email)) {
                            isLawyer = true;
                            mName.add(postSnapshot.child("User Name").getValue(String.class));
                            mNewMessage.add(postSnapshot.child("Lawyer Seen").getValue(Boolean.class));
                        } else {
                            isLawyer = false;
                            mName.add(postSnapshot.child("Lawyer Name").getValue(String.class));
                            mNewMessage.add(postSnapshot.child("User Seen").getValue(Boolean.class));
                        }
                        storeKeys.add(postSnapshot.getKey());
                        Log.d(TAG, postSnapshot.getKey());

                    }
                }
                for (int y = 0; y < mLastActive.size(); y++) {
                    Log.d(TAG, "mLastActive:" + y + mLastActive.get(y));
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
//                bundle.putString("room_name", ((TextView) view).getText().toString());
                if (isLawyer) {
                    root.child(storeKeys.get(i)).child("Lawyer Seen").setValue(true);
                } else {
                    root.child(storeKeys.get(i)).child("User Seen").setValue(true);
                }
                bundle.putString("user_name", name);
                bundle.putString("key", storeKeys.get(i));
                Log.d(TAG, "Clicked:" + storeKeys.get(i));
                Chat_Room chat_room = new Chat_Room();
                chat_room.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, chat_room).commit();
            }
        });
        return view;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
