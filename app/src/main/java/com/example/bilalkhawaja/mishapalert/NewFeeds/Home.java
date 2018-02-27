package com.example.bilalkhawaja.mishapalert.NewFeeds;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.GPS.GPSTracker;
import com.example.bilalkhawaja.mishapalert.GPS.Myservice;
import com.example.bilalkhawaja.mishapalert.Notification.Notification_View;
import com.example.bilalkhawaja.mishapalert.Posts.Post;
import com.example.bilalkhawaja.mishapalert.Profiles.CustomAdapter;
import com.example.bilalkhawaja.mishapalert.Profiles.DataModel;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Registration.PostModel;
import com.example.bilalkhawaja.mishapalert.Registration.User;
import com.example.bilalkhawaja.mishapalert.Searchs.Search;
import com.example.bilalkhawaja.mishapalert.Utilities.BottomNavigationViewHelper;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 3;
    private static final int REQUEST_VIDE0 = 4;
    GPSTracker gps;
    ArrayList<DataModel> list;
    ArrayList names, profileimage;
    ListView listView;
    CustomAdapter adapter;
    CustomAdapter_Home customAdapter_home;
    ArrayList<PostModel> postModel;
    String name, profileImage, currentuser_city;
    int following = 0, increment_following = 0, increment;

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    BottomNavigationView bottomNavigationView;
    //firebase
    private DatabaseReference databaseReference, databaseref_currentuser_post, databaseref_following_check, databaseref_otheruser_post, databaseReference_Searchcity;
    private Firebase firebaseRef;
    FirebaseUser user;
    // Double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
               /* gps = new GPSTracker(Home.this);
               *//* lat = gps.getLatitude();
                lon = gps.getLongitude();*/
            } else {
                String[] Permissionrequet = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(Permissionrequet, 10);

            }


        postModel = new ArrayList<>();

        names = new ArrayList();
        profileimage = new ArrayList();
        Intent intent = new Intent(this, Myservice.class);
        startService(intent);
        //bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        listView = (ListView) findViewById(R.id.list);
        list = new ArrayList<>();


        //firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = user.getUid();


        //<--Firebase storing Registration Token in database--->
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("FCM_TOKEN", token);
        databaseReference.updateChildren(map);
        // <--Firebase storing Registration Token in database--->

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseref_otheruser_post = FirebaseDatabase.getInstance().getReference("Users");
        databaseref_currentuser_post = databaseReference;
        databaseref_following_check = databaseReference.child(userID).child("Following");
        databaseReference_Searchcity = FirebaseDatabase.getInstance().getReference();
        firebaseRef = new Firebase("https://mishap-alert.firebaseio.com/Users/" + userID);

        //this is current user will all post notification everything and this is method for calling it once only
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {


              User user = dataSnapshot.getValue(User.class);

                //this is method to find all the post from peshawar

                FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("city").equalTo(user.getCity()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<User> userinacity = new ArrayList<>();
                        final ArrayList<PostModel> postModels = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            User user = dataSnapshot1.getValue(User.class);
                            following = Integer.parseInt(String.valueOf(dataSnapshot1.child("following").getValue(Long.class)));
                            userinacity.add(user);
                        }
                        for (User user : userinacity) {
                            if (user.getPosts() != null)
                                for (PostModel postModel : user.getPosts()) {
                                    if (postModel != null) {
                                        // Log.d("Postmodel", postModel.toString());
                                        postModel.setName(user.getName());
                                        postModel.setId(user.getId());
                                        postModel.setUri(user.getUri());
                                        postModel.setRadius(user.getRadius());
                                        postModel.setEmail(user.getEmail());
                                        postModels.add(postModel);

                                    }

                                }
                        }
                        Collections.reverse(postModels);
                        customAdapter_home = new CustomAdapter_Home(Home.this, postModels);
                        listView.setAdapter(customAdapter_home);


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


        //for retrieving current user details i.e name and profile image uri.
//        firebaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, String> map = dataSnapshot.getValue(Map.class);
//                increment = Integer.parseInt(String.valueOf(map.get("increment")));
//
//                //getting city of the current user
//                currentuser_city = map.get("city");
//
//                databaseReference_Searchcity.child(currentuser_city).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//                    @Override
//                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                        for (final com.google.firebase.database.DataSnapshot data : dataSnapshot.getChildren()) {
//
//
//                            databaseReference.child(data.getKey()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//                                @Override
//                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//
//                                    name = dataSnapshot.child("name").getValue(String.class);
//                                    profileImage = dataSnapshot.child("uri").getValue(String.class);
//                                    following = Integer.parseInt(String.valueOf(dataSnapshot.child("increment").getValue(Long.class)));
//
//                                        /*Map<Object, String> map = new HashMap<Object, String>();
//                                        map.put("" , String.valueOf(dataSnapshot.child("posts").getValue()));*/
//
//                                    Log.d("TAG1", String.valueOf(dataSnapshot.child("posts").child("posturi").getValue()));
//
////                                        names.add(dataSnapshot.child("name").getValue(String.class));
////                                        profileimage.add(dataSnapshot.child("uri").getValue(String.class));
//                                    databaseReference.child(data.getKey()).child("posts").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//
//                                            for (com.google.firebase.database.DataSnapshot data : dataSnapshot.getChildren()) {
//
//                                                String description = data.child("description").getValue(String.class);
//                                                String dateTime = data.child("dateTime").getValue(String.class);
//                                                String posturi = data.child("posturi").getValue(String.class);
//                                                String severity = data.child("severity").getValue(String.class);
//                                                String lat = data.child("lat").getValue(String.class);
//                                                String lon = data.child("lon").getValue(String.class);
//                                                String metadata = data.child("metadata").getValue(String.class);
//
//                                                    /*Log.d("TAG1", data.getKey().toString() );
//                                                    Log.d("TAG Name", String.valueOf(names.get(0)));*/
//
//
//                                                DataModel dataModel = new DataModel(userID, name, dateTime, description, profileImage, posturi, lon, lat, severity, data.getKey().toString(), metadata);
//                                                list.add(dataModel);
//
//
//                                            }
//
//                                            Collections.reverse(list);
//                                            adapter = new CustomAdapter(Home.this, list);
//                                            listView.setAdapter(adapter);
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


        //check if following exists and reteriving their data
//       databaseref_following_check.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//
//            String id, othername, picture;
//
//            @Override
//            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//
//                //  Log.d("DATASNAPSHOT", "1 "+dataSnapshot.toString());
//
//
//
//
//                for (com.google.firebase.database.DataSnapshot data : dataSnapshot.getChildren()) {
//                    id = data.getKey().toString();
//
//
//                    databaseReference.child(id).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//                        @Override
//                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//
//                            //Log.d("DATASNAPSHOT", "3"+);
//                            if (dataSnapshot.getValue() != null) {
//
//                              //  Log.d("DATASNAPSHOT", "2 "+ dataSnapshot.toString());
//                                othername = dataSnapshot.child("username").getValue(String.class);
//                                picture = dataSnapshot.child("uri").getValue(String.class);
//                                increment_following = Integer.parseInt(String.valueOf(dataSnapshot.child("increment").getValue(Long.class)));
//                                //final String radius = dataSnapshot.child("radius").getValue(String.class);
//                                final String radiuss = dataSnapshot.child("radius").getValue(String.class);
//                             // Toast.makeText(Home.this, "inc: " + increment_following, Toast.LENGTH_SHORT).show();
//                                Log.d("TAG", othername + increment_following + dataSnapshot.getKey());
//                              // databaseref_otheruser_post = databaseReference.child(dataSnapshot.getKey()).child("posts");
//                                if (increment_following == 0) {
//                                    //do nothing
//                                } else {
//                                   // Toast.makeText(Home.this, "else "+ id, Toast.LENGTH_SHORT).show();
//                                    final ArrayList<PostModel> postModels = new ArrayList<>();
//
//                                    databaseref_otheruser_post.child(dataSnapshot.getKey()+"/posts").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//
//                                            for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                                                String description = ds.child("description").getValue(String.class);
//                                                String dateTime = ds.child("dateTime").getValue(String.class);
//                                                String posturi = ds.child("posturi").getValue(String.class);
//                                                String severity = ds.child("severity").getValue(String.class);
//                                                String lat = ds.child("lat").getValue(String.class);
//                                                String lon = ds.child("lon").getValue(String.class);
//                                                String metadata = ds.child("metadata").getValue(String.class);
//
//                                               /* DataModel dataModel1 = new DataModel(id, othername, dateTime, description, picture, posturi, lon, lat, severity, ds.getKey().toString(), metadata, radius);
//                                                list.add(dataModel1);*/
//
//
//                                                PostModel postModel = new PostModel();
//                                                postModel.setId(id);
//                                                postModel.setName(othername);
//                                                postModel.setDateTime(ds.child("dateTime").getValue(String.class));
//                                                postModel.setDescription(ds.child("description").getValue(String.class));
//                                                postModel.setUri(picture);
//                                                postModel.setPosturi(ds.child("posturi").getValue(String.class));
//                                                postModel.setLon(ds.child("lat").getValue(String.class));
//                                                postModel.setLat(ds.child("lat").getValue(String.class));
//                                                postModel.setSeverity(ds.child("severity").getValue(String.class));
//                                                postModel.setPostid(ds.getKey().toString());
//                                                postModel.setMetadata(ds.child("metadata").getValue(String.class));
//                                                postModel.setRadius(radiuss);
//                                                postModels.add(postModel);
//                                               // Log.d("DATASNAPSHOT", "3 " + ds.child("posturi").getValue(String.class));
//
//
//                                            }
//
//                                            /*Collections.reverse(list);
//                                            adapter = new CustomAdapter(Home.this, list);
//                                            listView.setAdapter(adapter);*/
//                                            Collections.reverse(postModels);
//                                            customAdapter_home = new CustomAdapter_Home(Home.this, postModels);
//                                            listView.setAdapter(customAdapter_home);
//
//
//
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//
//
//                            }
//                        }
//
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });
//

        //For bottom Navigation bar
        BottomNavigation();

    }

    private void cameraIntent() {

        //Loading Post
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void videointent() {

        //Loading Post
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_VIDE0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Passing image to Post activity through intent
            Intent intent = new Intent(Home.this, Post.class);
            intent.putExtra("imageUri", imageBitmap);
            startActivity(intent);
            finish();
        }
        if (requestCode == REQUEST_VIDE0 && resultCode == RESULT_OK) {


            String videoUri = data.getDataString();

            Intent intent = new Intent(Home.this, Post.class);
            intent.putExtra("videouri", videoUri);
            startActivity(intent);
            finish();

        }


    }


    //Function for bottom navigation
    public void BottomNavigation() {

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Home:
                        Intent intent = new Intent(Home.this, Home.class);
                        startActivity(intent);
                        break;
                    case R.id.Search:
                        intent = new Intent(Home.this, Search.class);
                        startActivity(intent);
                        break;
                    case R.id.Camera:

                        listView.setVisibility(android.view.View.INVISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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
                                Intent intent1 = new Intent(getBaseContext(), Home.class);
                                startActivity(intent1);
                            }
                        });

                        builder.create();
                        builder.show();

                        break;
                    case R.id.Notification:
                        intent = new Intent(Home.this, Notification_View.class);
                        startActivity(intent);
                        break;
                    case R.id.Setting:
                        intent = new Intent(Home.this, com.example.bilalkhawaja.mishapalert.Utilities.Settings.class);
                        startActivity(intent);
                        break;

                }

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
