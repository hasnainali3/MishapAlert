package com.example.bilalkhawaja.mishapalert.Notification;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.Posts.Post;
import com.example.bilalkhawaja.mishapalert.Profiles.CustomAdapter;
import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Searchs.Search;
import com.example.bilalkhawaja.mishapalert.Utilities.BottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;

public class Notification_View extends AppCompatActivity {
    DatabaseReference databaseReference, dataref;
    FirebaseUser firebaseUser;
    ArrayList<Notification_datamodel> list;
    ListView lvNotification;
    String Userid;
    String title = null, image;
    Notification_adapter adapter;
    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_CAMERA = 3;
    private static final int REQUEST_VIDE0 = 4;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification__view);

        //bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        list = new ArrayList<>();
        lvNotification = (ListView) findViewById(R.id.lvNotification);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dataref = databaseReference;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Userid = firebaseUser.getUid();

        lvNotification.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Notification_View.this);
                builder.setCancelable(false);
                builder.setTitle("Delete this post?");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        databaseReference.child("Users/" + Userid + "/Notification/" + list.get(position).getPostid()).removeValue();
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

        databaseReference.child("Users/" + Userid + "/Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("TAG", dataSnapshot.toString());

                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                    final String description = datas.child("description").getValue(String.class);
                    final String userid = datas.child("id").getValue(String.class);
                    final String postid = datas.getKey();

                    dataref.child("Users/" + userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            title = dataSnapshot.child("name").getValue(String.class);
                            image = dataSnapshot.child("uri").getValue(String.class);

                            dataref.child("Users/" + userid + "/posts/" + postid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Log.d("onDataChange ", dataSnapshot.toString());
                                    final String type = dataSnapshot.child("type").getValue(String.class);


                                    Notification_datamodel datamodel = new Notification_datamodel(userid, postid, title, description, image, type);
                                    list.add(datamodel);


                                    Collections.reverse(list);
                                    adapter = new Notification_adapter(Notification_View.this, list);
                                    lvNotification.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                Toast.makeText(Notification_View.this, "postid: " + list.get(i).getPostid(), Toast.LENGTH_SHORT).show();
//
                Intent intent = new Intent(Notification_View.this, ShowNotification.class);
                intent.putExtra("userid", list.get(i).getUserid());
                intent.putExtra("post", list.get(i).getPostid());
                startActivity(intent);
            }
        });


        //For bottom Navigation bar
        BottomNavigation();

    }


    private void cameraIntent() {

        //Loading Post
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Passing image to Post activity through intent
            Intent intent = new Intent(Notification_View.this, Post.class);
            intent.putExtra("imageUri", imageBitmap);
            startActivity(intent);
            finish();
        }
        if (requestCode == REQUEST_VIDE0 && resultCode == RESULT_OK) {


            String videoUri = data.getDataString();
            Intent intent = new Intent(Notification_View.this, Post.class);
            intent.putExtra("videouri", videoUri);
            startActivity(intent);
            finish();

        }


    }

    //Function for bottom navigation
    public void BottomNavigation() {

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Home:
                        Intent intent = new Intent(Notification_View.this, Home.class);
                        startActivity(intent);
                        break;
                    case R.id.Search:
                        intent = new Intent(Notification_View.this, Search.class);
                        startActivity(intent);
                        break;
                    case R.id.Camera:
                        lvNotification.setVisibility(View.INVISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Notification_View.this);
                        builder.setCancelable(false);
                        builder.setTitle("Camera Option");
                        // builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Picture", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        cameraIntent();
                                    } else {
                                        String[] Permissionrequet = {Manifest.permission.CAMERA};
                                        requestPermissions(Permissionrequet, MY_CAMERA_REQUEST_CODE);

                                    }
                                }

                            }
                        });

                        builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                videointent();
                            }
                        });
                        builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent(getBaseContext(), Notification_View.class);
                                startActivity(intent1);
                            }
                        });
                        builder.create();
                        builder.show();
                        break;
                    case R.id.Notification:
                        intent = new Intent(Notification_View.this, Notification_View.class);
                        startActivity(intent);
                        break;
                    case R.id.Setting:
                        intent = new Intent(Notification_View.this, com.example.bilalkhawaja.mishapalert.Utilities.Settings.class);
                        startActivity(intent);
                        break;

                }

                return true;
            }
        });

    }

    private void videointent() {
        //Loading Post
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_VIDE0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Notification_View.this, Home.class);
        startActivity(intent);

    }
}
