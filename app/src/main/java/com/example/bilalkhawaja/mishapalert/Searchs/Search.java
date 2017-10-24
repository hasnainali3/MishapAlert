package com.example.bilalkhawaja.mishapalert.Searchs;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.CheckIntenet.CheckNetwork;
import com.example.bilalkhawaja.mishapalert.Followers.OtherUser;
import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.Notification.Notification_View;
import com.example.bilalkhawaja.mishapalert.Posts.Post;
import com.example.bilalkhawaja.mishapalert.Profiles.CustomAdapter;
import com.example.bilalkhawaja.mishapalert.Profiles.DataModel;
import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Utilities.BottomNavigationViewHelper;
import com.example.bilalkhawaja.mishapalert.Utilities.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.bilalkhawaja.mishapalert.R.id.Home;

public class Search extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 3;
    private static final int REQUEST_VIDE0 = 4;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    android.widget.SearchView searchView;
    ArrayList<DataModel> list;
    ListView listView;
    CustomAdapter adapter;
    ImageView imageView;
    TextView tvUsername;
    String id, currentuser_city, names, profileImage;
    Uri pic, pic2 = null;
    ArrayList<String> name = new ArrayList<String>();
    int following;
    RelativeLayout relativeLayout, body;
    LinearLayout linearlayout, linearlayout2;
    Toast toast;
    BottomNavigationView bottomNavigationView;
    FirebaseUser user;
    Button city, users;
    Spinner spFilter;
    //firebase
    private DatabaseReference databaseReference, databaseref, databaseReference_Searchcity, mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String[] types = {"Bombblast", "Earthquake", "Building Collapse", "Road Problem"};
        ArrayAdapter adap = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, types);

        listView = (ListView) findViewById(R.id.list);
        list = new ArrayList<>();

        toast = new Toast(Search.this);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        body = (RelativeLayout) findViewById(R.id.body);
        linearlayout = (LinearLayout) findViewById(R.id.linearlayout);
        searchView = (android.widget.SearchView) findViewById(R.id.svUser);
        imageView = (ImageView) findViewById(R.id.ivProfilePic);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        city = (Button)findViewById(R.id.city);
        users = (Button)findViewById(R.id.user);
        spFilter = (Spinner)findViewById(R.id.spFilter);
        linearlayout2 = (LinearLayout)findViewById(R.id.linearlayout2);

        //bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        spFilter.setAdapter(adap);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Arraylist for getting names

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference_Searchcity = FirebaseDatabase.getInstance().getReference();
        databaseref = databaseReference.child("usernames");
        relativeLayout.setEnabled(false);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot data : dataSnapshot.getChildren()) {

                    databaseref = databaseReference.child(data.getKey());
                    databaseref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name.add(dataSnapshot.child("username").getValue(String.class));
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


        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Search.this, currentuser_city , Toast.LENGTH_SHORT).show();
                linearlayout2.setVisibility(View.INVISIBLE);
                if (name.contains(currentuser_city)) {

                    mDatabase.child("Users")
                            .orderByChild("username")
                            .equalTo(currentuser_city)
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        id = childSnapshot.getKey();
                                        pic = Uri.parse(childSnapshot.child("uri").getValue(String.class));
                                        Picasso.with(getApplicationContext()).load(pic).fit().centerCrop().into(imageView);
                                        tvUsername.setText(currentuser_city);
                                        relativeLayout.setEnabled(true);
                                        linearlayout.setVisibility(View.VISIBLE);
                                        body.setVisibility(View.INVISIBLE);
                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                } else {
                    imageView.setImageResource(android.R.color.transparent);
                    tvUsername.setText("No User Found");
                    relativeLayout.setEnabled(false);
                    linearlayout.setVisibility(View.VISIBLE);
                    body.setVisibility(View.INVISIBLE);

                }



            }
        });

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                list.clear();

                if(currentuser_city != null) {
                    databaseReference_Searchcity.child(currentuser_city.toLowerCase()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                            for(final com.google.firebase.database.DataSnapshot data: dataSnapshot.getChildren())
                            {
                                databaseReference.child(data.getKey()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                                    @Override
                                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                        Log.d("onDataChange1: ", dataSnapshot.child("name").getValue(String.class));
                                        names = dataSnapshot.child("name").getValue(String.class);
                                        profileImage = dataSnapshot.child("uri").getValue(String.class);
                                        following = Integer.parseInt(String.valueOf(dataSnapshot.child("increment").getValue(Long.class)));


                                        databaseReference.child(data.getKey()+"/posts").orderByChild("type").equalTo(spFilter.getSelectedItem().toString()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                                            @Override
                                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {



                                                for(com.google.firebase.database.DataSnapshot data: dataSnapshot.getChildren())
                                                {

                                                    /*Log.d("data", data.toString());
                                                    Toast.makeText(Search.this, data.child("metadata").getValue(String.class) , Toast.LENGTH_SHORT).show();*/
                                                    String description = data.child("description").getValue(String.class);
                                                    String dateTime = data.child("dateTime").getValue(String.class);
                                                    String posturi = data.child("posturi").getValue(String.class);
                                                    String severity = data.child("severity").getValue(String.class);
                                                    String lat = data.child("lat").getValue(String.class);
                                                    String lon = data.child("lon").getValue(String.class);
                                                    String metadata = data.child("metadata").getValue(String.class);


                                                    DataModel dataModel = new DataModel(userID, names, dateTime, description, profileImage, posturi, lon, lat, severity, data.getKey().toString(), metadata);
                                                    list.add(dataModel);



                                                }

                                                Collections.reverse(list);
                                                adapter = new CustomAdapter(Search.this, list);
                                                listView.setAdapter(adapter);

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //to hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String type = String.valueOf(spFilter.getSelectedItem());

                body.setVisibility(View.VISIBLE);
                tvUsername.setText("");
                imageView.setImageResource(android.R.color.transparent);
                linearlayout2.setVisibility(View.VISIBLE);
                if(currentuser_city != null) {
                databaseReference_Searchcity.child(currentuser_city.toLowerCase()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        for(final com.google.firebase.database.DataSnapshot data: dataSnapshot.getChildren())
                        {
                            databaseReference.child(data.getKey()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                                @Override
                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                    Log.d("onDataChange1: ", dataSnapshot.child("name").getValue(String.class));
                                    names = dataSnapshot.child("name").getValue(String.class);
                                    profileImage = dataSnapshot.child("uri").getValue(String.class);
                                    following = Integer.parseInt(String.valueOf(dataSnapshot.child("increment").getValue(Long.class)));


                                    databaseReference.child(data.getKey()+"/posts").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                                        @Override
                                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {



                                            for(com.google.firebase.database.DataSnapshot data: dataSnapshot.getChildren())
                                            {

                                                String description = data.child("description").getValue(String.class);
                                                String dateTime = data.child("dateTime").getValue(String.class);
                                                String posturi = data.child("posturi").getValue(String.class);
                                                String severity = data.child("severity").getValue(String.class);
                                                String lat = data.child("lat").getValue(String.class);
                                                String lon = data.child("lon").getValue(String.class);
                                                String metadata = data.child("metadata").getValue(String.class);


                                                DataModel dataModel = new DataModel(userID, names, dateTime, description, profileImage, posturi, lon, lat, severity, data.getKey().toString(), metadata);
                                                list.add(dataModel);



                                            }

                                            Collections.reverse(list);
                                            adapter = new CustomAdapter(Search.this, list);
                                            listView.setAdapter(adapter);

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
                }
            }
        });


        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                if (CheckNetwork.isInternetAvailable(Search.this)) {

                    if (name.contains(query)) {

                        currentuser_city = query;

                        mDatabase.child("Users")
                                .orderByChild("username")
                                .equalTo(query)
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            id = childSnapshot.getKey();
                                            pic = Uri.parse(childSnapshot.child("uri").getValue(String.class));
                                            Picasso.with(getApplicationContext()).load(pic).fit().centerCrop().into(imageView);
                                            tvUsername.setText(query);
                                            relativeLayout.setEnabled(true);
                                            linearlayout.setVisibility(View.VISIBLE);
                                            body.setVisibility(View.INVISIBLE);
                                        }
                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });

                    } else {
                        imageView.setImageResource(android.R.color.transparent);
                        tvUsername.setText("No User Found");
                        relativeLayout.setEnabled(false);
                        currentuser_city = query;
                        linearlayout.setVisibility(View.VISIBLE);
                        body.setVisibility(View.INVISIBLE);


                    }


                } else {
                    Toast.makeText(Search.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userID.equals(id)) {
                    Intent intent = new Intent(Search.this, Profile.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Search.this, OtherUser.class);
                    intent.putExtra("UserID", id);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });


        //For bottom Navigation bar
        BottomNavigation();
    }


    private void cameraIntent() {

        //Loading camera
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
            Intent intent = new Intent(Search.this, Post.class);
            intent.putExtra("imageUri", imageBitmap);
            startActivity(intent);
            finish();
        }
        if (requestCode == REQUEST_VIDE0 && resultCode == RESULT_OK) {


            String videoUri = data.getDataString();
            Intent intent = new Intent(Search.this, Post.class);
            intent.putExtra("videouri", videoUri);
            startActivity(intent);
            finish();

        }


    }


    //Function for bottom navigation
    public void BottomNavigation() {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case Home:
                        Intent intent = new Intent(Search.this, Home.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.Search:
                        intent = new Intent(Search.this, Search.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.Camera:
                        searchView.setVisibility(View.INVISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
                        builder.setCancelable(false);
                        builder.setTitle("Camera Option");
                        builder.setPositiveButton("Picture", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Build.VERSION.SDK_INT >= 23) {
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
                                Intent intent1 = new Intent(getBaseContext(),Search.class);
                                startActivity(intent1);
                            }
                        });

                        builder.create();
                        builder.show();

                        break;
                    case R.id.Notification:
                        intent = new Intent(Search.this, Notification_View.class);
                        startActivity(intent);
                        break;
                    case R.id.Setting:
                        intent = new Intent(Search.this, Settings.class);
                        startActivity(intent);
                        finish();
                        break;

                }

                return true;
            }
        });
    }


    private void videointent() {

        //Loading Post
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,15);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_VIDE0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Search.this, Home.class);
        startActivity(intent);
        finish();
    }
}
