package com.example.ramesh.internshala;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Signin extends FragmentActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    LoginButton facebook_login;
    CallbackManager callbackManager;
    SignInButton google_signin;
    private static final int signin=0;
    GoogleApiClient googleApiClient;
    int google=0;
    EditText email1,password;
    Button login;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(Signin.this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
        facebook_login=(LoginButton)findViewById(R.id.fb_signin);
        callbackManager=CallbackManager.Factory.create();
        google_signin=(SignInButton)findViewById(R.id.google_signin);
        google_signin.setOnClickListener(this);
        email1=(EditText)findViewById(R.id.email2);
        password=(EditText)findViewById(R.id.password2);
        login=(Button)findViewById(R.id.signup);
        login.setOnClickListener(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();
    final    DatabaseReference child=mDatabase.child("users");
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        if(!pref.getBoolean("firstTime", false)){
            editor.putString("email", "");
            editor.putString("password","");
            editor.putInt("method",0);
            editor.putString("photo_url","");
            Toast.makeText(Signin.this, "Shared Preference", Toast.LENGTH_SHORT).show();
        }else{
            String email=pref.getString("email","");
            String value=email.replace(".","");
            value=value.replace("@","");
            String password1=pref.getString("password","");
            final String finalValue=value;
            child.orderByKey().equalTo(value).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        DatabaseReference child1=child.child("lawyer");
                        child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //  Toast.makeText(Home.this, "Entered", Toast.LENGTH_SHORT).show();
                                String userid=dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                if(userid==""){

                                } if(Integer.parseInt(userid)==0){
                                    Toast.makeText(Signin.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signin.this,MainActivity.class));
                                    finish();
                                }
                                if(Integer.parseInt(userid)==1){
                                    startActivity(new Intent(Signin.this,Main2Activity.class));
                                    finish();
                                }
                                Toast.makeText(Signin.this, userid, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }else{
                        DatabaseReference child1=child.child("regular");
                        child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null&&dataSnapshot!=null){
                                    String userid=dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                    if(userid==""){

                                    }
                                    if(Integer.parseInt(userid)==0){
                                        Toast.makeText(Signin.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Signin.this,MainActivity.class));
                                        finish();
                                    }
                                    if(Integer.parseInt(userid)==1){
                                        startActivity(new Intent(Signin.this,Main2Activity.class));
                                        finish();
                                    }
                                }else{

                                    String email=pref.getString("email","");
                                    String password=pref.getString("password","");
                                    String photo_url=pref.getString("photo_url","");
                                    Integer method=pref.getInt("method",0);
                                    Intent profile =new Intent(Signin.this,Profile.class);
                                    profile.putExtra("photo_url",photo_url);
                                    profile.putExtra("method",0);
                                    profile.putExtra("email",email);
                                    profile.putExtra("password",password);
                                    startActivity(profile);
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
            });}



        editor.commit();



        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               final DatabaseReference child=mDatabase.child("users");
                String uid=loginResult.getAccessToken().getUserId();
                final String finalValue=uid;
                child.orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseReference child1=child.child("lawyer");
                        child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    String userid = "2";
                                    if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 0) {
                                        Toast.makeText(Signin.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Signin.this, MainActivity.class));
                                        finish();
                                    }
                                    if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                        startActivity(new Intent(Signin.this, Main2Activity.class));
                                        finish();
                                    }
                                    Toast.makeText(Signin.this, userid, Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference child1 = child.child("regular");
                                    child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                                if (dataSnapshot.child(finalValue).child("userid").getValue().toString() == null) {

                                                }
                                                if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 0) {
                                                    Toast.makeText(Signin.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Signin.this, MainActivity.class));
                                                    finish();
                                                }
                                                if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                                    startActivity(new Intent(Signin.this, Main2Activity.class));
                                                    finish();
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
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(Signin.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancel() {
                Toast.makeText(Signin.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Signin.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });}
        @Override
        protected  void onActivityResult(int requestcode, int resultcode, Intent intent){
            if(google==0){
                callbackManager.onActivityResult(requestcode,resultcode,intent);
            }
            if(google==1){
                super.onActivityResult(requestcode,resultcode,intent);
                if(requestcode==signin){
                    GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                    handleresult(result);
                }
            }
        }

    private void google_sigin1(){
        Toast.makeText(Signin.this, "Entered Here", Toast.LENGTH_SHORT).show();
        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,signin);
        google=1;
    }
    private void handleresult(GoogleSignInResult result){
        GoogleSignInAccount account=result.getSignInAccount();
        if(result.isSuccess()) {
            Toast.makeText(Signin.this, "Signed in", Toast.LENGTH_SHORT).show();
            String email=account.getEmail();
            String value=email.replace(".","");
            value=value.replace("@","");
            final String finalValue=value;
            final DatabaseReference child=mDatabase.child("users");
            child.orderByKey().equalTo(value).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference child1=child.child("lawyer");
                    child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //  Toast.makeText(Home.this, "Entered", Toast.LENGTH_SHORT).show();
                            String userid=dataSnapshot.child(finalValue).child("userid").getValue().toString();
                            if(userid==""){
                                DatabaseReference child1=child.child("regular");
                                child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userid=dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                        if(userid==""){

                                        }
                                        if(Integer.parseInt(userid)==0){
                                            Toast.makeText(Signin.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Signin.this,MainActivity.class));
                                            finish();
                                        }
                                        if(Integer.parseInt(userid)==1){
                                            startActivity(new Intent(Signin.this,Main2Activity.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } if(Integer.parseInt(userid)==0){
                                Toast.makeText(Signin.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Signin.this,MainActivity.class));
                                finish();
                            }
                            if(Integer.parseInt(userid)==1){
                                startActivity(new Intent(Signin.this,Main2Activity.class));
                                finish();
                            }
                            Toast.makeText(Signin.this, userid, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }}
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.google_signin)
            google_sigin1();
        if(view.getId()==R.id.login){


        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
