package com.example.ramesh.internshala;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Law_list extends AppCompatActivity {

    //create listview object
    private ListView lawlistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law_list);


        //create reference to lawlistview
        lawlistview=(ListView)findViewById(R.id.lawlistview);

        //create database reference
        DatabaseReference databaseReference_law= FirebaseDatabase.getInstance().getReferenceFromUrl("https://legal-desire-e5f23.firebaseio.com/online_lib/learn_law/law_name");

        //create FirebaseAdapter
        final FirebaseListAdapter<String> firebaseListAdapter_law=new FirebaseListAdapter<String>(
                this,
                String.class,
                android.R.layout.activity_list_item,
                databaseReference_law
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView_law=(TextView) v.findViewById(android.R.id.text1);
                textView_law.setTextSize(15);
                textView_law.setText(model);
                //DatabaseReference item=getRef(position);
                //String itemkey=item.getKey();
                //Toast.makeText(getApplicationContext(), itemkey, Toast.LENGTH_LONG).show();


            }
        };
        lawlistview.setAdapter(firebaseListAdapter_law);

        lawlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_law=(String)lawlistview.getItemAtPosition(i);
                Intent article=new Intent(Law_list.this,Articles.class);
                article.putExtra("law_name",selected_law);
                startActivity(article);

            }


        } );
    }
}
