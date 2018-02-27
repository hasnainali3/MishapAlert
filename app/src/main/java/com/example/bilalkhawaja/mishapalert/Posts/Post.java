package com.example.bilalkhawaja.mishapalert.Posts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bilalkhawaja.mishapalert.GPS.GPSTracker;
import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.Notification.MyVolley;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Utilities.AsyncResponse;
import com.example.bilalkhawaja.mishapalert.Utilities.VideoCompressAsyncTask;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.fitness.data.Goal;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.R.attr.data;
import static android.R.attr.handle;
import static android.R.attr.thickness;

public class Post extends AppCompatActivity implements AsyncResponse {
    private static final int MY_LOCATION_REQUEST_CODE = 100;
    MediaController media;
    GPSTracker gps;
    StorageReference storageReference;
    DatabaseReference databaseReference, dataref, databaseref_savenotification, databaseref_getnames, dataref_notification;
    FirebaseUser user;
    Firebase ref, firebaseref;
    Double[] latitude, longitude;
    float[] results = new float[1];
    String[] ids, id;
    int count;
    TextView tvCancel, metadata;
    ImageView cameraImage, ivSave, IvPlay;
    Bitmap bitmap;
    EditText etDescription;
    Uri downloaduri, imageuri;
    List<String> users, userids;
    JSONObject obj;
    // PROGRESS DIALOG
    ProgressDialog mProgress;
    String rbValue = null, title, description;
    RadioButton rbhigh, rbmedium, rblow;
    Spinner spinner;
    int increment;
    String currentDateTimeString;
    RadioGroup radioSeverity;
    String userID;
    String url, metaData = null;
    Double lat, lon, radius = 0.0;
    StringBuilder ab;
    com.google.firebase.database.DataSnapshot data1;
    VideoView video;
    String videouri, filePath;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    PhotoViewAttacher photoViewAttacher ;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        gps = new GPSTracker(Post.this);
        ab = new StringBuilder();

        media = new MediaController(this);
        url = "http://192.168.137.1/projectapi/index.php?";

        // url = "http://mihsapalert.000webhostapp.com/index.php?";

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        

