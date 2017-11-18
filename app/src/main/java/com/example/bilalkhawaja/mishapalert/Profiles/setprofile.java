package com.example.bilalkhawaja.mishapalert.Profiles;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.GPS.GPSTracker;
import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class setprofile extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    EditText etName, etUName, etCity;
    TextView tvuploadphoto;
    ImageView ivsave;
    SimpleDraweeView ivProfilePicture;
    int increment = 0, follower = 0, following = 0;
    String lat = "0.0" , lon = "0.0" ;
    Uri imageHoldUri = null;
    Uri downloadURI = Uri.parse("https://firebasestorage.googleapis.com/v0/b/mishap-alert.appspot.com/o/profilepicture.png?alt=media&token=b07a1751-d695-4bd0-949d-e2707739b5f1");
    FirebaseUser user;
    ProgressDialog progressDialog;
    String userID, email;
    GPSTracker gps;
    //Firebase
    private StorageReference storageReference;
    private DatabaseReference databaseReference, databaseref;
    ArrayList<String> name = new ArrayList<String>();
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setprofile);

        gps = new GPSTracker(setprofile.this);

        if(Build.VERSION.SDK_INT >= 23)
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED )
        {
            lat = String.valueOf(gps.getLatitude());
            lon = String.valueOf(gps.getLongitude());
        }
        else
        {
            String[] Permissionrequet = {Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(Permissionrequet, 10);

        }


        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        email = user.getEmail();
        ref = new Firebase("https://mishap-alert.firebaseio.com/Users/" + userID); //To avoid duplication in database we have used "UserID" in the link

        etName = (EditText) findViewById(R.id.etName);
        etUName = (EditText) findViewById(R.id.etUName);
        etCity = (EditText) findViewById(R.id.etCity);
        tvuploadphoto = (TextView) findViewById(R.id.tvUploadphoto);
        ivsave = (ImageView) findViewById(R.id.ivSave);
        ivProfilePicture = (SimpleDraweeView) findViewById(R.id.ivProfilePicture);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("DATA", data.toString());

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

        //Loading Image From Gallery
        tvuploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profilePicSelection();


            }
        });


        ivsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(etUName.getText())) {
                    etUName.setError("Enter Username");
                }
               else if(TextUtils.isEmpty(etName.getText()))
                {
                    etName.setError("Enter Name");
                }
               else if (TextUtils.isEmpty(etCity.getText()))
                {
                    etCity.setError("Enter City");
                }
                else
                {
                   SetProfile();
                }

            }
        });


    }

    private void profilePicSelection() {

        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(setprofile.this);
        builder.setTitle("Add Photo!");

        //SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        //CHOOSE CAMERA
        Log.d("gola", "entered here");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
        Log.d("gola", "entered here");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        //SAVE URI FROM GALLERY
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageHoldUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            //SAVE URI FROM CAMERA
            Uri imageUri = data.getData();
            imageHoldUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        //image crop library code
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageHoldUri = result.getUri();

                ivProfilePicture.setImageURI(imageHoldUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        StorageReference filepath = storageReference.child("Photos").child(userID);
        filepath.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadURI = taskSnapshot.getDownloadUrl();
                progressDialog.dismiss();

            }
        });
    }

    //Updating User Profile information
    public void SetProfile() {

        if(name.contains(etUName.getText().toString()))
        {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("Setting Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.create();
            progressDialog.show();


            //creating map for information to be stored
            String id = user.getUid();
            String name = etName.getText().toString();
            String userName = etUName.getText().toString();
            String city = etCity.getText().toString();


            //putting values in map
            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("id", id);
            imageInfo.put("name", name);
            imageInfo.put("email",email);
            imageInfo.put("username", userName);
            imageInfo.put("city", city);
            imageInfo.put("uri", downloadURI.toString());
            imageInfo.put("increment", increment);
            imageInfo.put("followers", follower);
            imageInfo.put("following", following);
         /*   imageInfo.put("latitude", lat);
            imageInfo.put("longitude", lon);*/
            imageInfo.put("radius", "10");

            //saving values in database
            databaseReference.child(id).updateChildren(imageInfo);
            Toast.makeText(setprofile.this, "Profile Completed", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            Intent intent = new Intent(setprofile.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(setprofile.this, "You can not go back without setting up your profile", Toast.LENGTH_SHORT).show();
    }
}
