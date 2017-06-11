package com.example.ramesh.internshala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Video_center  extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    public static final String YOUTUBE_API_KEY = "AIzaSyCfmt4UmA93Ai_o-oC9fgTprnlcwnS6G6A";

    String name;
    YouTubePlayer player1;
    Button button;
    ListView videolist;
    TextView t;
    //String name;
    String itemkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_center); t=(TextView)findViewById(R.id.tittle);


        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(YOUTUBE_API_KEY, this);



        videolist=(ListView)findViewById(R.id.videolist);



        DatabaseReference databaseReference_v= FirebaseDatabase.getInstance().getReferenceFromUrl("https://legal-desire-e5f23.firebaseio.com/video_center");

        //create FirebaseAdapter
        final FirebaseListAdapter<String> firebaseListAdapter_v=new FirebaseListAdapter<String>(
                this,
                String.class,
                R.layout.v_list_items,
                databaseReference_v
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView_law=(TextView) v.findViewById(R.id.text1);
                textView_law.setTextSize(15);

                DatabaseReference item=getRef(position);
                itemkey=item.getKey();
                textView_law.setText(itemkey);
                name=model;

                //Toast.makeText(getApplicationContext(), itemkey, Toast.LENGTH_LONG).show();


            }
        };
        videolist.setAdapter(firebaseListAdapter_v);

        videolist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String selected_law=(String)lawlistview.getItemAtPosition(i);
                //Intent article=new Intent(Laws_list.this,Articles.class);
                //article.putExtra("law_name",selected_law);
                //startActivity(article);
                String selected_law=(String)videolist.getItemAtPosition(i);
                t.setText(itemkey);

                player1.cueVideo(selected_law);
                player1.play();
            }


        } );

    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            // player.cueVideo(""); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            player1=player;
            t.setText("Playlist");
            player.cuePlaylist("PLChOO_ZAB22VxRaLk4q3WZXzRBytm0fPg");
            player.play();

        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

// the number before &list id video id
//put video ids in database
//last number after = is playlist id so put that in initialization succes function.