package com.example.bilalkhawaja.mishapalert.Notification;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.bilalkhawaja.mishapalert.Followers.OtherUser;
import com.example.bilalkhawaja.mishapalert.MapActivity.MapsActivity2;
import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Utilities.imagezoom;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowNotification extends AppCompatActivity {

    ImageView  ivMap,ivPlay;
    SimpleDraweeView ivProfile,ivPostimage;
    TextView tvusername, tvSeverity, tvDescription, tvLat, tvLong, tvDateTime;

    VideoView video;
    DatabaseReference databaseReference, dataref, data_notification;
    Firebase firebase;
    FirebaseUser firebaseUser;
    String current_userid, postID;
    ArrayList uri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);


        uri = new ArrayList();
        postID = getIntent().getStringExtra("post");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        data_notification = databaseReference;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        current_userid = firebaseUser.getUid();

        ivProfile = (SimpleDraweeView) findViewById(R.id.ivProfilePic);
        ivMap = (ImageView) findViewById(R.id.ivmap);
        ivPostimage = (SimpleDraweeView) findViewById(R.id.ivPost);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        tvusername = (TextView) findViewById(R.id.tvUsername);
        tvSeverity = (TextView) findViewById(R.id.tvSeverity);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDateTime = (TextView) findViewById(R.id.tvDate);
        tvLat = (TextView) findViewById(R.id.tvlat);
        tvLong = (TextView) findViewById(R.id.tvlong);
        video = (VideoView) findViewById(R.id.video);

        final String userid = getIntent().getStringExtra("userid");
       // Toast.makeText(this, "ID: " + userid, Toast.LENGTH_SHORT).show();
        databaseReference.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tvusername.setText(dataSnapshot.child("name").getValue(String.class));
               // Picasso.with(ShowNotification.this).load(dataSnapshot.child("uri").getValue(String.class)).fit().centerCrop().into(ivProfile);
                ivProfile.setImageURI(dataSnapshot.child("uri").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dataref = databaseReference.child(userid).child("posts");
        // Toast.makeText(this, "post: " + getIntent().getStringExtra("post"), Toast.LENGTH_SHORT).show();
        dataref.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("POST DATA", dataSnapshot.toString());

              /*  for(DataSnapshot data: dataSnapshot.getChildren()) {


                    Toast.makeText(ShowNotification.this, "meta: " + metadata, Toast.LENGTH_SHORT).show();
                }*/

                String metadata = dataSnapshot.child("metadata").getValue(String.class);

                if(metadata == null)
                {
                    //Do nothing
                }
                else {
                    if(metadata.equals("video"))
                    {
                        ivPlay.setVisibility(View.VISIBLE);
                        video.setVisibility(View.VISIBLE);
                        video.setVideoURI(Uri.parse(dataSnapshot.child("posturi").getValue(String.class)));
                    }
                    else {
                        ivPostimage.setVisibility(View.VISIBLE);
                       // Picasso.with(ShowNotification.this).load(dataSnapshot.child("posturi").getValue(String.class)).fit().centerCrop().into(ivPostimage);
                        ivPostimage.setImageURI(dataSnapshot.child("posturi").getValue(String.class));
                        uri.add(dataSnapshot.child("posturi").getValue(String.class));
                    }
                }

//                Toast.makeText(ShowNotification.this,dataSnapshot.child("metadata").getValue(String.class) , Toast.LENGTH_SHORT).show();
                tvSeverity.setText(dataSnapshot.child("severity").getValue(String.class));
                tvDescription.setText(dataSnapshot.child("description").getValue(String.class));
                tvDateTime.setText(dataSnapshot.child("dateTime").getValue(String.class));
                tvLat.setText(dataSnapshot.child("lat").getValue(String.class));
                tvLong.setText(dataSnapshot.child("lon").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                video.pause();
                ivPlay.setVisibility(View.VISIBLE);
                return false;
            }
        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                video.pause();
                ivPlay.setVisibility(View.VISIBLE);
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.start();
                ivPlay.setVisibility(View.INVISIBLE);
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowNotification.this, MapsActivity2.class);
                intent.putExtra("lat", tvLat.getText().toString());
                intent.putExtra("lon", tvLong.getText().toString());
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getStringExtra("userid").equals(current_userid))
                {
                    Intent intent = new Intent(ShowNotification.this, Profile.class);

                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(ShowNotification.this, OtherUser.class);
                    intent.putExtra("UserID", getIntent().getStringExtra("userid"));
                    startActivity(intent);
                }

            }
        });
        tvusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getStringExtra("userid").equals(current_userid))
                {
                    Intent intent = new Intent(ShowNotification.this, Profile.class);

                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(ShowNotification.this, OtherUser.class);
                    intent.putExtra("UserID", getIntent().getStringExtra("userid"));
                    startActivity(intent);
                }
            }
        });



        ivPostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowNotification.this, imagezoom.class);
                intent.putExtra("image", uri.get(0).toString());
                startActivity(intent);
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
