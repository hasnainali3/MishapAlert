package com.example.bilalkhawaja.mishapalert.Profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Utilities.Settings;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private TextView selectImage;
    EditText etName, etUName, etCity, etEmail;
    ImageView  ivSave,backArrow;
    SimpleDraweeView ivProfilePicture;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, databaseRef;
    private Firebase ref;

    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    ProgressDialog progressDialog;
    FirebaseUser user;

    //IMAGE HOLD URI
    Uri imageHoldUri = null;
    Uri downloadURI;
    String userID, usercity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();


        userID = user.getUid();
        ref = new Firebase("https://mishap-alert.firebaseio.com/Users/" + userID); //To avoid duplication in database we have used "UserID" in the link


        selectImage = (TextView) findViewById(R.id.tvUploadphoto);
        etName = (EditText) findViewById(R.id.etName);
        etUName = (EditText) findViewById(R.id.etUName);
        etCity = (EditText) findViewById(R.id.etCity);
        etEmail = (EditText) findViewById(R.id.etEmail);
        ivSave = (ImageView) findViewById(R.id.ivSave);
        backArrow = (ImageView)findViewById(R.id.backArrow);
        ivProfilePicture = (SimpleDraweeView) findViewById(R.id.ivProfilePicture);

        //Loading Profile
        LoadProfile();

        //Loading Image From Gallery
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profilePicSelection();


            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(EditProfile.this, Settings.class);
                finish();
                startActivity(inten);
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(etName.getText().toString()))
                {
                    etName.setError("Enter Name");
                }
                else if(TextUtils.isEmpty(etCity.getText().toString()))
                {
                    etCity.setError("Enter City");
                }
                else {
                    updateUsersProfile();
                }
            }
        });

    }

    //Fetching profile data from database
    public void LoadProfile() {
        final String uID = user.getUid();
        if (user != null) {

            ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                    Map<String, String> map = dataSnapshot.getValue(Map.class);

                    String name = map.get("name");
                    String username = map.get("username");
                    String city = map.get("city");
                    String email = map.get("email");
                    downloadURI = Uri.parse(map.get("uri"));
                    usercity = map.get("city");
                    etName.setText(name);
                    etUName.setText(username);
                    etCity.setText(city);
                    etEmail.setText(email);

                    ivProfilePicture.setImageURI(downloadURI);

                    /*try {
                        final File tmpFile = File.createTempFile("img", "png");
                        StorageReference reference = FirebaseStorage.getInstance().getReference("Photos");

                        //  "id" is name of the image file....


                        reference.child(uID).getFile(tmpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                //Bitmap image = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());

                                //Picasso.with(EditProfile.this).load(downloadURI).fit().centerCrop().into(ivProfilePicture);


                                //ivProfilePicture.setImageBitmap(image);

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


        }
    }


    private void profilePicSelection() {


        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
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
        if(resultCode == Activity.RESULT_CANCELED)
        {
            /*Intent i = new Intent(EditProfile.this, EditProfile.class);
            startActivity(i);
            finish();*/
        }
        else if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageHoldUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            //SAVE URI FROM CAMERA
            Uri imageUri = Uri.parse(data.getDataString());
            imageHoldUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        if(imageHoldUri!= null) {

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

    }


    //Updating User Profile information
    public void updateUsersProfile() {

       /* Toast.makeText(this, usercity + etCity.getText().toString(), Toast.LENGTH_SHORT).show();*/
        //check to see if user is logged in
        if (user != null) {
            //creating map for information to be stored
            String id = user.getUid();
            String name = etName.getText().toString();
            String userName = etUName.getText().toString();
            String city = etCity.getText().toString();

            if(usercity.toLowerCase().equals(city.toLowerCase())) {
                //putting values in map
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", id);
                imageInfo.put("name", name);
                imageInfo.put("username", userName);
                imageInfo.put("city", city);
                imageInfo.put("uri", downloadURI.toString());

                //saving values in database
                databaseReference.child(id).updateChildren(imageInfo);
                Toast.makeText(EditProfile.this, "Updated...!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            }
            else {

                //putting values in map
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", id);
                imageInfo.put("name", name);
                imageInfo.put("username", userName);
                imageInfo.put("city", city);
                imageInfo.put("uri", downloadURI.toString());

                //saving values in database
                databaseRef.child(usercity.toLowerCase()).child(id).removeValue();
                databaseRef.child(city.toLowerCase()).child(id).setValue("");
                databaseReference.child(id).updateChildren(imageInfo);
                Toast.makeText(EditProfile.this, "Updated...!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            }


        }
    }

}
