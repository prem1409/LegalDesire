package com.example.ramesh.internshala;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
ImageView Profile;
    EditText Name,Email,Mobile,City;
    Button Next;
    GoogleApiClient googleApiClient;
    private DatabaseReference mDatabase;
    String validemail= "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

            "\\@" +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

            "(" +

            "\\." +

            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

            ")+";
    String MobilePattern = "[0-9]{10}";
    String imageurl="";
    Integer Method=0;
    String password="";
    String uid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Name = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Mobile = (EditText) findViewById(R.id.city);
        City = (EditText) findViewById(R.id.mobile);
        Profile = (ImageView) findViewById(R.id.profilepicture);
        Next = (Button) findViewById(R.id.Next);
        Intent profile = this.getIntent();
        Method = profile.getExtras().getInt("method");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = mDatabase.child("users/");
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
                                //   Toast.makeText(Profile.this, "Value:" +Integer.parseInt(userid), Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(com.example.ramesh.internshala.Profile.this, FirstActivity.class));
                                   finish();
                               }
                              else if (Integer.parseInt(userid) == 1){
                                 //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(com.example.ramesh.internshala.Profile.this, MainActivity.class));
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

        } else {
            String email = profile.getExtras().getString("email");
            String value = email.replace(".", "");
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
                   //                 Toast.makeText(Profile.this, "User ID doesnt exist", Toast.LENGTH_SHORT).show();
                                }
                                else
                                if (Integer.parseInt(userid) == 0) {
                                    Intent transfer=new Intent(com.example.ramesh.internshala.Profile.this,FirstActivity.class);
                                    transfer.putExtra("value",finalValue);
                                    startActivity(transfer);
                                    finish();
                                }else if (Integer.parseInt(userid) == 1){
                                    //  Toast.makeText(Profile.this, "Entered Here", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(com.example.ramesh.internshala.Profile.this, MainActivity.class));
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

      if(Method==1) {
            String name=profile.getExtras().getString("name");
            String email=profile.getExtras().getString("email");

            imageurl=profile.getExtras().getString("image_url");

          Glide.with(this).load(imageurl).into(Profile);

            Name.setText(name);
            Email.setText(email);

        }
        if(Method==2) {
             imageurl=profile.getExtras().getString("image_url");
          Glide.with(this).load(imageurl).into(Profile);
            uid=profile.getExtras().getString("uid");
        }
        if(Method==3){
          String  email=profile.getExtras().getString("email");
            password=profile.getExtras().getString("password");
            Email.setText(email);
        }
            Next.setOnClickListener(this);
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();


    }

    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    private Boolean exit = false;
    @Override
    public void onBackPressed(){
        if (exit) {
            finish();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onClick(View view) {
        String name,email,city,mobile,url;
        String mobilepattern="[0-9]{10}";
        Name = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Mobile = (EditText) findViewById(R.id.city);
        City = (EditText) findViewById(R.id.mobile);
        Profile = (ImageView) findViewById(R.id.profilepicture);
        String phone=Mobile.getText().toString();
        Matcher matcherObj = Pattern.compile(validemail).matcher(Email.getText().toString());
        Matcher mobilepa=Pattern.compile(mobilepattern).matcher(Mobile.getText().toString());
        if(view.getId()==R.id.Next){
        if(TextUtils.isEmpty(Name.getText().toString()))
        {
            Toast.makeText(Profile.this, "Name Cannot be Blank", Toast.LENGTH_SHORT).show();

        } else if(TextUtils.isEmpty(Email.getText().toString()))
            {
                Toast.makeText(Profile.this, "Email Cannot be Blank", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(Mobile.getText().toString()))
            {
                Toast.makeText(Profile.this, "Mobile Number Cannot be Blank", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(City.getText().toString()))
            {
                Toast.makeText(Profile.this, "City Cannot be Blank", Toast.LENGTH_SHORT).show();
            }
        else if(!matcherObj.matches()){
            Toast.makeText(Profile.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
        }

        else if(phone.length()<=9 && phone.length()>=11){
            Toast.makeText(Profile.this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            }
            else
            {
                name=Name.getText().toString();
                email=Email.getText().toString();
                city=City.getText().toString();
                mobile=Mobile.getText().toString();
                url=imageurl;
                Intent profile1=new Intent(Profile.this,Profile1.class);
                profile1.putExtra("name", name);
                profile1.putExtra("email",email);
                profile1.putExtra("image_url",url);
                profile1.putExtra("mobile",mobile);
                profile1.putExtra("city",city);
                profile1.putExtra("method",Method);
                profile1.putExtra("password",password);
                profile1.putExtra("uid",uid);
                startActivity(profile1);
                finish();


            }

        }

    }
}
