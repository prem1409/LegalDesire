package com.example.ramesh.internshala;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class Complaint_center extends AppCompatActivity {


    ListView dep_list;
    Button send;
    String subject,message;
    String  mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_center);


        Firebase.setAndroidContext(this);

        dep_list=(ListView)findViewById(R.id.listView) ;

        send=(Button) findViewById(R.id.sendEmail);

        DatabaseReference databaseReference_c= FirebaseDatabase.getInstance().getReferenceFromUrl("https://legal-desire-e5f23.firebaseio.com/Complaint_center/c_departments");


        final FirebaseListAdapter<String> firebaseListAdapter_law=new FirebaseListAdapter<String>(
                this,
                String.class,
                R.layout.list_items,
                databaseReference_c
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                //TextView textView_law=(TextView) v.findViewById(R.id.tex);
                //textView_law.setTextSize(15);
                //textView_law.setText(model);
                //DatabaseReference item=getRef(position);
                //String itemkey=item.getKey();
                //Toast.makeText(getApplicationContext(), itemkey, Toast.LENGTH_LONG).show();
                final CheckBox checkButton=(CheckBox) v.findViewById(R.id.checkBox);
                checkButton.setText(model);
                checkButton.setTag(position);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkButton.isChecked())
                        {
                            String dep=checkButton.getText().toString();
                            Toast.makeText(getApplicationContext(), dep, Toast.LENGTH_LONG).show();


                        }
                    }
                });

                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (Integer) view.getTag();
                        final String selected_dep = (String) dep_list.getItemAtPosition(position);
                        //Toast.makeText(getApplicationContext(), selected_dep, Toast.LENGTH_LONG).show();
                        Firebase mref=new Firebase("https://legal-desire-e5f23.firebaseio.com/Complaint_center/dep_info/"+selected_dep);
                        mref.addValueEventListener(new com.firebase.client.ValueEventListener() {
                            @Override
                            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                Map<String,String> map=dataSnapshot.getValue(Map.class);


                                mail = map.get("mail");
                                Toast.makeText(getApplicationContext(), mail, Toast.LENGTH_LONG).show();

                                send.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditText sub= (EditText)findViewById(R.id.textView4) ;
                                        subject=sub.getText().toString();

                                        EditText msg=(EditText)findViewById(R.id.textView5);
                                        message=msg.getText().toString();

                                        sendEmail();

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    }
                });


            }
        };
        dep_list.setAdapter(firebaseListAdapter_law);

        dep_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_dep=(String)dep_list.getItemAtPosition(i);
                //Intent article=new Intent(Laws_list.this,Articles.class);
                //article.putExtra("law_name",selected_law);
                //startActivity(article);
                Toast.makeText(getApplicationContext(), selected_dep, Toast.LENGTH_LONG).show();


            }


        } );


    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {mail};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Complaint_center.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
