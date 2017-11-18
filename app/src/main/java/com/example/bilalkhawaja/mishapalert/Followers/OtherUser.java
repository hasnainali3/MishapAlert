package com.example.bilalkhawaja.mishapalert.Followers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bilalkhawaja.mishapalert.Profiles.CustomAdapter;
import com.example.bilalkhawaja.mishapalert.Profiles.DataModel;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtherUser extends AppCompatActivity {

    String username,picture;
    int follow = 0;
    TextView name, city, post, followers, following;
    String current_userid, other_userid, otheruser_name, pic;
    int follower, followings;
    ImageView profileImage;
    Button btnFollow;
    ArrayList<DataModel> list;
    ListView listView;
    CustomAdapter adapter;
    FirebaseUser user;
    private DatabaseReference databaseReference, databaseref_posts, dataref_otheruser_addingdata, databaseref_currentuser_check_following, databaseref_delete_following, databaseref_delete_follower;
    private Firebase firebaseref_otheruser_fetchingdata,  databaseref_following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);



        listView = (ListView) findViewById(R.id.lvuserdetail);
        list = new ArrayList<>();
        post = (TextView) findViewById(R.id.tvPost);
        followers = (TextView) findViewById(R.id.tvFollowers);
        following = (TextView) findViewById(R.id.tvFollwoing);
        name = (TextView) findViewById(R.id.tvName);
        city = (TextView) findViewById(R.id.tvCity);
        profileImage = (ImageView) findViewById(R.id.ivProfileimage);
        btnFollow = (Button)findViewById(R.id.btnFollow);
        //getting the id of the currently signed in user
        user = FirebaseAuth.getInstance().getCurrentUser();
         current_userid = user.getUid();
        //intent getting user id, name and picture from search
         other_userid = getIntent().getStringExtra("UserID");
         otheruser_name = getIntent().getStringExtra("name");
         //pic = getIntent().getStringExtra("picture");

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseref_posts = databaseReference.child(other_userid).child("posts");
        dataref_otheruser_addingdata =  databaseReference.child(other_userid);

        databaseref_delete_following = databaseReference.child(current_userid + "/" + "Following");
        databaseref_delete_follower = databaseReference.child(other_userid + "/" + "Follower");

        databaseref_following = new Firebase("https://mishap-alert.firebaseio.com/Users/" + current_userid);

        databaseref_currentuser_check_following = databaseReference.child(current_userid).child("Following");

        firebaseref_otheruser_fetchingdata = new Firebase("https://mishap-alert.firebaseio.com/Users/" + other_userid);

        //Fetching the detail of the current signed in user i.e username, profile picture and user id.
        databaseref_following.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                username = map.get("username");
                picture = map.get("uri");
                follow = Integer.parseInt(String.valueOf(map.get("following")));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        databaseReference.child(other_userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    pic = data.child("uri").getValue(String.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        LoadUserData();
        LoadProfile();
    }


    private void LoadUserData() {

        databaseref_currentuser_check_following.orderByKey().equalTo(other_userid).addValueEventListener(new ValueEventListener() {
            String id1 = null;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()) {

                    id1 = data.getKey().toString();


                }
                if(other_userid.equals(id1)) {
                    btnFollow.setText("Unfollow");
                    btnFollow.setBackground(getResources().getDrawable(R.drawable.buttonshape_unfollow));
                    btnFollow.setTextColor(getResources().getColor(R.color.black));


                }
                else if (!other_userid.equals(id1)) {
                    btnFollow.setText("Follow");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //Loading user profile
    public void LoadProfile() {

        firebaseref_otheruser_fetchingdata.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                Map<String, String> map = dataSnapshot.getValue(Map.class);

                final String Name = map.get("name");
                String City = map.get("city");
                String increment = String.valueOf(map.get("increment"));
                follower = Integer.parseInt(String.valueOf(map.get("followers")));
                followings = Integer.parseInt(String.valueOf(map.get("following")));
                final String downloadURI = map.get("uri");

                final Double radius = Double.valueOf(String.valueOf(map.get("radius")));

                name.setText(Name);
                city.setText(City);
                post.setText(increment);
                followers.setText(follower+"");
                following.setText(followings+"");
                Picasso.with(OtherUser.this).load(downloadURI).fit().centerCrop().into(profileImage);

                //For retrieving the detail of user posts and setting it in custom listview through DataModel class
                com.google.firebase.database.ValueEventListener eventListener = new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
                            String description = ds.child("description").getValue(String.class);
                            String dateTime = ds.child("dateTime").getValue(String.class);
                            String posturi = ds.child("posturi").getValue(String.class);
                            String severity = ds.child("severity").getValue(String.class);
                            String lat = ds.child("lat").getValue(String.class);
                            String lon = ds.child("lon").getValue(String.class);
                            String metadata = ds.child("metadata").getValue(String.class);
                            DataModel dataModel = new DataModel(other_userid,Name, dateTime, description, downloadURI, posturi, lon, lat, severity,ds.getKey().toString(),metadata, radius);
                            list.add(dataModel);
                        }

                        adapter = new CustomAdapter(OtherUser.this, list);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("DataBase Error", databaseError.toString());
                    }

                };
                databaseref_posts.addListenerForSingleValueEvent(eventListener);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        btnFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                if(btnFollow.getText().equals("Follow")) {

                    Map<String, Object> map = new HashMap<>();
                    follower++;
                    map.put("followers", follower);
                    dataref_otheruser_addingdata.updateChildren(map);

                    //creating a Followers node in the id of searched user and saving the detail of the currently signed in user
                    // in the searched user node.
                    firebaseref_otheruser_fetchingdata.child("Follower").child(current_userid).setValue(current_userid);

                    //updating the following child node in the current user
                    follow++;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("following", follow);
                    databaseref_following.updateChildren(map1);

                    //creating a Following node in the id of current user and saving the detail of the searched user
                    // in the current user node.
                    databaseref_following.child("Following").child(other_userid).setValue(other_userid);

                }

                else if(btnFollow.getText().equals("Unfollow"))
                {
                    databaseref_delete_following.child(other_userid).removeValue();
                    databaseref_delete_follower.child(current_userid).removeValue();

                    Map<String, Object> map = new HashMap<>();
                    follower--;
                    map.put("followers", follower);
                    dataref_otheruser_addingdata.updateChildren(map);

                    Map<String, Object> map1 = new HashMap<>();
                    follow--;
                    map1.put("following", follow);
                    databaseref_following.updateChildren(map1);


                }
                LoadUserData();


            }
        });

    }
}
