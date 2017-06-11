package com.example.ramesh.internshala;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Online_lib extends AppCompatActivity {

    //create listview object
    private ListView mlistview;

    //create Button object
    private Button learnlaw_button;

    //create firebasestorage instance
    private FirebaseStorage storage;

    //create storagereference object
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_lib);

        // Firebase.setAndroidContext(this);   //this works for only one page alternative way is create a applicatio file:fireup for all activities

        learnlaw_button=(Button)findViewById(R.id.button);
        //alternative to setAndroidcontext
        /*if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }*/
        storage= FirebaseStorage.getInstance();
        //create a listview reference
        mlistview=(ListView)findViewById(R.id.listView);

        //create database reference
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://legal-desire-e5f23.firebaseio.com/online_lib/indian_bare_acts");

        //create firebase adapter
        FirebaseListAdapter<String> firebaseListAdapter=new FirebaseListAdapter<String>(
                this
                ,String.class,
                android.R.layout.activity_list_item,
                databaseReference

        ) {
            @Override
            protected void populateView(View v, String model, int position) {

                //display name of pdf in listview
                TextView textview=(TextView) v.findViewById(android.R.id.text1);
                textview.setTextSize(15);
                textview.setText(model);

            }
        };
        mlistview.setAdapter(firebaseListAdapter);

        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFromList = (String) mlistview.getItemAtPosition(i);
                Toast.makeText(
                        getApplicationContext(),
                        selectedFromList, Toast.LENGTH_SHORT)
                        .show();
                storageRef = storage.getReferenceFromUrl("gs://legal-desire-e5f23.appspot.com/").child(selectedFromList);

                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        /*Log.e("Tuts+", "uri: " + uri.toString());
                        //Handle whate
                        ver you're going to do with the URL here
                        Toast.makeText(
                                getApplicationContext(),
                                uri.toString(), Toast.LENGTH_SHORT)
                                .show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        intent.setDataAndType(Uri.parse(uri.toString()), "text/html");

                        startActivity(intent);*/

                        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pdfOpenintent.setDataAndType(Uri.parse("http://drive.google.com/viewerng/viewer?embedded=true&url="+uri.toString()), "*/*");
                        try {
                            startActivity(pdfOpenintent);
                        }
                        catch (ActivityNotFoundException e) {

                        }

                    }
                });

            }
        });

        learnlaw_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent law_activity=new Intent(Online_lib.this,Law_list.class);
                startActivity(law_activity);
            }
        });


    }
}
