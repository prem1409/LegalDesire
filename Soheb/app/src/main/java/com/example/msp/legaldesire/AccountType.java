package com.example.msp.legaldesire;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountType extends Fragment {
    public static final String TAG = "accounttype123";
    RadioButton mUser, mLawyer;
    Button mNext;


    public AccountType() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Account type onCreate");
        View view = inflater.inflate(R.layout.fragment_account_type, container, false);
        mUser = (RadioButton) view.findViewById(R.id.radio_user);
        mLawyer = (RadioButton) view.findViewById(R.id.radio_lawyer);
        mNext = (Button) view.findViewById(R.id.btn_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.isChecked()) {
                    //User wants to be registered as a regular user
                    Log.d(TAG, "user selected");
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new Regular_User_Registration()).commit();
                } else if (mLawyer.isChecked()) {
                    //    User wants to be registered as lawyer
                    Log.d(TAG, "lawyer selected");
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new Lawyer_User_registration()).commit();

                } else {
                    Toast.makeText(getContext(), "No type Selected", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
}
