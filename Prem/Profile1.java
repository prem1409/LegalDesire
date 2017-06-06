package com.example.ramesh.internshala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ramesh.internshala.Data.User;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Profile1 extends Activity {
String email,password,photo_url,name,city,mobile;
    String contact1,contact2,contact3,contact4,contact5,usertype;
    EditText Contact1,Contact2,Contact3,Contact4,Contact5,user;
    Button submit;
    String userid="0";
    Integer method;
    private DatabaseReference mDatabase;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_profile1);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child=mDatabase.child("users");
        Intent data=getIntent();
        email=data.getStringExtra("email");
        password=data.getStringExtra("password");
        method=data.getIntExtra("method",0);
        photo_url=data.getStringExtra("photo_url");
        name=data.getStringExtra("name");
        mobile=data.getStringExtra("mobile");
        city=data.getStringExtra("city");
        user=(EditText)findViewById(R.id.editText10);
        Contact1=(EditText)findViewById(R.id.editText5);
        Contact2=(EditText)findViewById(R.id.editText6);
        Contact3=(EditText)findViewById(R.id.editText7);
        Contact4=(EditText)findViewById(R.id.editText8);
        Contact5=(EditText)findViewById(R.id.editText9);
        submit=(Button)findViewById(R.id.button2);
        if(isloggedin()) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
             uid = accessToken.getUserId();
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(Contact1.getText())){
                    Toast.makeText(Profile1.this, "Contact 1 cannot be blank", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(Contact2.getText())){
                    Toast.makeText(Profile1.this, "Contact 2 cannot be blank", Toast.LENGTH_SHORT).show();
                }else  if(TextUtils.isEmpty(Contact3.getText())){
                    Toast.makeText(Profile1.this, "Contact 3 cannot be blank", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(Contact4.getText())){
                    Toast.makeText(Profile1.this, "Contact 4 cannot be blank", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(Contact5.getText())){
                    Toast.makeText(Profile1.this, "Contact 5 cannot be blank", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(user.getText())){
                    Toast.makeText(Profile1.this, "User type cannot be blank", Toast.LENGTH_SHORT).show();
                }else if(Contact1.getText().toString().trim().length()!=10){
                    Toast.makeText(Profile1.this, "Enter valid Contact 1", Toast.LENGTH_SHORT).show();
                }else if(Contact2.getText().toString().trim().length()!=10){
                    Toast.makeText(Profile1.this, "Enter valid Contact 2", Toast.LENGTH_SHORT).show();
                }else if(Contact3.getText().toString().trim().length()!=10){
                    Toast.makeText(Profile1.this, "Enter valid Contact 3", Toast.LENGTH_SHORT).show();
                }else if(Contact4.getText().toString().trim().length()!=10){
                    Toast.makeText(Profile1.this, "Enter valid Contact 4", Toast.LENGTH_SHORT).show();
                }else if(Contact5.getText().toString().trim().length()!=10){
                    Toast.makeText(Profile1.this, "Enter valid Contact 5", Toast.LENGTH_SHORT).show();
                }else if(Contact1.getText().toString().trim().matches(Contact2.getText().toString())||Contact1.getText().toString().trim().matches(Contact3.getText().toString())||Contact1.getText().toString().trim().matches(Contact4.getText().toString())||Contact1.getText().toString().trim().matches(Contact5.getText().toString())){
                    Toast.makeText(Profile1.this, "Contacts cannot be same", Toast.LENGTH_SHORT).show();
                }else if(Contact2.getText().toString().trim().matches(Contact1.getText().toString())||Contact2.getText().toString().trim().matches(Contact3.getText().toString())||Contact2.getText().toString().trim().matches(Contact4.getText().toString())||Contact2.getText().toString().trim().matches(Contact5.getText().toString())){
                    Toast.makeText(Profile1.this, "Contacts cannot be same", Toast.LENGTH_SHORT).show();
                }else if(Contact3.getText().toString().trim().matches(Contact2.getText().toString())||Contact3.getText().toString().trim().matches(Contact1.getText().toString())||Contact3.getText().toString().trim().matches(Contact4.getText().toString())||Contact3.getText().toString().trim().matches(Contact5.getText().toString())){
                    Toast.makeText(Profile1.this, "Contacts cannot be same", Toast.LENGTH_SHORT).show();
                }else if(Contact4.getText().toString().trim().matches(Contact2.getText().toString())||Contact4.getText().toString().trim().matches(Contact3.getText().toString())||Contact4.getText().toString().trim().matches(Contact1.getText().toString())||Contact4.getText().toString().trim().matches(Contact5.getText().toString())){
                    Toast.makeText(Profile1.this, "Contacts cannot be same", Toast.LENGTH_SHORT).show();
                }else if(Contact5.getText().toString().trim().matches(Contact2.getText().toString())||Contact5.getText().toString().trim().matches(Contact3.getText().toString())||Contact5.getText().toString().trim().matches(Contact4.getText().toString())||Contact5.getText().toString().trim().matches(Contact1.getText().toString())){
                    Toast.makeText(Profile1.this, "Contacts cannot be same", Toast.LENGTH_SHORT).show();
                }
                else if(user.getText().toString().contains("User")||user.getText().toString().contains("user")||user.getText().toString().contains("USER")||user.getText().toString().contains("LAWYER")||user.getText().toString().contains("Lawyer")||user.getText().toString().contains("lawyer")){
                   if(method==2){
                    if(user.getText().toString().contains("User")||user.getText().toString().contains("user")||user.getText().toString().contains("USER")){
                        DatabaseReference child1=child.child("regular");
                        HashMap<String, String> insertData = new HashMap<String, String>();
                        insertData.put("Contact1",Contact1.getText().toString());
                        insertData.put("Contact2",Contact2.getText().toString());
                        insertData.put("Contact3",Contact3.getText().toString());
                        insertData.put("Contact4",Contact4.getText().toString());
                        insertData.put("Contact5",Contact5.getText().toString());
                        insertData.put("city",city);
                        insertData.put("email",email);
                        insertData.put("mobile",mobile);
                        insertData.put("name",name);
                        insertData.put("password",password);
                        insertData.put("url",photo_url);
                        insertData.put("userid",userid);
                        insertData.put("usertype",user.getText().toString());
                        child1.child(uid).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Profile1.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Profile1.this,MainActivity.class));
                                finish();
                            }
                        });
                    }else{
                        DatabaseReference child1=child.child("lawyer");
                        HashMap<String, String> insertData = new HashMap<String, String>();
                        insertData.put("Contact1",Contact1.getText().toString());
                        insertData.put("Contact2",Contact2.getText().toString());
                        insertData.put("Contact3",Contact3.getText().toString());
                        insertData.put("Contact4",Contact4.getText().toString());
                        insertData.put("Contact5",Contact5.getText().toString());
                        insertData.put("city",city);
                        insertData.put("email",email);
                        insertData.put("mobile",mobile);
                        insertData.put("name",name);
                        insertData.put("password",password);
                        insertData.put("url",photo_url);
                        insertData.put("userid",userid);
                        insertData.put("usertype",user.getText().toString());
                        child1.child(uid).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Profile1.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Profile1.this,MainActivity.class));
                                finish();
                            }
                        }); }
                }else{
                       String value=email;
                     value=  value.replace(".","");
                     value=  value.replace("@","");
                       if(user.getText().toString().contains("User")||user.getText().toString().contains("user")||user.getText().toString().contains("USER")){
                           DatabaseReference child1=child.child("regular");
                           HashMap<String, String> insertData = new HashMap<String, String>();
                           insertData.put("Contact1",Contact1.getText().toString());
                           insertData.put("Contact2",Contact2.getText().toString());
                           insertData.put("Contact3",Contact3.getText().toString());
                           insertData.put("Contact4",Contact4.getText().toString());
                           insertData.put("Contact5",Contact5.getText().toString());
                           insertData.put("city",city);
                           insertData.put("email",email);
                           insertData.put("mobile",mobile);
                           insertData.put("name",name);
                           insertData.put("password",password);
                           insertData.put("url",photo_url);
                           insertData.put("userid",userid);
                           insertData.put("usertype",user.getText().toString());
                           child1.child(value).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   Toast.makeText(Profile1.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(Profile1.this,MainActivity.class));
                               }
                           });
                       }else{
                           DatabaseReference child1=child.child("lawyer");
                           HashMap<String, String> insertData = new HashMap<String, String>();
                           insertData.put("Contact1",Contact1.getText().toString());
                           insertData.put("Contact2",Contact2.getText().toString());
                           insertData.put("Contact3",Contact3.getText().toString());
                           insertData.put("Contact4",Contact4.getText().toString());
                           insertData.put("Contact5",Contact5.getText().toString());
                           insertData.put("city",city);
                           insertData.put("email",email);
                           insertData.put("mobile",mobile);
                           insertData.put("name",name);
                           insertData.put("password",password);
                           insertData.put("url",photo_url);
                           insertData.put("userid",userid);
                           insertData.put("usertype",user.getText().toString());
                           child1.child(value).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   Toast.makeText(Profile1.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(Profile1.this,MainActivity.class));
                               }
                           });
                   }

                }}

            }
        });



    }
    private boolean isloggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }
}
