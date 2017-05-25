package com.example.ramesh.internshala;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity implements View.OnClickListener{
    Button Signin,Register;
    private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        final DatabaseReference child = databaseReference.child("users/");
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();



        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            GoogleSignInAccount account=result.getSignInAccount();
            String email=account.getEmail();String value = email.replace(".", "");
            value = value.replace("@", "");

            final String finalValue = value;
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
                                    startActivity(new Intent(Home.this,Login.class));
                                    //                 Toast.makeText(Profile.this, "User ID doesnt exist", Toast.LENGTH_SHORT).show();
                                }
                                else
                                if (Integer.parseInt(userid) == 0) {
                                    Intent transfer=new Intent(Home.this,FirstActivity.class);
                                    transfer.putExtra("value",finalValue);
                                    startActivity(transfer);
                                    finish();
                                }else if (Integer.parseInt(userid) == 1){
                                    //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Home.this, MainActivity.class));
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

        if (isFacebookLoggedIn()) {
            AccessToken loginResult=AccessToken.getCurrentAccessToken();
            String name=loginResult.getUserId();
            final String    uid1=name;
            String value=uid1;

            final String finalValue=value;
            child.orderByKey().equalTo(value).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null || dataSnapshot.getChildren() != null) {
                        DatabaseReference child1 = child.child(finalValue).child("userid");
                        child1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null || dataSnapshot.getChildren() != null) {
                                    String userid = dataSnapshot.getValue(String.class);
                                    if(userid==""||userid==null){
                                        //                  Toast.makeText(Profile.this, "User ID doesnt exist", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    if (Integer.parseInt(userid) == 0 ) {
                                        Log.i("Value1",Integer.parseInt(userid)+"");
                                     //   Toast.makeText(Home.this, "Value:" +Integer.parseInt(userid), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Home.this, FirstActivity.class));
                                        finish();
                                    }
                                    else if (Integer.parseInt(userid) == 1){
                                        //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Home.this, MainActivity.class));
                                        finish();
                                    }}
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
            SharedPreferences mypreference=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        Boolean boolea=mypreference.getBoolean("Logged",false);
            if(boolea==true){
                Intent login=new Intent(Home.this,Profile.class);
                String email=mypreference.getString("Email","");
                String password=mypreference.getString("password","");
                Integer method=mypreference.getInt("method",0);
                login.putExtra("email",email);
                login.putExtra("password",password);
                login.putExtra("method",method);
                startActivity(login);
                finish();
               // Toast.makeText(Home.this, "Entered", Toast.LENGTH_SHORT).show();
            }else{
                editor.putString("Email","");
                editor.putString("password","");
                editor.putInt("method",0);
                editor.putBoolean("Logged",false);
                editor.apply();
            }
         Signin=(Button)findViewById(R.id.button);
        Register=(Button)findViewById(R.id.button2);
        Signin.setOnClickListener(this);
        Register.setOnClickListener(this);

    }
    private Boolean exit = false;
    @Override
    public void onBackPressed(){
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button){
            startActivity(new Intent(this,Signin.class));
        }
        if(view.getId()==R.id.button2){
            startActivity(new Intent(this,Login.class));
        }

    }
}
