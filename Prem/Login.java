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
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private SignInButton google_login;
    private GoogleApiClient googleApiClient;
    private static final int req_code=1;
    private int gmail=0;
    LoginButton loginButton;
    CallbackManager callbackManager;
    EditText emailid,password;
    Button Signin;
    AccessTokenTracker accessTokenTracker;
    String img_url="https://goo.gl/bJS58H";
    DatabaseReference mDatabase;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {

            GoogleSignInResult result = opr.get();
            GoogleSignInAccount account=result.getSignInAccount();
            String name=account.getDisplayName();
            String email=account.getEmail();
            try{ if(account.getPhotoUrl().toString()==null){
                img_url="https://goo.gl/bJS58H";
            }
            else{
                img_url=account.getPhotoUrl().toString();
            }}catch (Exception e){
                img_url="https://goo.gl/bJS58H";
            }
            Intent profile=new Intent(Login.this,Profile.class);
            Integer in=1;
            profile.putExtra("method",in);
            profile.putExtra("name", name);
            profile.putExtra("email",email);
            profile.putExtra("image_url",img_url);
            startActivity(profile);
            finish();

        } else if(isFacebookLoggedIn()){
            AccessToken loginResult=AccessToken.getCurrentAccessToken();
            String name=loginResult.getUserId();
            String img_url="http://graph.facebook.com/"+name+"/picture?type=large";
            Intent profile=new Intent(Login.this,Profile.class);
            Integer in=2;
            profile.putExtra("method",in);
            profile.putExtra("image_url",img_url);
            startActivity(profile);
            finish();
        }else {


        google_login=(SignInButton)findViewById(R.id.google_login);
        google_login.setOnClickListener(this);
        loginButton=(LoginButton)findViewById(R.id.fb_login_id);
        callbackManager= CallbackManager.Factory.create();
        Signin=(Button)findViewById(R.id.signin);
        emailid=(EditText)findViewById(R.id.emailid);
        password=(EditText)findViewById(R.id.password);
        Signin.setOnClickListener(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String uid=loginResult.getAccessToken().getUserId();
                String img_url="http://graph.facebook.com/"+uid+"/picture?type=large";
                Intent profile=new Intent(Login.this,Profile.class);
                Integer in=2;
                profile.putExtra("method",in);
                profile.putExtra("image_url",img_url);
                profile.putExtra("uid",uid);

                startActivity(profile);
                finish();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }}
    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
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

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.google_login){
            google_signin();

        }
        if(view.getId()==R.id.signin){
            emailid=(EditText)findViewById(R.id.emailid);
            password=(EditText)findViewById(R.id.password);
            Intent profile=new Intent(Login.this,Profile.class);
            Integer in=3;
            String email=emailid.getText().toString();
            String password1=password.getText().toString();
            profile.putExtra("method",in);
            profile.putExtra("email",email);
            profile.putExtra("password",password1);
            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("Email",email);
            editor.putString("password",password1);
            editor.putInt("method",in);
            editor.putBoolean("Logged",true);
            editor.apply();
          //  Toast.makeText(Login.this, "Value entered in shared preference" , Toast.LENGTH_SHORT).show();
            startActivity(profile);
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void google_signin(){

        Intent googleintent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        gmail=1;
        startActivityForResult(googleintent,req_code);
    }
    private void google_result(GoogleSignInResult result){

        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            String name=account.getDisplayName();
            String email=account.getEmail();
           try{ if(account.getPhotoUrl().toString()==null){
                img_url="https://goo.gl/bJS58H";
            }
            else{
                img_url=account.getPhotoUrl().toString();
            }}catch (Exception e){
               img_url="https://goo.gl/bJS58H";
           }
            Intent profile=new Intent(Login.this,Profile.class);
            Integer in=1;
            profile.putExtra("method",in);
            profile.putExtra("name", name);
            profile.putExtra("email",email);
            profile.putExtra("image_url",img_url);
            startActivity(profile);
            finish();
        }
    }
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
