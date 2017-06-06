package com.example.ramesh.internshala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
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

import java.util.Timer;
import java.util.TimerTask;

public class Home extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
ViewPager viewPager;
    Button signin,login;
    DatabaseReference mDatabase;
    GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child=mDatabase.child("users");
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(Home.this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        if(!pref.getBoolean("firstTime", false)){
            editor.putString("email", "");
            editor.putString("password","");
            editor.putInt("method",0);
            editor.putString("photo_url","");
            Toast.makeText(Home.this, "Shared Preference", Toast.LENGTH_SHORT).show();
        }else{
            String email=pref.getString("email","");
            String value=email.replace(".","");
            value=value.replace("@","");
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
                                    Toast.makeText(Home.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Home.this,MainActivity.class));
                                    finish();
                                }
                                if(Integer.parseInt(userid)==1){
                                    startActivity(new Intent(Home.this,Main2Activity.class));
                                    finish();
                                }
                                Toast.makeText(Home.this, userid, Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(Home.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Home.this,MainActivity.class));
                                        finish();
                                    }
                                    if(Integer.parseInt(userid)==1){
                                        startActivity(new Intent(Home.this,Main2Activity.class));
                                        finish();
                                    }
                                }else{

                                    String email=pref.getString("email","");
                                    String password=pref.getString("password","");
                                    String photo_url=pref.getString("photo_url","");
                                    Integer method=pref.getInt("method",0);
                                    Intent profile =new Intent(Home.this,Profile.class);
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
            });

        }

        editor.commit();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

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
                                 //   Toast.makeText(Login.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Home.this, MainActivity.class));
                                    finish();
                                }
                                if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                    startActivity(new Intent(Home.this, Main2Activity.class));
                                    finish();
                                }
                                Toast.makeText(Home.this, userid, Toast.LENGTH_SHORT).show();
                            } else {
                                DatabaseReference child1 = child.child("regular");
                                child1.orderByKey().equalTo(finalValue).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                            if (dataSnapshot.child(finalValue).child("userid").getValue().toString() == null) {

                                            }
                                            if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 0) {
                                                Toast.makeText(Home.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Home.this, MainActivity.class));
                                                finish();
                                            }
                                            if (Integer.parseInt(dataSnapshot.child(finalValue).child("userid").getValue().toString()) == 1) {
                                                startActivity(new Intent(Home.this, Main2Activity.class));
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
                            Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });







        }


        if (opr.isDone()) {

            GoogleSignInResult result = opr.get();
            GoogleSignInAccount account=result.getSignInAccount();
            String email=account.getEmail();
            String value=email.replace(".","");
            value=value.replace("@","");
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
                                Toast.makeText(Home.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Home.this,MainActivity.class));
                                finish();
                            }
                            if(Integer.parseInt(userid)==1){
                                startActivity(new Intent(Home.this,Main2Activity.class));
                                finish();
                            }
                            Toast.makeText(Home.this, userid, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(Home.this, "Enter 0", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Home.this,MainActivity.class));
                                    finish();
                                }
                                if(Integer.parseInt(userid)==1){
                                    startActivity(new Intent(Home.this,Main2Activity.class));
                                    finish();
                                }
                            }else{
                                   startActivity(new Intent(Home.this,Login.class));
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




        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new MyTimerClass(),2000,4000);
        signin=(Button)findViewById(R.id.button3);
        login=(Button)findViewById(R.id.button4);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Signin.class));
                finish();
            }
        });
login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(Home.this,Login.class));
        finish();
    }
});
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private boolean isloggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

    public class MyTimerClass extends TimerTask{

        @Override
        public void run() {
            Home.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem()==0){
                        viewPager.setCurrentItem(1);
                    }else if(viewPager.getCurrentItem()==1){
                        viewPager.setCurrentItem(2);
                    }else if(viewPager.getCurrentItem()==2){
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}
