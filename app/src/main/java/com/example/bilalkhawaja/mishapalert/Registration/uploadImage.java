package com.example.bilalkhawaja.mishapalert.Registration;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class uploadImage extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;

    ImageView profilepicture;
    LinearLayout saveProfileBtn;

    private StorageReference storageReference;
    private DatabaseReference databaseReference, dataRef;
    private FirebaseUser user;
    private Firebase ref;

    //IMAGE HOLD URI
    Uri imageHoldUri = null;

    Uri downloaduri;

    //PROGRESS DIALOG
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);

        profilepicture = (ImageView) findViewById(R.id.userProfileImageView);

        saveProfileBtn = (LinearLayout) findViewById(R.id.saveProfile);
        user = FirebaseAuth.getInstance().getCurrentUser();

        String userID = user.getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        dataRef = FirebaseDatabase.getInstance().getReference("Users");

        ref = new Firebase("https://mishap-alert.firebaseio.com/Users/" + userID);

        //PROGRESS DIALOG
        mProgress = new ProgressDialog(this);

        //ONCLICK SAVE PROFILE BTN
        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LOGIC FOR SAVING USER PROFILE
                saveUserProfile();

            }
        });


        //USER IMAGEVIEW ONCLICK LISTENER
        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //LOGIC FOR PROFILE PICTURE
                profilePicSelection();

            }
        });


    }


    private void saveUserProfile() {

        final String uID = user.getUid();



            if( imageHoldUri != null )
            {

                mProgress.setTitle("Saving Image");
                mProgress.setMessage("Please wait....");
                mProgress.show();

                StorageReference mChildStorage = storageReference.child("Photos").child(uID);//.child(uID) For Creating Sub Folder
               // String profilePicUrl = imageHoldUri.getLastPathSegment();

                mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String username = getIntent().getStringExtra("username");

                        downloaduri = taskSnapshot.getDownloadUrl();

                        Map<String, Object> imageInfo = new HashMap<>();
                        imageInfo.put("uri", downloaduri.toString());

                        //saving values in database
                        databaseReference.updateChildren(imageInfo);
                        dataRef.child("usernames").child(username).updateChildren(imageInfo);


                        mProgress.dismiss();

                        finish();
                        Intent moveToHome = new Intent(uploadImage.this, Home.class);
                        moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(moveToHome);


                    }
                });
            }else
            {

                Toast.makeText(uploadImage.this, "Please select the profile pic", Toast.LENGTH_LONG).show();

            }

        }


    ///////////////////////////////////////////////////////////////

    private void profilePicSelection() {


        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(uploadImage.this);
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


        //SAVE URI FROM GALLERY
        if(requestCode == SELECT_FILE && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            imageHoldUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }else if ( requestCode == REQUEST_CAMERA && resultCode == RESULT_OK ){
            //SAVE URI FROM CAMERA

            Uri imageUri = data.getData();
            imageHoldUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }


        //image crop library code
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageHoldUri = result.getUri();

                profilepicture.setImageURI(imageHoldUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }








}
