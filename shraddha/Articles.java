package com.example.ramesh.internshala;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Articles extends AppCompatActivity {

    //create listview object
    private ListView articlelistview;


    //create firebase storage reference
    FirebaseStorage storage_articles;

    //create Storagereference object
    StorageReference storageReference_articles;

    public int check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);


        Intent intent = getIntent();
        String law = intent.getStringExtra("law_name");

        //create listview reference
        articlelistview = (ListView) findViewById(R.id.articleslist);

        //create firebase storage reference
        storage_articles = FirebaseStorage.getInstance();

        DatabaseReference databaseReference_articles = FirebaseDatabase.getInstance().getReferenceFromUrl("https://legal-desire-e5f23.firebaseio.com/online_lib/learn_law/law_articles/"+law);

        FirebaseListAdapter<String> firebaseListAdapter_articles = new FirebaseListAdapter<String>(
                this,
                String.class,
                R.layout.articles_items,
                databaseReference_articles
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView = (TextView) v.findViewById(R.id.text);
                //TextView textView1=(TextView)v.findViewById(android.R.id.text2);
                textView.setTextSize(15);
                textView.setText(model);
                //textView1.setText("Audio");
                final ImageButton audio = (ImageButton) v.findViewById(R.id.audio);
                audio.setTag(position);
                final ImageButton pdf = (ImageButton) v.findViewById(R.id.pdf);
                pdf.setTag(position);





                audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (Integer) view.getTag();
                        final String selected_item = (String) articlelistview.getItemAtPosition(position);

                        String r_name=selected_item.substring(0,selected_item.length()-4);
                        String re_name=r_name+".mp3";

                        check = 0;
                        Toast.makeText(getApplicationContext(), "Wait while the audio is loading....", Toast.LENGTH_LONG).show();
                        storageReference_articles = storage_articles.getReferenceFromUrl("gs://legal-desire-e5f23.appspot.com/online_lib/learn_law/audios/").child(re_name);

                        storageReference_articles.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                try {
                                   /* String url = uri.toString(); // your URL here
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mediaPlayer.setDataSource(url);
                                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                                    mediaPlayer.start();*/


                                    Intent article=new Intent(Articles.this,Play_audio.class);
                                    article.putExtra("law_name",uri.toString());
                                    article.putExtra("audio_name",selected_item);
                                    startActivity(article);



                                } catch (Exception e) {

                                }


                            }
                        });




                    }
                });

                pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {




                        check = 1;
                        int position = (Integer) view.getTag();
                        final String selected_item = (String) articlelistview.getItemAtPosition(position);
                        storageReference_articles = storage_articles.getReferenceFromUrl("gs://legal-desire-e5f23.appspot.com/").child(selected_item);

                        storageReference_articles.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent pdfOnpen = new Intent(Intent.ACTION_VIEW);
                                pdfOnpen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                pdfOnpen.setDataAndType(Uri.parse("http://drive.google.com/viewerng/viewer?embedded=true&url=" + uri.toString()), "*/*");

                                try {
                                    startActivity(pdfOnpen);
                                } catch (ActivityNotFoundException e) {

                                }


                            }


                        });


                    }
                });



            }



        };
        articlelistview.setAdapter(firebaseListAdapter_articles);


    }
}