        //getting time and date
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        String[] types = {"Select Incident/Disaster type:","Bombblast", "Earthquack", "Building collaps", "Road problem"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, types);


        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED & checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
            Toast.makeText(Post.this, lat + "....\n" + lon, Toast.LENGTH_SHORT).show();
        } else {
            String[] Permissionrequet = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(Permissionrequet, MY_LOCATION_REQUEST_CODE);
            //gps.showSettingsAlert();
        }


        cameraImage = (ImageView) findViewById(R.id.ivCameraImage);
        IvPlay = (ImageView) findViewById(R.id.IvPlay);
        video = (VideoView) findViewById(R.id.video);
        spinner = (Spinner) findViewById(R.id.spType);

        users = new ArrayList<>();
        userids = new ArrayList<>();
        spinner.setAdapter(adapter);


        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                IvPlay.setVisibility(View.INVISIBLE);
                video.start();
            }
        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                IvPlay.setVisibility(View.VISIBLE);
            }
        });

        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                IvPlay.setVisibility(View.VISIBLE);
                video.pause();

                return false;
            }
        });

        IvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IvPlay.setVisibility(View.INVISIBLE);
                video.start();
            }
        });
        //String userID = user.getUid();

        //Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        dataref = FirebaseDatabase.getInstance().getReference();
        dataref_notification = FirebaseDatabase.getInstance().getReference();
        databaseref_savenotification = FirebaseDatabase.getInstance().getReference();
        databaseref_getnames = FirebaseDatabase.getInstance().getReference("Users");
        ref = new Firebase("https://mishap-alert.firebaseio.com/Users/" + userID); //To avoid duplication in database we have used "UserID" in the link
        firebaseref = new Firebase("https://mishap-alert.firebaseio.com/Users");




        //video compression
        if (getIntent().getParcelableExtra("imageUri") != null) {
            //Loading image in image view from home activity
            cameraImage.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            bitmap = (Bitmap) intent.getParcelableExtra("imageUri");
            cameraImage.setImageBitmap(bitmap);
            photoViewAttacher = new PhotoViewAttacher(cameraImage);
            photoViewAttacher.update();

        } else {
            Toast.makeText(this, "Getting your Video ready for upload", Toast.LENGTH_SHORT).show();
            video.setVisibility(View.VISIBLE);
            IvPlay.setVisibility(View.VISIBLE);
            videouri = getIntent().getStringExtra("videouri");

            final File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/videos");
            f.mkdirs();
            VideoCompressAsyncTask videocom = new VideoCompressAsyncTask(Post.this);
            videocom.execute(videouri, f.getPath());

        }
        //////////////

        etDescription = (EditText) findViewById(R.id.etDescription);
        rbhigh = (RadioButton) findViewById(R.id.rbHigh);
        rbmedium = (RadioButton) findViewById(R.id.rbMedium);
        rblow = (RadioButton) findViewById(R.id.rbLow);
        ivSave = (ImageView) findViewById(R.id.ivSave);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        metadata = (TextView) findViewById(R.id.metadata);
        radioSeverity = (RadioGroup) findViewById(R.id.radioSeverity);

        //PROGRESS DIALOG
        mProgress = new ProgressDialog(this);

        //fetching increment value from database for post
        ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                Map<String, Integer> map = dataSnapshot.getValue(Map.class);
                increment = map.get("increment");

                // Toast.makeText(Post.this, "increment: " + increment, Toast.LENGTH_SHORT).show();
                Map<String, String> map1 = dataSnapshot.getValue(Map.class);
                radius = Double.valueOf(map1.get("radius"));
                Toast.makeText(Post.this, "Radius" + radius, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //Geo Fire getting all the users in a radius
        final GeoFire geoFire = new GeoFire(dataref.child("geofire"));
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (location != null) {

                    Log.d("TAG FOR GEOFIRE", "Key Entered: " + key + "\n At Locaton" + location.latitude + "" + location.longitude);
                    //Toast.makeText(Post.this, "Key Entered: " + key + "\n At Locaton" + location.latitude + "" + location.longitude, Toast.LENGTH_SHORT).show();
                    if (!key.equals(userID)) {
                        userids.add(key);
                    }
                    dataref_notification.child("Users").orderByChild("id").equalTo(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Log.d("TAG", data.toString());
                                // Toast.makeText(Post.this, data.child("FCM_TOKEN").getValue(String.class), Toast.LENGTH_SHORT).show();
                                users.add(data.child("FCM_TOKEN").getValue(String.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(Post.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {
                // Toast.makeText(Post.this, "Key Entered: " + key, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // Toast.makeText(Post.this, "Key Entered: " + key + "\n At Locaton" + location.latitude + "" + location.longitude, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onGeoQueryReady() {
                // Toast.makeText(Post.this, "All initial data has been loaded and events have been fired!", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(Post.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });


        //post Button
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveUserPost();


            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*
                try {
                    title = URLEncoder.encode(spinner.getSelectedItem().toString(), "UTF-8");
                    description = URLEncoder.encode(etDescription.getText().toString(), "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dataref_notification.child("Users").orderByChild("email").equalTo(dep(title)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.d("TAG", data.toString());
                            // Toast.makeText(Post.this, data.child("FCM_TOKEN").getValue(String.class), Toast.LENGTH_SHORT).show();
                            users.add(data.child("FCM_TOKEN").getValue(String.class));
                            userids.add(data.getKey());
//                            Toast.makeText(Post.this, "users "+users.size(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(Post.this, "ids " + userids.size() + "\n"+data.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


                AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
                builder.setCancelable(false);
                builder.setTitle("Cancel Post");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(Post.this, Home.class);
                        startActivity(intent);
                        finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create();
                builder.show();
            }
        });


    }


    private void saveUserPost() {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isGPSEnabled)
        {
            lat = gps.getLatitude();
            lon = gps.getLongitude();

            if(spinner.getSelectedItem().equals("Select"))
            {
                Toast.makeText(this, "Select disaster/incident type", Toast.LENGTH_SHORT).show();
            }
            else if(!rbhigh.isChecked() && !rblow.isChecked() && !rbmedium.isChecked()) {

                Toast.makeText(this, "Select severity of the disaster/incident", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    title = URLEncoder.encode(spinner.getSelectedItem().toString(), "UTF-8");
                    description = URLEncoder.encode(etDescription.getText().toString(), "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dataref_notification.child("Users").orderByChild("email").equalTo(dep(title)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.d("TAG", data.toString());
                            //Toast.makeText(Post.this, data.child("FCM_TOKEN").getValue(String.class), Toast.LENGTH_SHORT).show();
                            users.add(data.child("FCM_TOKEN").getValue(String.class));
                            userids.add(data.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                increment = increment + 1;
                mProgress.setMessage("Uploading ");
                mProgress.setCancelable(false);
                mProgress.create();
                mProgress.show();

                String profilePicUrl;
                byte[] data;
                StorageReference mChildStorage;

                if (getIntent().getParcelableExtra("imageUri") != null) {
                    profilePicUrl = bitmap.toString();
                    cameraImage.setDrawingCacheEnabled(true);
                    cameraImage.buildDrawingCache();
                    Bitmap bitmap = cameraImage.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();
                    mChildStorage = storageReference.child("Posts").child(profilePicUrl);
                    //final StorageReference finalMChildStorage = mChildStorage;
                    mChildStorage.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            ab.append("image");
                            downloaduri = taskSnapshot.getDownloadUrl();

                            Map<String, Object> imageInfo = new HashMap<>();
                            imageInfo.put("posturi", downloaduri.toString());
                            imageInfo.put("description", etDescription.getText().toString());


                            if (rbhigh.isChecked()) {
                                rbValue = rbhigh.getText().toString();
                            }
                            if (rbmedium.isChecked()) {
                                rbValue = rbmedium.getText().toString();
                            }
                            if (rblow.isChecked()) {
                                rbValue = rblow.getText().toString();
                            }


                            imageInfo.put("severity", rbValue);
                            imageInfo.put("dateTime", currentDateTimeString);
                            imageInfo.put("lat", lat.toString());
                            imageInfo.put("lon", lon.toString());
                            imageInfo.put("type", spinner.getSelectedItem());
                            // Toast.makeText(Post.this, "SB: " + ab.toString(), Toast.LENGTH_SHORT).show();
                            imageInfo.put("metadata", ab.toString());
                            imageInfo.put("postid", String.valueOf(increment));
                            imageInfo.put("fake", 0);

                            // Toast.makeText(Post.this, "increment" + increment, Toast.LENGTH_SHORT).show();

                            //saving data of post in database
                            databaseReference.child("posts").child(String.valueOf(increment)).updateChildren(imageInfo);

                            Map<String, Object> m = new HashMap<String, Object>();
                            m.put("ID", "reported");
                            databaseReference.child("posts").child(String.valueOf(increment)).child("report").updateChildren(m);

                            // saving value of increment for Posts
                            Map<String, Object> map = new HashMap<>();
                            map.put("increment", increment);
                            databaseReference.updateChildren(map);


                            //Toast.makeText(Post.this, "Length: " + users.size(), Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < users.size(); i++) {

                                //  Toast.makeText(Post.this, "IDS: " + users.get(i), Toast.LENGTH_SHORT).show();
                                url += "title=" + title;
                                url += "&message=" + description;
                                // url += "&image=" + imageuri;
                                url += "&userid=" + userID;
                                url += "&datetime=" + increment;
                                url += "&regId=" + users.get(i);


                                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            Log.e("Response ", response.getString("message"));
                                            // Toast.makeText(Post.this, "Response " + response.getString("message"), Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            //Toast.makeText(Post.this, "Json: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //  Log.d("ERROR: ",error.getMessage() );
                                        // Toast.makeText(Post.this, "error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                                    }

                                });

                                MyVolley.getInstance(Post.this).addToRequestQueue(objectRequest);


                            }

                            for (int i = 0; i < userids.size(); i++) {
                                Map<String, Object> notification = new HashMap<String, Object>();
                                notification.put("id", userID);
                                notification.put("title", spinner.getSelectedItem().toString());
                                notification.put("description", etDescription.getText().toString());

                                // Toast.makeText(Post.this, "i: " + i, Toast.LENGTH_SHORT).show();

                                databaseref_savenotification.child("Users/" + userids.get(i)).child("Notification").child(String.valueOf(increment)).updateChildren(notification);
                            }


                            Toast.makeText(Post.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                            Intent moveToHome = new Intent(Post.this, Home.class);
                            moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(moveToHome);
                            finish();
                        }
                    });
                } else {

                    mChildStorage = storageReference.child("Posts").child(videouri);

                    final StorageReference finalMChildStorage = mChildStorage;
                    mChildStorage.putFile(Uri.fromFile(new File(filePath))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ab.append("video");
                            downloaduri = taskSnapshot.getDownloadUrl();
                            Map<String, Object> imageInfo = new HashMap<>();
                            imageInfo.put("posturi", downloaduri.toString());
                            imageInfo.put("description", etDescription.getText().toString());

                            if (rbhigh.isChecked()) {
                                rbValue = rbhigh.getText().toString();
                            }
                            if (rbmedium.isChecked()) {
                                rbValue = rbmedium.getText().toString();
                            }
                            if (rblow.isChecked()) {
                                rbValue = rblow.getText().toString();
                            }
                            imageInfo.put("severity", rbValue);
                            imageInfo.put("dateTime", currentDateTimeString);
                            imageInfo.put("lat", lat.toString());
                            imageInfo.put("lon", lon.toString());
                            imageInfo.put("type", spinner.getSelectedItem());
                            imageInfo.put("metadata", ab.toString());
                            // Toast.makeText(Post.this, "increment" + increment, Toast.LENGTH_SHORT).show();

                            //saving data of post in database
                            databaseReference.child("posts").child(String.valueOf(increment)).updateChildren(imageInfo);

                            // saving value of increment for Posts
                            Map<String, Object> map = new HashMap<>();
                            map.put("increment", increment);
                            databaseReference.updateChildren(map);

                            //Toast.makeText(Post.this, "Length: " + users.size(), Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < users.size(); i++) {

                                // Toast.makeText(Post.this, "IDS: " + users.get(i), Toast.LENGTH_SHORT).show();
                                url += "title=" + title;
                                url += "&message=" + description;
                                url += "&image=" + imageuri;
                                url += "&userid=" + userID;
                                url += "&datetime=" + increment;
                                url += "&regId=" + users.get(i);


                                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            Log.e("Response ", response.getString("message"));
//                                    Toast.makeText(Post.this, "Response " + response.getString("message"), Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            //Toast.makeText(Post.this, "Json: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Log.d("ERROR: ",error.getMessage() );
                                        // Toast.makeText(Post.this, "error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                                    }

                                });

                                MyVolley.getInstance(Post.this).addToRequestQueue(objectRequest);

                            }

                            for (int i = 0; i < userids.size(); i++) {
                                Map<String, Object> notification = new HashMap<String, Object>();
                                notification.put("id", userID);
                                notification.put("title", spinner.getSelectedItem().toString());
                                notification.put("description", etDescription.getText().toString());

                                //  Toast.makeText(Post.this, "i: " + i, Toast.LENGTH_SHORT).show();

                                databaseref_savenotification.child("Users/" + userids.get(i)).child("Notification").child(String.valueOf(increment)).updateChildren(notification);
                            }


                            Toast.makeText(Post.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                            Intent moveToHome = new Intent(Post.this, Home.class);
                            moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(moveToHome);
                            finish();
                        }

                    });


                }

            }
        }
        else
        {
            gps.showSettingsAlert();
        }


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
        builder.setCancelable(false);
        builder.setTitle("Cancel Post");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(Post.this, Home.class);
                startActivity(intent);
                finish();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create();
        builder.show();
    }


    @Override
    public void processFinish(String output) {

        filePath = output;
        video.setVideoURI(Uri.parse(output));
        // Toast.makeText(Post.this, "File Path: " + output, Toast.LENGTH_SHORT).show();
    }


    public String dep(String dep) {
        String dept = null;
        if (dep.equals("Bombblast")) {
            dept = "hasnainali555@gmail.com";
        } else if (dep.equals("Earthquack")) {
            dept = "bilalkhawaja1313@gmail.com";
        } else if (dep.equals("Building collaps")) {
            dept = "bilalkhawaja1313@gmail.com";
        } else if (dep.equals("Road problem")) {
            dept = "bilalkhawaja1313@gmail.com";
        }

        return dept;
    }
}
