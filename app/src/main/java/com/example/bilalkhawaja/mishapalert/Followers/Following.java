package com.example.bilalkhawaja.mishapalert.Followers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Following extends AppCompatActivity {

    ArrayList<DataModel_Followers_Following> list;
    ListView listView;
    customadapter_follower_and_following adapter;
    ImageView back;
    String name;
    String picture;


    //firebase
    private DatabaseReference databaseReference, databaseref_following, dataref_fetchdata;
    FirebaseUser user;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Following.this, Profile.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        listView = (ListView)findViewById(R.id.lvFollowing);
        list = new ArrayList<>();
        back = (ImageView)findViewById(R.id.ivBack);



        //firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseref_following = databaseReference.child(userID).child("Following");


        //For retrieving the detail of user posts and setting it in custom listview through DataModel class
        com.google.firebase.database.ValueEventListener eventListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {

                    final String id = ds.getKey();


                    if(id != null) {
                        dataref_fetchdata = databaseReference.child(id);
                        dataref_fetchdata.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                name = dataSnapshot.child("username").getValue(String.class);
                                picture = dataSnapshot.child("uri").getValue(String.class);

                                DataModel_Followers_Following dataModel = new DataModel_Followers_Following(id,name,picture);
                                list.add(dataModel);

                                adapter = new customadapter_follower_and_following(Following.this, list);
                                listView.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                    else {
                        Toast.makeText(Following.this,"no followers",Toast.LENGTH_SHORT).show();
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DataBase Error", databaseError.toString());
            }

        };
        databaseref_following.addListenerForSingleValueEvent(eventListener);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Following.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
