package com.example.bilalkhawaja.mishapalert.Profiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.Followers.Followers;
import com.example.bilalkhawaja.mishapalert.Followers.Following;
import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity {

    TextView name, city, post, followers, following;
    ImageView profileImage;
    Button btnFollow;
    ArrayList<DataModel> list;
    ListView listView;
    LinearLayout lyfollowers, lyfollowing;
    CustomAdapter adapter;
    FirebaseUser user;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, databaseref;
    private Firebase ref;
    DataModel dataModel;
    Double increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dataModel = new DataModel();
        listView = (ListView) findViewById(R.id.lvuserdetail);
        lyfollowers = (LinearLayout) findViewById(R.id.lyFollower);
        lyfollowing = (LinearLayout) findViewById(R.id.lyFollowing);
        list = new ArrayList<>();
        post = (TextView) findViewById(R.id.tvPost);
        name = (TextView) findViewById(R.id.tvName);
        city = (TextView) findViewById(R.id.tvCity);
        followers = (TextView) findViewById(R.id.tvFollowers);
        following = (TextView) findViewById(R.id.tvFollwoing);
        profileImage = (ImageView) findViewById(R.id.ivProfileimage);
        btnFollow = (Button) findViewById(R.id.btnFollow);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseref = databaseReference.child(userID).child("posts");

        ref = new Firebase("https://mishap-alert.firebaseio.com/Users/" + userID);


        LoadProfile();


        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                increment =dataSnapshot.child("increment").getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        lyfollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Followers.class);
                startActivity(intent);
            }
        });

        lyfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Following.class);
                startActivity(intent);
            }
        });

        //Deleting posts
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setCancelable(false);
                builder.setTitle("Delete this post?");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        increment = increment - 1;

                        Map<String, Object> map = new HashMap<>();
                        map.put("increment", increment);
                        databaseref.child(list.get(position).getPostid()).removeValue();
                        databaseReference.child(userID).updateChildren(map);
                        list.remove(position);
                        adapter.notifyDataSetChanged();


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                builder.create();
                builder.show();

                return false;
            }
        });
    }


    //Loading user profile
    public void LoadProfile() {
        final String uID = user.getUid();


        ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                //fetching details
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                final String Name = map.get("name");
                String City = map.get("city");
                String increment = String.valueOf(map.get("increment"));
                String follower = String.valueOf(map.get("followers"));
                String followin = String.valueOf(map.get("following"));
                final String downloadURI = map.get("uri");

                //setting details
                Picasso.with(Profile.this).load(downloadURI).fit().centerCrop().into(profileImage);
                name.setText(Name);
                city.setText(City);
                post.setText(increment);
                followers.setText(follower);
                following.setText(followin);


               // Toast.makeText(Profile.this, increment+"", Toast.LENGTH_SHORT).show();
                //For retrieving the detail of user posts and setting it in custom listview through DataModel class
                if(!increment.equals(0))
                {
                    databaseref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                String description = ds.child("description").getValue(String.class);
                                String dateTime = ds.child("dateTime").getValue(String.class);
                                String posturi = ds.child("posturi").getValue(String.class);
                                String severity = ds.child("severity").getValue(String.class);
                                String lat = ds.child("lat").getValue(String.class);
                                String lon = ds.child("lon").getValue(String.class);
                                String metadata = ds.child("metadata").getValue(String.class);

                                DataModel dataModel = new DataModel(uID, Name, dateTime, description, downloadURI, posturi, lon, lat, severity, ds.getKey().toString(),metadata);
                                list.add(dataModel);
                            }

                            Collections.reverse(list);
                            adapter = new CustomAdapter(Profile.this, list);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Profile.this, Home.class);
        startActivity(intent);
        finish();
    }
}
