package com.example.msp.legaldesire;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msp.legaldesire.Admin_Module.Lawyer_Module;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "registration123";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    boolean isLawyer = false;
    boolean isRegular = false;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User");


    String personName;
    String personEmail;
    Uri personPhoto;

    ImageView mPersonPhoto;
    TextView mPersonName;
    TextView mPersonEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPersonPhoto = (ImageView) findViewById(R.id.imageView);
        mPersonName = (TextView) findViewById(R.id.text_name);
        mPersonEmail = (TextView) findViewById(R.id.text_email);

        SharedPreferences preferences = getSharedPreferences("store_name_and_email", MODE_PRIVATE);
        personName = preferences.getString("person_name", null);
        personEmail = preferences.getString("person_email", null);
        Bundle bundle = new Bundle();
        bundle.putString("personName", personName);
        bundle.putString("personEmail", personEmail);
        Chat_Module chat_module = new Chat_Module();
        chat_module.setArguments(bundle);

        Log.d(TAG, "name:" + personName + "," + personEmail);

        mDatabase.child("Lawyer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside Lawyer search");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String lemail = postSnapshot.child("Email").getValue(String.class);
                    Log.d(TAG, "Lawyer:" + lemail);

                    if (lemail.equals(personEmail)) {
                        //person has registered before as lawyer, head to lawyer profile
                        isLawyer = true;
                        isRegular = false;
                        Log.d(TAG, "User logged in is lawyer");
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new Profile_Lawyer()).commitAllowingStateLoss();

                        break;
                        //Direct user to his/her profile

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!isLawyer) {
            mDatabase.child("Regular").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Inside Regular search");
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        Log.d(TAG, "Regular:" + lemail);
                        if (lemail.equals(personEmail)) {
                            //person has registered before as regular user, head to regular user profile
                            isRegular = true;
                            isLawyer = false;
                            Log.d(TAG, "User logged is regular");
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, new Profile_Regular()).commitAllowingStateLoss();
                            break;

                            //Direct user to his/her profile
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        if (!isLawyer && !isRegular) {
            //User has logged in for the first time, direct user to registration screen
            Log.d(TAG, "User logged for 1st time");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new AccountType()).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {


        return super.onCreateView(parent, name, context, attrs);
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.search_lawyer) {
            fragmentTransaction.replace(R.id.fragment_container, new SearchLawyer()).commit();
        } else if (id == R.id.admin_module) {
            fragmentTransaction.replace(R.id.fragment_container, new Lawyer_Module()).commit();
        } else if (id == R.id.logout) {
            signOut();
        } else if (id == R.id.chat_module) {
            fragmentTransaction.replace(R.id.fragment_container, new Chat_Module()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                  //      Toast.makeText(MainActivity.this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
