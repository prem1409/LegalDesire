package com.example.ramesh.internshala;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signin extends AppCompatActivity implements View.OnClickListener{
    private SignInButton google_login;
    LoginButton loginButton;
    int req_code=0;
    int gmail=0;
    EditText emailid,password;
    Button Signin;
    AccessTokenTracker accessTokenTracker;
    DatabaseReference mDatabase;
    CallbackManager callbackManager;
    GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        google_login=(SignInButton)findViewById(R.id.signgoogle_login);
        google_login.setOnClickListener(this);
        loginButton=(LoginButton)findViewById(R.id.signfb_login_id);
        callbackManager= CallbackManager.Factory.create();
        Signin=(Button)findViewById(R.id.signin);
        emailid=(EditText)findViewById(R.id.signemailid);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(Signin.this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
               // Toast.makeText(Signin.this, "Err", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();

        password=(EditText)findViewById(R.id.signpassword);
        Signin.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = mDatabase.child("users/");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String uid=loginResult.getAccessToken().getUserId();
                final String uid1=uid;
                child.orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null || dataSnapshot.getChildren() != null) {
                            DatabaseReference child1 = child.child(uid1).child("userid");
                            child1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot != null || dataSnapshot.getChildren() != null) {
                                        String userid = dataSnapshot.getValue(String.class);
                                        if(userid==""||userid==null){
                                            Toast.makeText(Signin.this, "User ID doesnt exist", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Signin.this,Login.class));
                                            finish();
                                        }
                                        else
                                        if (Integer.parseInt(userid) == 0 ) {
                                            Log.i("Value1",Integer.parseInt(userid)+"");
                                            Toast.makeText(Signin.this, "Value:" +Integer.parseInt(userid), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Signin.this, FirstActivity.class));
                                            finish();
                                        }
                                        else if (Integer.parseInt(userid) == 1){
                                            //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Signin.this, MainActivity.class));
                                            finish();
                                        }}
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    }}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.signgoogle_login){
            google_signin();

        }
        if(view.getId()==R.id.signin){
            String email1=emailid.getText().toString();
            String value = email1.replace(".", "");
            value = value.replace("@", "");
            final String finalValue = value;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference child = mDatabase.child("users/");
            child.orderByKey().equalTo(value).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null || dataSnapshot.getChildren() != null) {
                        DatabaseReference password1=child.child(finalValue).child("password");
                        if(password1.toString().matches(password.getText().toString())){
                        DatabaseReference child1 = child.child(finalValue).child("userid");
                        child1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userid = dataSnapshot.getValue(String.class);
                                if(userid==""||userid==null){
                                    Toast.makeText(Signin.this, "User ID doesnt exist", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signin.this,Login.class));
                                    finish();
                                }
                                else
                                if (Integer.parseInt(userid) == 0) {
                                    Intent transfer=new Intent(Signin.this,FirstActivity.class);
                                    transfer.putExtra("value",finalValue);
                                    startActivity(transfer);
                                    finish();
                                }else if (Integer.parseInt(userid) == 1){
                                    //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signin.this, MainActivity.class));
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }else{
                            Toast.makeText(Signin.this, "Entered Password is wrong", Toast.LENGTH_SHORT).show();
                        }
                }}

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }


    private void google_signin(){

        Intent googleintent= Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        gmail=1;
        startActivityForResult(googleintent,req_code);
    }


    private void google_result(GoogleSignInResult result){

        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            String email=account.getEmail();
            String value = email.replace(".", "");
            value = value.replace("@", "");
            final String finalValue = value;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference child = mDatabase.child("users/");
            child.orderByKey().equalTo(value).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null || dataSnapshot.getChildren() != null) {
                        DatabaseReference child1 = child.child(finalValue).child("userid");
                        child1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userid = dataSnapshot.getValue(String.class);
                                if(userid==""||userid==null){
                                    Toast.makeText(Signin.this, "User ID doesnt exist", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signin.this,Login.class));
                                    finish();
                                }
                                else
                                if (Integer.parseInt(userid) == 0) {
                                    Intent transfer=new Intent(Signin.this,FirstActivity.class);
                                    transfer.putExtra("value",finalValue);
                                    startActivity(transfer);
                                    finish();
                                }else if (Integer.parseInt(userid) == 1){
                                    //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signin.this, MainActivity.class));
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else{
            startActivity(new Intent(Signin.this,Login.class));
            finish();
        }}
    @Override
    protected void onActivityResult(int requestcode,int resultcode,Intent data){
        if(gmail==0)
            callbackManager.onActivityResult(requestcode,resultcode,data);
        else{
            super.onActivityResult(requestcode,requestcode,data);

            if(requestcode==req_code){
                GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d("result",result+"");

                google_result(result);
            }}
    }
}
