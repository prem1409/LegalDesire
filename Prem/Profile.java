package com.example.ramesh.internshala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;

public class Profile extends Activity implements View.OnClickListener{
ImageView imageView;
    EditText email,name,mobile_no,city;
    Button next;
    String password="";
    Integer method=0;
    String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_profile);
        Intent data=getIntent();
         method=data.getIntExtra("method",1);
        imageView=(ImageView)findViewById(R.id.imageView);
        email=(EditText)findViewById(R.id.editText);
        name=(EditText)findViewById(R.id.editText2);
        mobile_no=(EditText)findViewById(R.id.editText3);
        city=(EditText)findViewById(R.id.editText4);
        next=(Button)findViewById(R.id.button);
        next.setOnClickListener(this);

        if(method==0){
             url=data.getStringExtra("photo_url");
            String email1=data.getStringExtra("email");
            email.setText(email1);
            password=data.getStringExtra("password");
            Glide.with(this).load(url).into(imageView);
        }else if(method==1){
             url=data.getStringExtra("photo_url");
            String email1=data.getStringExtra("email");
            String name1=data.getStringExtra("name");
            email.setText(email1);
            name.setText(name1);
            Glide.with(this).load(url).into(imageView);

        }else{
             url=data.getStringExtra("photo_url");
            Glide.with(this).load(url).into(imageView);

        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button){
            if(TextUtils.isEmpty(email.getText())){

            }else if(TextUtils.isEmpty(email.getText())){
                Toast.makeText(Profile.this, "Email Id cannot be empty", Toast.LENGTH_SHORT).show();

            }else if(TextUtils.isEmpty(name.getText())){
                Toast.makeText(Profile.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(mobile_no.getText())){
                Toast.makeText(Profile.this, "Mobile Number cannot be empty", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(city.getText())){
                Toast.makeText(Profile.this, "City cannot be empty", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
                Toast.makeText(Profile.this, "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
            }else if(mobile_no.getText().toString().trim().length()!=10){
                Toast.makeText(Profile.this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String email1=email.getText().toString();
                String mobile=mobile_no.getText().toString();
                String city1=city.getText().toString();
                String name1=name.getText().toString();
                Intent profile1=new Intent(Profile.this,Profile1.class);
                profile1.putExtra("email",email1);
                profile1.putExtra("mobile",mobile);
                profile1.putExtra("city",city1);
                profile1.putExtra("name",name1);
                profile1.putExtra("password",password);
                profile1.putExtra("photo_url",url);
                profile1.putExtra("method",method);
                startActivity(profile1);
                finish();

            }
        }

    }
}
