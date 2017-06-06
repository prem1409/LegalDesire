package com.example.ramesh.internshala;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends FragmentActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    LoginButton facebook_login;
    CallbackManager callbackManager;
    SignInButton google_signin;
    private static final int signin=0;
    GoogleApiClient googleApiClient;
    int google=0;
    EditText email1,password;
    Button login;
    String email,name,method;
    int value=0;
    DatabaseReference mDatabase;
    String photo_url="https://drive.google.com/open?id=0B6E3wTM7m1NfSEdpcldUV1lIOHc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(Login.this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();


        mDatabase= FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child=mDatabase.child("users");
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        if(!pref.getBoolean("firstTime", false)){
            editor.putString("email", "");
            editor.putString("password","");
            editor.putInt("method",0);
            editor.putString("photo_url","");
            Toast.makeText(Login.this, "Shared Preference", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this,MainActivity.class));
                                        finish();
                                    }
                                    if(Integer.parseInt(userid)==1){
                                        startActivity(new Intent(Login.this,Main2Activity.class));
                                        finish();
                                    }
                                    Toast.makeText(Login.this, userid, Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this,MainActivity.class));
                                            finish();
                                        }
                                        if(Integer.parseInt(userid)==1){
                                            startActivity(new Intent(Login.this,Main2Activity.class));
                                            finish();
                                        }
                                    }else{

                                        String email=pref.getString("email","");
                                        String password=pref.getString("password","");
                                        String photo_url=pref.getString("photo_url","");
                                        Integer method=pref.getInt("method",0);
                                        Intent profile =new Intent(Login.this,Profile.class);
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



        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (opr.isDone())
            {
                Log.d("TAG", "Got cached sign-in");

                GoogleSignInResult result = opr.get();
                GoogleSignInAccount account=result.getSignInAccount();
                String email=account.getEmail();
                String value=email.replace(".","");
                value=value.replace("@","");
                final String finalValue=value;
                child.orderByKey().equalTo(value).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseReference child1=child.child("lawyer");
                        child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                //  Toast.makeText(Home.this, "Entered", Toast.LENGTH_SHORT).show();
                                String userid=dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                if(userid==""){

                                } if(Integer.parseInt(userid)==0){
                                    Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this,MainActivity.class));
                                    finish();
                                }
                                if(Integer.parseInt(userid)==1){
                                    startActivity(new Intent(Login.this,Main2Activity.class));
                                    finish();
                                }
                                Toast.makeText(Login.this, userid, Toast.LENGTH_SHORT).show();
                            }else{ DatabaseReference child1=child.child("regular");
                                    child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getValue()!=null&&dataSnapshot!=null){
                                            String userid=dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                            if(userid==""){

                                            }
                                            if(Integer.parseInt(userid)==0){
                                                Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Login.this,MainActivity.class));
                                                finish();
                                            }
                                            if(Integer.parseInt(userid)==1){
                                                startActivity(new Intent(Login.this,Main2Activity.class));
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

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                handleresult(result);


            }

        if(isloggedin()){
            AccessToken accessToken=AccessToken.getCurrentAccessToken();
            String uid=accessToken.getUserId();
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
                                    Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                                if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                    startActivity(new Intent(Login.this, Main2Activity.class));
                                    finish();
                                }
                                Toast.makeText(Login.this, userid, Toast.LENGTH_SHORT).show();
                            } else {
                                DatabaseReference child1 = child.child("regular");
                                child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                        if (dataSnapshot.child(finalValue).child("userid").getValue().toString() == null) {

                                        }
                                        if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 0) {
                                            Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            finish();
                                        }
                                        if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                            startActivity(new Intent(Login.this, Main2Activity.class));
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
                            Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });






            photo_url=  "http://graph.facebook.com/"+ uid+ "/picture?type=large";
         //   Toast.makeText(Login.this, loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
            Intent profile =new Intent(Login.this,Profile.class);
            profile.putExtra("photo_url",photo_url);
            profile.putExtra("method",2);
            profile.putExtra("uid",uid);
            startActivity(profile);
            finish();

        }





        facebook_login=(LoginButton)findViewById(R.id.fb_login_id);
        callbackManager=CallbackManager.Factory.create();
        google_signin=(SignInButton)findViewById(R.id.google_button);
        google_signin.setOnClickListener(this);
        email1=(EditText)findViewById(R.id.email1);
        password=(EditText)findViewById(R.id.password1);
        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(this);


        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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
                                        Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();
                                    }
                                    if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                        startActivity(new Intent(Login.this, Main2Activity.class));
                                        finish();
                                    }
                                    Toast.makeText(Login.this, userid, Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference child1 = child.child("regular");
                                    child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                                if (dataSnapshot.child(finalValue).child("userid").getValue().toString() == null) {

                                                }
                                                if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 0) {
                                                    Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                    finish();
                                                }
                                                if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                                    startActivity(new Intent(Login.this, Main2Activity.class));
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
                                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                photo_url=  "http://graph.facebook.com/"+ uid+ "/picture?type=large";
               // Toast.makeText(Login.this, loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
                Intent profile =new Intent(Login.this,Profile.class);
                profile.putExtra("photo_url",photo_url);
                profile.putExtra("method",2);
                profile.putExtra("uid",uid);
                startActivity(profile);
                finish();

            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });


            }
    @Override
    protected  void onActivityResult(int requestcode, int resultcode, Intent intent){
        if(google==0){
            callbackManager.onActivityResult(requestcode,resultcode,intent);
        }
        if(google==1){
            super.onActivityResult(requestcode,resultcode,intent);
            if(requestcode==signin){
                GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                handleresult(result)    ;
            }
        }
    }

    private void google_sigin(){

        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,signin);
        google=1;
    }
    private void handleresult(GoogleSignInResult result){
        GoogleSignInAccount account=result.getSignInAccount();
        if(result.isSuccess()) {
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
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                //  Toast.makeText(Home.this, "Entered", Toast.LENGTH_SHORT).show();
                                String userid = dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                if (userid == "") {
                                  }
                                if (Integer.parseInt(userid) == 0) {
                                    Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                                if (Integer.parseInt(userid) == 1) {
                                    startActivity(new Intent(Login.this, Main2Activity.class));
                                    finish();
                                }
                                Toast.makeText(Login.this, userid, Toast.LENGTH_SHORT).show();
                            }else{
                                DatabaseReference child1 = child.child("regular");
                                child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userid = dataSnapshot.child(finalValue).child("userid").getValue().toString();
                                        if (userid == "") {

                                        }
                                        if (Integer.parseInt(userid) == 0) {
                                            Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            finish();
                                        }
                                        if (Integer.parseInt(userid) == 1) {
                                            startActivity(new Intent(Login.this, Main2Activity.class));
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            name = account.getDisplayName();
            email = account.getEmail();
           try{ if(account.getPhotoUrl().toString()!=null){
                photo_url = account.getPhotoUrl().toString();
            }else{
                photo_url="https://drive.google.com/open?id=0B6E3wTM7m1NfSEdpcldUV1lIOHc";
            }}catch (Exception e){
               photo_url="https://drive.google.com/open?id=0B6E3wTM7m1NfSEdpcldUV1lIOHc";
           }
            Intent profile =new Intent(Login.this,Profile.class);
            profile.putExtra("photo_url",photo_url);
            profile.putExtra("method",1);
            profile.putExtra("email",email);
            profile.putExtra("name",name);
            startActivity(profile);
            finish();
            //Glide.with(this).load(photo_url).into(view);
        }

    }
    private boolean isloggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }
    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.google_button)
            google_sigin();
        if(view.getId()==R.id.login){
           email= email1.getText().toString();
         String password1=   password.getText().toString();
            Intent profile =new Intent(Login.this,Profile.class);
            profile.putExtra("photo_url",photo_url);
            profile.putExtra("method",0);
            profile.putExtra("email",email);
            profile.putExtra("password",password1);
            startActivity(profile);
            finish();
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", email);
            editor.putString("password",password1);
            editor.putInt("method",0);
            editor.putString("photo_url",photo_url);
            editor.putBoolean("firstTime", true);
            editor.commit();
            value=1;





        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
