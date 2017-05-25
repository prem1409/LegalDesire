package com.example.ramesh.internshala;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ramesh.internshala.Data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

public class Profile1 extends AppCompatActivity implements View.OnClickListener{
EditText contact1,contact2,contact3,contact4,contact5,user;
    Button Submit;
    String name,email,city,mobile,url,password,uid;
    String Contact1,Contact2,Contact3,Contact4,Contact5,usertype;
    private DatabaseReference mDatabase;
    Integer Method=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile1);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        contact1=(EditText)findViewById(R.id.contact1);
        contact2=(EditText)findViewById(R.id.contact2);
        contact3=(EditText)findViewById(R.id.contact3);
        contact4=(EditText)findViewById(R.id.contact4);
        contact5=(EditText)findViewById(R.id.contact5);
        Submit=(Button)findViewById(R.id.submit);
        user=(EditText)findViewById(R.id.user);
        Intent profile1=this.getIntent();
        Method=profile1.getExtras().getInt("method");
        name=profile1.getExtras().getString("name");
        password=profile1.getExtras().getString("password");
        email=profile1.getExtras().getString("email");
        city=profile1.getExtras().getString("city");
        mobile=profile1.getExtras().getString("mobile");
        url=profile1.getExtras().getString("image_url");
        uid=profile1.getExtras().getString("uid");
        Contact1=contact1.getText().toString();
        Contact2=contact2.getText().toString();
        Contact3=contact3.getText().toString();
        Contact4=contact4.getText().toString();
        Contact5=contact5.getText().toString();
        usertype=user.getText().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Submit.setOnClickListener(this);

    }
public void savedata(){
    Contact1=contact1.getText().toString();
    Contact2=contact2.getText().toString();
    Contact3=contact3.getText().toString();
    Contact4=contact4.getText().toString();
    Contact5=contact5.getText().toString();
    usertype=user.getText().toString();
    if(TextUtils.isEmpty(Contact1)){
        Toast.makeText(Profile1.this, "Contact No 1 cannot be empty", Toast.LENGTH_SHORT).show();
    }
   else if(TextUtils.isEmpty(Contact2)){
        Toast.makeText(Profile1.this, "Contact No 2 cannot be empty", Toast.LENGTH_SHORT).show();
    }
   else if(TextUtils.isEmpty(Contact3)){
        Toast.makeText(Profile1.this, "Contact No 3 cannot be empty", Toast.LENGTH_SHORT).show();
    }
   else if(TextUtils.isEmpty(Contact4)){
        Toast.makeText(Profile1.this, "Contact No 4 cannot be empty", Toast.LENGTH_SHORT).show();
    }
   else if(TextUtils.isEmpty(Contact5)){
        Toast.makeText(Profile1.this, "Contact No 5 cannot be empty", Toast.LENGTH_SHORT).show();
    }
    else  if(TextUtils.isEmpty(usertype)){
        Toast.makeText(Profile1.this, "User type cannot be empty", Toast.LENGTH_SHORT).show();
    }
    else if(Contact1.length()<=9&&Contact1.length()>=11){
        Toast.makeText(Profile1.this, "Enter valid Mobile Number for contact 1", Toast.LENGTH_SHORT).show();
    }

    else if(Contact2.length()<=9&&Contact2.length()>=11){
        Toast.makeText(Profile1.this, "Enter valid Mobile Number for contact 2", Toast.LENGTH_SHORT).show();
    }
    else if(Contact3.length()<=9&&Contact3.length()>=11){
        Toast.makeText(Profile1.this, "Enter valid Mobile Number for contact 3", Toast.LENGTH_SHORT).show();
    }
    else if(Contact4.length()<=9&&Contact4.length()>=11){
        Toast.makeText(Profile1.this, "Enter valid Mobile Number for contact 4", Toast.LENGTH_SHORT).show();
    }
    else if(Contact5.length()<=9&&Contact5.length()>=11){
        Toast.makeText(Profile1.this, "Enter valid Mobile Number for contact 5", Toast.LENGTH_SHORT).show();
    }

    else if(!usertype.matches("USER")&&!usertype.matches("User")&&!usertype.matches("user")&&!usertype.matches("Lawyer")&&!usertype.matches("LAWYER")&&!usertype.matches("lawyer")){
        Toast.makeText(Profile1.this, "User Type should be USER or LAWYER", Toast.LENGTH_LONG).show();
    }
    else{
        Toast.makeText(Profile1.this, "Entered Here", Toast.LENGTH_SHORT).show();
        Toast.makeText(Profile1.this,name+email+city+mobile+url+password+Contact1+Contact2+Contact3+Contact4+Contact5+usertype , Toast.LENGTH_SHORT).show();
        writeNewUser(name,email,city,mobile,url,password,Contact1,Contact2,Contact3,Contact4,Contact5,usertype);
        startActivity(new Intent(Profile1.this,FirstActivity.class));
        finish();
    }
}
    private void writeNewUser(String name, String email, String city,String mobile,String url,String password,String Contact1,String Contact2,String Contact3,String Contact4,String Contact5,String usertype) {
         String userid="0";


        User user = new User(name,userid,city,mobile,url,password,Contact1,Contact2,Contact3,Contact4,Contact5,usertype,email);
        Toast.makeText(Profile1.this, user+"", Toast.LENGTH_SHORT).show();
        if(Method==2){


            mDatabase.child("users").child(uid).setValue(user);

        }
        else {
            String value = email.replace(".", "");
            value = value.replace("@", "");
            mDatabase.child("users").child(value).setValue(user);
        }
    }







    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.submit){
            savedata();
        }

    }
}
